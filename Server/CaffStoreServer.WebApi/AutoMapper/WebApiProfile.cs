using AutoMapper;
using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Models.Responses;

namespace CaffStoreServer.WebApi.AutoMapper
{
    public class WebApiProfile : Profile
    {
        public WebApiProfile()
        {
            CreateMap<Comment, CommentResponse>();
            CreateMap<Caff, CAFFResponse>();
        }
    }
}
