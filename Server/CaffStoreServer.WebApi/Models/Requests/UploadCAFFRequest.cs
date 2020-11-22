using Microsoft.AspNetCore.Http;
using System.ComponentModel.DataAnnotations;

namespace CaffStoreServer.WebApi.Models.Requests
{
    public class UploadCAFFRequest
    {
        [Required]
        public string Name { get; set; }
        [Required]
        public double Price { get; set; }
        [Required]
        public IFormFile Image { get; set; }
    }
}
