using CaffStoreServer.WebApi.Models;
using System.Linq;
using System.Security.Claims;

namespace CaffStoreServer.WebApi.Extensions
{
    public static class ClaimsPrincipalExtensions
    {
        public static bool IsAdmin(this ClaimsPrincipal user)
        {
            return user?
                .HasClaim(c => c.Type == ClaimTypes.Role && c.Value == RoleConstants.AdminRoleName) ?? false;
        }

        public static string UserId(this ClaimsPrincipal user)
        {
            return user?.Claims.Where(c => c.Type == "userid").FirstOrDefault()?.Value;
        }
    }
}
