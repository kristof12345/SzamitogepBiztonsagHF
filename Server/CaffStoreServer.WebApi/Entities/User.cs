using Microsoft.AspNetCore.Identity;
using System.Collections.Generic;

namespace CaffStoreServer.WebApi.Entities
{
    public class User : IdentityUser<long> {
        public virtual ICollection<UserRole> UserRoles { get; set; }
        public virtual ICollection<IdentityUserClaim<long>> Claims { get; set; }
        public virtual ICollection<IdentityUserLogin<long>> Logins { get; set; }
        public virtual ICollection<IdentityUserToken<long>> Tokens { get; set; }
        public virtual ICollection<PurchasedCaff> PurchasedCaffs { get; set; }
    }
}
