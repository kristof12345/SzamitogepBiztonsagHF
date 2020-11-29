using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Exceptions;
using System;
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

        public static long UserId(this ClaimsPrincipal user)
        {
            try
            {
                var userIdStr = user?.Claims.Where(c => c.Type == "userid").FirstOrDefault()?.Value;
                return Convert.ToInt64(userIdStr);
            } catch
            {
                throw new AuthorizationException();
            }
            
        }
    }
}
