namespace CaffStoreServer.WebApi.Models.Responses
{
    public class RegisterResponse
    {
        public bool IsSuccess { get; set; }

        public long UserId { get; set; }

        public string Token { get; set; }

        public UserType UserType { get; set; }
    }
}
