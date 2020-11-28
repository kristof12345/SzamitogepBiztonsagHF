using CaffStoreServer.WebApi.Context;
using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Exceptions;
using CaffStoreServer.WebApi.Models.Requests;
using Microsoft.EntityFrameworkCore;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Services
{
    public class CaffService : ICaffService
    {
        private CaffStoreDbContext _context;
        private readonly IFileSettings _fileSettings;
        private readonly ICAFFparserSettings _parserSettings;
        private string[] permittedExtensions = { ".caff" };

        public CaffService(CaffStoreDbContext context, IFileSettings fileSettings, ICAFFparserSettings parserSettings)
        {
            _context = context;
            _fileSettings = fileSettings;
            _parserSettings = parserSettings;
        }

        public Task BuyAsync(string userId, long caffId)
        {
            return Task.CompletedTask;
        }

        public async Task<Caff> Create(Caff caff)
        {
            var result = _context.Caffs.Add(caff);
            await _context.SaveChangesAsync();
            return result.Entity;
        }

        public async Task<bool> Any()
        {
            return await _context.Caffs.AnyAsync();
        }

        public async Task Delete(long id)
        {
            var caff = await _context.Caffs.FirstOrDefaultAsync(c => c.Id == id);
            if (caff == null)
                throw new EntityNotFoundException("Caff not found");
            File.Delete(caff.ImageUrl);
            // TODO delete files generated with parser (thumbnails, json)
            _context.Remove(caff);
            await _context.SaveChangesAsync();
        }

        public async Task<byte[]> Download(string userId, long id)
        {
            var caff = await _context.Caffs.FirstOrDefaultAsync(c => c.Id == id);
            if (caff == null)
                throw new EntityNotFoundException("Caff not found");
            string filepath = caff.ImageUrl;
            byte[] filedata = await File.ReadAllBytesAsync(filepath);

            return filedata;
        }

        public async Task<IEnumerable<Caff>> SearchAsync(string userId, string creator, string title, bool? free, bool? bought)
        {
            //TODO: Filter with bought
            var caffs = _context.Caffs.Include(c => c.Comments).AsQueryable();

            if (!string.IsNullOrEmpty(creator))
                caffs = caffs.Where(c => c.Creator == creator);
            if (!string.IsNullOrEmpty(title))
                caffs = caffs.Where(c => c.Name == title);
            if (free == true)
                caffs = caffs.Where(c => c.Cost == 0);
            if (free == false)
                caffs = caffs.Where(c => c.Cost != 0);

            return await caffs.ToListAsync();
        }

        public async Task<Caff> Upload(string userId, UploadCAFFRequest request)
        {
            var ext = Path.GetExtension(request.Image.FileName).ToLowerInvariant();

            if (string.IsNullOrEmpty(ext) || !permittedExtensions.Contains(ext))
                throw new BadRequestException("Not supported file extension");
            if (request.Image.Length > _fileSettings.MaxSizeInMegaBytes * 1024 * 1024)
                throw new BadRequestException("Size limit reached");

            var extension = ".caff";
            Directory.CreateDirectory(_fileSettings.FilePath);
            var filePath = Path.Combine(_fileSettings.FilePath,
            Path.GetRandomFileName() + extension);

            using (var stream = File.Create(filePath))
            {
                await request.Image.CopyToAsync(stream);
            }

            var directory = filePath.Substring(0, filePath.Length - extension.Length);
            Directory.CreateDirectory(directory);

            string creator;
            DateTime creationDate;
            int duration = 0;
            string thumbnail = Path.Combine(directory, "1_img.bmp");
            try
            {
                var process = System.Diagnostics.Process.Start(_parserSettings.ParserExe, $"{filePath} {directory}");
                process.WaitForExit();

                var metadataJsonFile = Path.Combine(directory, "output.json");
                if (!File.Exists(metadataJsonFile))
                    throw new Exception("CAFF parsing did not produce metadata");

                if (!File.Exists(thumbnail))
                    throw new Exception("CAFF parsing did not produce thumbnail image");

                var jsonContent = File.ReadAllText(metadataJsonFile);
                dynamic jToken = JToken.Parse(jsonContent);

                var year = jToken.Credits.year.ToObject<int>();
                var month = jToken.Credits.month.ToObject<int>();
                var day = jToken.Credits.day.ToObject<int>();
                var hour = jToken.Credits.hour.ToObject<int>();
                var minute = jToken.Credits.minute.ToObject<int>();

                creator = jToken.Credits.creator;
                creationDate = new DateTime(
                    year,
                    month,
                    day,
                    hour,
                    minute,
                    0
                    );

                foreach (var anim in jToken.Animations)
                {
                    duration += anim.duration.ToObject<int>();
                }
            }
            catch
            {
                Directory.Delete(directory);
                throw;
            }

            var caff = new Caff
            {
                Name = request.Name,
                Cost = request.Price,
                ImageUrl = filePath,
                Creator = creator,
                CreationDate = creationDate.ToString(), // TODO DateTime?
                Duration = duration,
                ThumbnailUrl = thumbnail
            };
            return await Create(caff);
        }
    }
}
