using CaffStoreServer.WebApi.Context;
using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models.Exceptions;
using CaffStoreServer.WebApi.Models.Responses;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Services
{
    public class CommentService : ICommentService
    {
        private CaffStoreDbContext _context;

        public CommentService(CaffStoreDbContext context)
        {
            _context = context;
        }

        public async Task Add(long userId, long id, string text)
        {
            var comment = new Comment
            {
                AddTime = DateTime.Now,
                UserId = userId,
                CaffId = id,
                Text = text
            };

            _context.Comments.Add(comment);
            await _context.SaveChangesAsync();
        }

        public async Task Delete(long caffId, long commentId)
        {
            var caff = await _context.Caffs
                .Include(c => c.Comments)
                .FirstOrDefaultAsync(c => c.Id == caffId);
            if (caff == null)
                throw new EntityNotFoundException("Caff not found");
            var comment = caff.Comments.FirstOrDefault(c => c.Id == commentId);
            if (comment == null)
                throw new EntityNotFoundException("Comment not found");
            _context.Remove(comment);
            await _context.SaveChangesAsync();
        }

        public async Task<List<CommentResponse>> GetForCaffAsync(long id)
        {
            var caff = await _context.Caffs
                .Include(c => c.Comments)
                .FirstOrDefaultAsync(c => c.Id == id);
            var userIds = caff.Comments.Select(c => c.UserId);
            var userNameById = await _context.Users
                .Where(u => userIds.Contains(u.Id))
                .ToDictionaryAsync(u => u.Id, u => u.UserName);
            return caff.Comments.Select(c => new CommentResponse
            {
                Id = c.Id,
                UserName = userNameById.GetValueOrDefault(c.UserId) ?? "n/a",
                AddTime = c.AddTime.ToShortDateString() + " " + c.AddTime.ToString("H:mm:ss"), //Androidon jelenleg ilyen formában várja az időpontot
                Text = c.Text
            }).ToList();
        }
    }
}
