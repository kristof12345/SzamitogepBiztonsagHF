using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models.Requests;
using CaffStoreServer.WebApi.Models.Responses;
using CaffStoreServer.WebApi.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Security.Claims;

namespace CaffStoreServer.WebApi.Controllers
{
    [ApiController]
    [Route("caffs")]
    public class CAFFsController : ControllerBase
    {
        private readonly ICaffService _caffService;
        private readonly ICommentService _commentService;

        public CAFFsController(ICaffService caffService, ICommentService commentService)
        {
            _caffService = caffService;
            _commentService = commentService;
        }

        [HttpGet]
        [Authorize]
        public ActionResult<List<CAFFResponse>> Search([FromQuery] string creator, [FromQuery] string title, [FromQuery] bool free, [FromQuery] bool bought)
        {
            _caffService.SearchAsync(UserId(), creator, title, free, bought);

            var list = new List<CAFFResponse>();

            var caff1 = new CAFFResponse
            {
                Id = 1,
                Name = "This is caff 1",
                Duration = 10,
                Creator = "me",
                CreationDate = DateTime.Now.ToShortDateString() + " " + DateTime.Now.ToShortTimeString(), //Androidon jelenleg ilyen formában várja az időpontot
                Cost = 3.14,
                Bought = false,
                ImageUrl = "https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png",
                ThumbnailUrl = "https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png"
            };

            var caff2 = new CAFFResponse
            {
                Id = 2,
                Name = "This is caff 2",
                Duration = 20,
                Creator = "me",
                CreationDate = DateTime.Now.AddDays(-1).ToShortDateString() + " " + DateTime.Now.ToShortTimeString(), //Androidon jelenleg ilyen formában várja az időpontot
                Cost = 3.14,
                Bought = false,
                ImageUrl = "https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png",
                ThumbnailUrl = "https://media.sproutsocial.com/uploads/2017/02/10x-featured-social-media-image-size.png"
            };

            list.Add(caff1);
            list.Add(caff2);

            return Ok(list);
        }

        [HttpGet]
        [Route("{id}/comments")]
        public ActionResult<List<CommentResponse>> GetComments([FromRoute] string id)
        {
            //TODO: Authorization token from header
            var list = new List<CommentResponse>();

            var comment = new CommentResponse
            {
                Id = 23,
                UserName = "me",
                Text = "Hello world!",
                AddTime = DateTime.Now.ToShortDateString() + " " + DateTime.Now.ToShortTimeString(), //Androidon jelenleg ilyen formában várja az időpontot
            };

            list.Add(comment);

            return Ok(list);
        }

        [HttpPost]
        [Route("{id}/buy")]
        public ActionResult Buy([FromRoute] string id)
        {
            //TODO: Authorization token from header

            return Ok();
        }

        [HttpPost]
        [Route("{id}/comments")]
        public ActionResult Comment([FromRoute] string id)
        {
            //TODO: Authorization token from header

            return Ok();
        }

        [HttpPost]
        [Route("{name}/{price}")]
        public ActionResult<CommentResponse> UploadImage(IFormFile file, [FromRoute] string name, [FromRoute] string price)
        {
            //TODO: Authorization token from header

            var request = new UploadCAFFRequest
            {
                Image = file,
                Name = name,
                Price = double.Parse(price, CultureInfo.InvariantCulture)
            };

            var response = new CAFFResponse();
            return Ok(response);
        }

        [HttpGet]
        [Route("{id}/download")]
        public ActionResult<IFormFile> DownloadImage([FromRoute] string id)
        {
            //TODO: Authorization token from header

            //TODO: return the file
            return Ok();
        }

        [Authorize]
        [HttpDelete]
        [Route("{id}")]
        public ActionResult<IFormFile> DeleteCaff([FromRoute] string id)
        {
            if (!IsAdmin())
            {
                return Unauthorized();
            }

            return Ok();
        }

        [Authorize]
        [HttpDelete]
        [Route("{caffId}/comments/{commentId}")]
        public ActionResult<IFormFile> DeleteComment([FromRoute] string caffId, [FromRoute] string commentId)
        {
            if (!IsAdmin())
            {
                return Unauthorized();
            }

            return Ok();
        }

        private bool IsAdmin()
        {
            return User.HasClaim(c => c.Type == ClaimTypes.Role && c.Value == "Administrator");
        }

        private string UserId()
        {
            return User.Claims.Where(c => c.Type == "userid").FirstOrDefault().Value;
        }
    }
}
