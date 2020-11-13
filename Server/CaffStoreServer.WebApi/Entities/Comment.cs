namespace CaffStoreServer.WebApi.Entities
{
    public class Comment
    {
        public long Id { get; set; }
        public string UserName { get; set; }
        public string AddTime { get; set; }
        public string Text { get; set; }
    }
}
