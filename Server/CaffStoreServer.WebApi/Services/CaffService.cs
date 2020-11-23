using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models.Requests;
using Microsoft.AspNetCore.Http;
using System;
using System.Collections.Generic;
using System.IO;
using System.Net.Mime;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Services
{
    public class CaffService : ICaffService
    {
        public Task BuyAsync(string userId, string caffId)
        {
            return Task.CompletedTask;
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
            var list = new List<Caff> {
                new Caff
                {
                    Id = 1,
                    Name = "This is caff 1",
                    Duration = 10,
                    Creator = "me",
                    CreationDate = DateTime.Now.ToShortDateString() + " " + DateTime.Now.ToShortTimeString(), //Androidon jelenleg ilyen formában várja az időpontot
                    Cost = 3.14,
                    Bought = false,
                    ImageUrl = "https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png",
                    ThumbnailUrl = "https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png"
                },
                new Caff
                {
                    Id = 2,
                    Name = "This is caff 2",
                    Duration = 20,
                    Creator = "me",
                    CreationDate = DateTime.Now.AddDays(-1).ToShortDateString() + " " + DateTime.Now.ToShortTimeString(), //Androidon jelenleg ilyen formában várja az időpontot
                    Cost = 3.14,
                    Bought = false,
                    ImageUrl = "https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png",
                    ThumbnailUrl = "https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png"
                }
            };
            return await Task.FromResult(list);
        }

        public Task Upload(string userId, UploadCAFFRequest request)
        {
            throw new NotImplementedException();
        }
    }
}
