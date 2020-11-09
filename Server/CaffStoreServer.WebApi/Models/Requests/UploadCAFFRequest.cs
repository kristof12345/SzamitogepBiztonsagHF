using Microsoft.AspNetCore.Http;

namespace CaffStoreServer.WebApi.Models.Requests
{
    public class UploadCAFFRequest
    {
        public string Name { get; set; }

        public double Price { get; set; }

        public IFormFile Image { get; set; }
    }
}
