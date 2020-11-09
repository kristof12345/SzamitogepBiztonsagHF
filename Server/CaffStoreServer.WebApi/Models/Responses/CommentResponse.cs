using System;

namespace CaffStoreServer.WebApi.Models.Responses
{
    public class CommentResponse
    {
        public long Id { get; set; }

        public string UserName { get; set; }

        public DateTime AddTime { get; set; }

        public string Text { get; set; }
    }
}
