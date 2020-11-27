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

        public async Task<byte[]> Download(string v, string id)
        {
            string filename = "1.caff";
            string filepath = AppDomain.CurrentDomain.BaseDirectory + "/" + filename;
            byte[] filedata = await File.ReadAllBytesAsync(filepath);

            return filedata;
        }

        public async Task<IEnumerable<Caff>> SearchAsync(string userId, string creator, string title, bool free, bool bought)
        {
            //TODO: Apply filters
            return await _context.Caffs.Include(c => c.Comments).ToListAsync();
        }

        public async Task Upload(string userId, UploadCAFFRequest request)
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

            //TODO: Save file, call create()
        }
    }
}
