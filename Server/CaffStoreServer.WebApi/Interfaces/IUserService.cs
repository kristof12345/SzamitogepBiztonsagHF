using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Models.Requests;
using CaffStoreServer.WebApi.Models.Responses;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Interfaces
{
    public interface IUserService
    {
        Task<IEnumerable<User>> GetAsync();
        Task<User> GetByIdAsync(long id);
        Task<RegisterResponse> CreateUserAsync(RegisterRequest request);
        Task UpdateAsync(UpdateRequest request);
        Task DeleteAsync(long id);
    }
}
