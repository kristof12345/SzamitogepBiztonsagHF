using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Interfaces;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Services
{
    public class CommentService : ICommentService
    {
        public Task Add(string userId, long id, string text)
        {
            throw new NotImplementedException();
        }

        public Task Delete(long caffId, string commentId)
        {
            throw new NotImplementedException();
        }

        public async Task<List<Comment>> GetForCaffAsync(long id)
        {
            var list = new List<Comment>
            {
                new Comment
                {
                    Id = "23",
                    CaffId = id,
                    UserName = "me",
                    Text = "Hello world!",
                    AddTime = DateTime.Now.ToShortDateString() + " " + DateTime.Now.ToShortTimeString(), //Androidon jelenleg ilyen formában várja az időpontot
                }
            };
            return await Task.FromResult(list);
        }
    }
}
