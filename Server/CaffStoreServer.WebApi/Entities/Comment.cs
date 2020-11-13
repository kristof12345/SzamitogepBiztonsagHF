namespace CaffStoreServer.WebApi.Entities
{
    public class Comment
    {
        public string Id { get; set; }
        public string CaffId { get; set; }
        public string UserName { get; set; }
        public string AddTime { get; set; }
        public string Text { get; set; }
    }
}
