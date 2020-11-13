namespace CaffStoreServer.WebApi.Models.Requests
{
    public class UpdateRequest
    {
        public long UserId { get; set; }

        public string Username { get; set; }

        public string Email { get; set; }

    }
}
