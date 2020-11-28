using System;
using System.Collections.Generic;

namespace CaffStoreServer.WebApi.Entities
{
    public class Caff
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public DateTime CreationDate { get; set; }
        public string Creator { get; set; }
        public int Duration { get; set; }
        public virtual ICollection<Thumbnail> Thumbnails { get; set; }
        public double Cost { get; set; }
        public virtual ICollection<Comment> Comments { get; set; }
        public string ImagePath { get; set; }
    }
}
