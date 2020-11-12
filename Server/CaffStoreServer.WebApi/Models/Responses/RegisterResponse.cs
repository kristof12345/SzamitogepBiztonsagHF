using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Models.Responses
{
    public class RegisterResponse
    {
        public bool IsSuccess { get; set; }

        public long UserId { get; set; }

        public string Token { get; set; }
    }
}
