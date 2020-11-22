using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Models;

namespace CaffStoreServer.WebApi.Interfaces
{
    public interface ITokenService
    {
        public string GenerateToken(User user, int expireMinutes = 60);
    }
}
