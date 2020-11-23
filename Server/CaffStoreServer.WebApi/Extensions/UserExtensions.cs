using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Models;
using System.Linq;

namespace CaffStoreServer.WebApi.Extensions
{
    public static class UserExtensions
    {
        public static bool IsAdmin(this User user)
        {
            return user?
                .UserRoles?
                .Any(ur => ur.Role.NormalizedName == RoleConstants.AdminNormalizedRoleNome) ?? false;
        }
    }
}
