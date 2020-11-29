using Microsoft.AspNetCore.Identity;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Entities
{
    public class UserRole : IdentityUserRole<long>
    {
        public virtual User User { get; set; }
        public virtual Role Role { get; set; }
    }
}
