using System.Collections.Generic;
using System.Threading.Tasks;
using CaffStoreServer.WebApi.Entities;

namespace CaffStoreServer.WebApi.Interfaces
{
    public interface ICaffService
    {
        Task<IEnumerable<Caff>> SearchAsync(string userId, string creator, string title, bool free, bool bought);
    }
}
