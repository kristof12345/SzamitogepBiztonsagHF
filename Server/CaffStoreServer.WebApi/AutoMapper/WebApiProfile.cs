using AutoMapper;
using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Responses;
using System.Linq;

namespace CaffStoreServer.WebApi.AutoMapper
{
    public class WebApiProfile : Profile
    {
        public WebApiProfile()
        {
            CreateMap<Comment, CommentResponse>();
            CreateMap<Caff, CAFFResponse>();
            CreateMap<User, UserDTO>()
                .AfterMap((entity, dto, ctx) =>
                {
                    dto.UserType = entity.UserRoles
                        .Any(ur => ur.Role.NormalizedName == RoleConstants.AdminNormalizedRoleNome) ? UserType.Admin : UserType.User;
                });
        }
    }
}
