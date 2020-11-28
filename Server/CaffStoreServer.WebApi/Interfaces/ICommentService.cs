using CaffStoreServer.WebApi.Entities;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Interfaces
{
    public interface ICommentService
    {
        Task<List<Comment>> GetForCaffAsync(long id);
        Task Add(long userId, long id, string text);
        Task Delete(long caffId, string commentId);
    }
}
