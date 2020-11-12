namespace CaffStoreServer.WebApi.Models.Requests
{
    public class LoginRequest
    {
        public string Username { get; set; }

        public string Password { get; set; }

        public long UserId { get; set; }
    }
}
