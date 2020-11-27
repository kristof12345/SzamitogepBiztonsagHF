using System.ComponentModel.DataAnnotations;

namespace CaffStoreServer.WebApi.Models.Requests
{
    public class CommentRequest
    {
        [Required]
        public string Text { get; set; }
    }
}
