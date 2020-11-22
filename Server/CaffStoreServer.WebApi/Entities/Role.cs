using Microsoft.AspNetCore.Identity;
using System.Collections.Generic;

namespace CaffStoreServer.WebApi.Entities
{
    public class Role : IdentityRole<long>
    {
        public virtual ICollection<UserRole> UserRoles { get; set; }
    }
}
