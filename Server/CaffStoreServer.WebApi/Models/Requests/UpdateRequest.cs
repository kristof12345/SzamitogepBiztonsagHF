using System.ComponentModel.DataAnnotations;

namespace CaffStoreServer.WebApi.Models.Requests
{
    public class UpdateRequest
    {
        [Required]
        public long UserId { get; set; }
        [Required]
        public string Username { get; set; }
        [Required]
        [EmailAddress]
        public string Email { get; set; }

    }
}
