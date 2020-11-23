using AutoMapper;
using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Extensions;
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
                    dto.UserType = entity.IsAdmin() ? UserType.Admin : UserType.User;
                });
        }
    }
}
