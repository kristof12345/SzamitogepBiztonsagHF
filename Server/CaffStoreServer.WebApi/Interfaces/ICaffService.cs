using System.Collections.Generic;
using System.Threading.Tasks;
using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Models.Requests;

namespace CaffStoreServer.WebApi.Interfaces
{
    public interface ICaffService
    {
        Task<IEnumerable<Caff>> SearchAsync(string userId, string creator, string title, bool? free, bool? bought);
        Task BuyAsync(string userId, string caffId);
        Task<Caff> Upload(string userId, UploadCAFFRequest request);
        Task<byte[]> Download(string userId, int id);
        Task Delete(string id);
        Task<Caff> Create(Caff caff);
        Task<bool> Any();
    }
}
