using System.Collections.Generic;
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
        public virtual ICollection<Thumbnail> Thumbnails { get; set; }
        public double Cost { get; set; }
        public virtual ICollection<Comment> Comments { get; set; }
        public string ImagePath { get; set; }
    }
}
