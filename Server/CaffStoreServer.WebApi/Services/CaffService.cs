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

        public async Task BuyAsync(long userId, long caffId)
        {
            var user = await _context.Users
                .Include(u => u.PurchasedCaffs)
                .FirstOrDefaultAsync(u => u.Id == userId);
            if (user == null)
                throw new UserNotFoundException();
            if (user.PurchasedCaffs.Any(pc => pc.CaffId == caffId))
                throw new BadRequestException("Caff already purchased");
            var caff = await _context.Caffs.FirstOrDefaultAsync(c => c.Id == caffId);
            if (caff == null)
                throw new BadRequestException("Not existing caff");
            user.PurchasedCaffs.Add(new PurchasedCaff
            {
                User = user,
                Caff = caff,
                PurchasedOn = DateTime.UtcNow
            });
            await _context.SaveChangesAsync();
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
            var caff = await _context.Caffs.Include(c => c.Thumbnails).FirstOrDefaultAsync(c => c.Id == id);

            if (caff == null)
                throw new EntityNotFoundException("Caff not found");
            File.Delete(caff.ImagePath);
            foreach (var thumbnail in caff.Thumbnails)
            {
                File.Delete(thumbnail.FilePath);
            }
            var extension = ".caff";
            var directory = caff.ImagePath.Substring(0, caff.ImagePath.Length - extension.Length);
            Directory.Delete(directory, true);

            await _context.SaveChangesAsync();
        }

        public async Task<byte[]> Download(long userId, long id)
        {
            var user = await _context.Users
                .Include(u => u.PurchasedCaffs)
                .FirstOrDefaultAsync(u => u.Id == userId);
            if (user == null)
                throw new UserNotFoundException("User not found");
            if (!user.PurchasedCaffs.Any(pc => pc.CaffId == id))
                throw new BadRequestException("Caff not purchased");
            var caff = await _context.Caffs.FirstOrDefaultAsync(c => c.Id == id);
            if (caff == null)
                throw new EntityNotFoundException("Caff not found");
            string filepath = caff.ImagePath;
            byte[] filedata = await File.ReadAllBytesAsync(filepath);

            return filedata;
        }

        public async Task<byte[]> DownloadThumbnail(long caffId, long thumbnailId)
        {
            var caff = await _context.Caffs.Include(c => c.Thumbnails).FirstOrDefaultAsync(c => c.Id == caffId);
            if (caff == null)
                throw new EntityNotFoundException("Caff not found");
            string filepath = caff.Thumbnails.FirstOrDefault(th => th.Id == thumbnailId)?.FilePath;
            if (filepath == null)
                throw new EntityNotFoundException("Thumbnail not found");
            byte[] filedata = await File.ReadAllBytesAsync(filepath);

            return filedata;
        }

        public async Task<(IEnumerable<Caff>, IEnumerable<long>)> SearchAsync(long userId, string creator, string title, bool free, bool bought)
        {
            var caffs = _context.Caffs
                .Include(c => c.Comments)
                .Include(c => c.Thumbnails)
                .AsQueryable();
            var purchasedCaffIds = await _context.PurchasedCaffs
                .Where(pc => pc.UserId == userId)
                .Select(pc => pc.CaffId)
                .ToListAsync();

            if (!string.IsNullOrEmpty(creator))
                caffs = caffs.Where(c => c.Creator.ToLower().Contains(creator.ToLower()));
            if (!string.IsNullOrEmpty(title))
                caffs = caffs.Where(c => c.Name.ToLower().Contains(title.ToLower()));
            if (free == true)
                caffs = caffs.Where(c => c.Cost == 0);
            if (bought == true)
                caffs = caffs.Where(c => purchasedCaffIds.Contains(c.Id));

            var list = await caffs.ToListAsync();

            return (list, purchasedCaffIds);
        }

        public async Task<Caff> Upload(long userId, UploadCAFFRequest request)
        {
            // TODO What to do with userId?
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

            var thumbnails = new List<Thumbnail>();
            foreach (var file in Directory.GetFiles(directory, "*.bmp"))
            {
                thumbnails.Add(new Thumbnail
                {
                    FilePath = file
                });
            }

            var caff = new Caff
            {
                Name = request.Name,
                Cost = request.Price,
                ImagePath = filePath,
                Creator = creator,
                CreationDate = creationDate,
                Duration = duration,
                Thumbnails = thumbnails
            };
            return await Create(caff);
        }
    }
}
