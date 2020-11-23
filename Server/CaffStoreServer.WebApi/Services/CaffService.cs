using CaffStoreServer.WebApi.Context;
using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Interfaces;
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

        public CaffService(CaffStoreDbContext context)
        {
            _context = context;
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

        public Task Upload(string userId, UploadCAFFRequest request)
        {
            //TODO: Save file, call create()
            throw new NotImplementedException();
        }
    }
}
