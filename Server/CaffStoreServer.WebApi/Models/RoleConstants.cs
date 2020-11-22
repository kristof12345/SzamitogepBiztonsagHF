using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Models
{
    public static class RoleConstants
    {
        public static string AdminRoleName { get; } = "Administrator";
        public static string UserRoleName { get; } = "User";
        public static string AdminNormalizedRoleNome { get; } = AdminRoleName.ToUpper();
        public static string UserNormalizedRoleName { get; } = UserRoleName.ToUpper();
    }
}
