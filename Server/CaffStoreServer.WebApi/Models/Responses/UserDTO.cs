namespace CaffStoreServer.WebApi.Models.Responses
{
    public class UserDTO
    {
        public long Id { get; set; }
        public string Username { get; set; }
        public string Email { get; set; }
        public UserType UserType { get; set; }
    }
}
