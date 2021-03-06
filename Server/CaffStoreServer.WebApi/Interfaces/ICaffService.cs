﻿using System.Collections.Generic;
using System.Threading.Tasks;
using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Models.Requests;

namespace CaffStoreServer.WebApi.Interfaces
{
    public interface ICaffService
    {
        Task<(IEnumerable<Caff>, IEnumerable<long>)> SearchAsync(long userId, string creator, string title, bool free, bool bought);
        Task BuyAsync(long userId, long caffId);
        Task<Caff> Upload(long userId, UploadCAFFRequest request);
        Task<byte[]> Download(long userId, long id);
        Task Delete(long id);
        Task<Caff> Create(Caff caff);
        Task<bool> Any();
        Task<byte[]> DownloadThumbnail(long caffId, long thumbnailId);
    }
}
