using CaffStoreServer.WebApi.Context;
using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Exceptions;
using CaffStoreServer.WebApi.Models.Requests;
using Microsoft.EntityFrameworkCore;
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
        private string[] permittedExtensions = { ".caff" };

        public CaffService(CaffStoreDbContext context, IFileSettings fileSettings)
        {
            _context = context;
            _fileSettings = fileSettings;
        }

        public Task BuyAsync(string userId, string caffId)
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

        public Task Delete(string id)
        {
            throw new NotImplementedException();
        }

        public async Task<byte[]> Download(string userId, int id)
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
            var caffs = _context.Caffs.Include(c => c.Comments)
                .Where(c => string.IsNullOrEmpty(creator) || c.Creator == creator)
                .Where(c => string.IsNullOrEmpty(title) || c.Name == title);

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

            Directory.CreateDirectory(_fileSettings.FilePath);
            var filePath = Path.Combine(_fileSettings.FilePath,
            Path.GetRandomFileName() + ".caff");

            using (var stream = File.Create(filePath))
            {
                await request.Image.CopyToAsync(stream);
            }

            // TODO parse CAFF file

            var caff = new Caff
            {
                Name = request.Name,
                Cost = request.Price,
                ImageUrl = filePath
                // TODO fill properties after parsing with data from the json
            };
            return await Create(caff);
        }
    }
}
