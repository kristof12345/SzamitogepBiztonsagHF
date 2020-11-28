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
            CreateMap<Comment, CommentResponse>()
                .ForMember(c => c.AddTime, opt => opt.Ignore())
                .AfterMap((entity, dto, ctx) =>
                {
                    dto.AddTime = entity.AddTime.ToShortDateString() + " " + entity.AddTime.ToString("H:mm:ss"); //Androidon jelenleg ilyen formában várja az időpontot
                });
            CreateMap<Caff, CAFFResponse>()
                .ForMember(c => c.CreationDate, opt => opt.Ignore())
                .AfterMap((entity, dto, ctx) =>
                {
                    dto.CreationDate = entity.CreationDate.ToShortDateString() + " " + entity.CreationDate.ToString("H:mm:ss"); //Androidon jelenleg ilyen formában várja az időpontot
                });
            CreateMap<User, UserDTO>()
                .AfterMap((entity, dto, ctx) =>
                {
                    dto.UserType = entity.IsAdmin() ? UserType.Admin : UserType.User;
                });
        }
    }
}
