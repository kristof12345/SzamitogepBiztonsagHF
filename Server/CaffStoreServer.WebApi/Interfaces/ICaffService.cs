using System.Collections.Generic;
using System.Threading.Tasks;
using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Models.Requests;

namespace CaffStoreServer.WebApi.Interfaces
{
    public interface ICaffService
    {
        Task<IEnumerable<Caff>> SearchAsync(string userId, string creator, string title, bool free, bool bought);
        Task<IEnumerable<Caff>> BuyAsync(string userId, string caffId);
        Task Upload(string userId, UploadCAFFRequest request);
        Task<byte[]> Download(string v, string id);
        Task Delete(string id);
    }
}
