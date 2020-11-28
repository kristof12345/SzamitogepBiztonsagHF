namespace CaffStoreServer.WebApi.Entities
{
    public class Thumbnail
    {
        public long Id { get; set; }
        public string FilePath { get; set; }
        public long CaffId { get; set; }
        public virtual Caff Caff { get; set; }
    }
}
