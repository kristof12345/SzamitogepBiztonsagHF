using System.ComponentModel.DataAnnotations.Schema;

namespace CaffStoreServer.WebApi.Entities
{
    public class Caff
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public string CreationDate { get; set; }
        public string Creator { get; set; }
        public int Duration { get; set; }
        public string ThumbnailUrl { get; set; }
        public double Cost { get; set; }
        [NotMapped]
        public bool Bought { get; set; } //It's not stored in db, because it only refers to a specific user
        public string ImageUrl { get; set; }
    }
}
