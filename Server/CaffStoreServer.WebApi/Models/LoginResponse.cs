namespace CaffStoreServer.WebApi.Models
{
    public class LoginResponse
    {
        public bool IsSuccess { get; set; }

        public long UserId { get; set; }

        public string Token { get; set; }

        public UserType UserType { get; set; }
    }
}
