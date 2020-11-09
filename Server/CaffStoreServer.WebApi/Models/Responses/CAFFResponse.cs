using System;

namespace CaffStoreServer.WebApi.Models.Responses
{
    public class CAFFResponse
    {
        public long Id { get; set; }

        public string Name { get; set; }

        public string CreationDate { get; set; }

        public string Creator { get; set; }

        public int Duration { get; set; }

        public string ThumbnailUrl { get; set; }

        public double Cost { get; set; }

        public bool Bought { get; set; }

        public string ImageUrl { get; set; }
    }
}
