using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Models.Requests
{
    public class UpdateRequest
    {
        public long UserId { get; set; }

        public string Username { get; set; }

        public string Email { get; set; }

    }
}
