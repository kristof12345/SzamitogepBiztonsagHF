using CaffStoreServer.WebApi.Models.Responses;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Interfaces
{
    public interface ICommentService
    {
        Task<List<CommentResponse>> GetForCaffAsync(long id);
        Task Add(long userId, long id, string text);
        Task Delete(long caffId, long commentId);
    }
}
