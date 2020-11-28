using AutoMapper;
using CaffStoreServer.WebApi.Extensions;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models.Requests;
using CaffStoreServer.WebApi.Models.Responses;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Controllers
{
    [ApiController]
    [Route("caffs")]
    public class CAFFsController : ControllerBase
    {
        private readonly ICaffService _caffService;
        private readonly ICommentService _commentService;
        private readonly IMapper _mapper;

        public CAFFsController(ICaffService caffService, ICommentService commentService, IMapper mapper)
        {
            _caffService = caffService;
            _commentService = commentService;
            _mapper = mapper;
        }

        [Authorize]
        [HttpGet]
        public async Task<ActionResult<List<CAFFResponse>>> Search([FromQuery] string creator, [FromQuery] string title, [FromQuery] bool free, [FromQuery] bool bought)
        {
            var list = await _caffService.SearchAsync(Convert.ToInt64(User.UserId()), creator, title, free, bought);
            var result = _mapper.Map<List<CAFFResponse>>(list);
            return Ok(result);
        }

        [Authorize]
        [HttpGet]
        [Route("{id:long}/comments")]
        public async Task<ActionResult<List<CommentResponse>>> GetCommentsAsync([FromRoute] long id)
        {
            var list = await _commentService.GetForCaffAsync(id);
            var result = _mapper.Map<List<CommentResponse>>(list);
            return Ok(result);
        }

        [Authorize]
        [HttpPost]
        [Route("{id:long}/buy")]
        public async Task<ActionResult> BuyAsync([FromRoute] long id)
        {
            if (User.UserId() == null)
                return Unauthorized();
            await _caffService.BuyAsync(Convert.ToInt64(User.UserId()), id);
            return Ok();
        }

        [Authorize]
        [HttpPost]
        [Route("{id:long}/comments")]
        public async Task<ActionResult> CommentAsync([FromRoute] long id, [FromBody] CommentRequest comment)
        {
            await _commentService.Add(User.UserId(), id, comment.Text);
            return Ok();
        }

        [Authorize]
        [HttpPost]
        [Route("{name}/{price}")]
        public async Task<ActionResult<CommentResponse>> UploadCaffAsync(IFormFile file, [FromRoute] string name, [FromRoute] string price)
        {
            var request = new UploadCAFFRequest
            {
                Image = file,
                Name = name,
                Price = double.Parse(price, CultureInfo.InvariantCulture)
            };

            var caff = await _caffService.Upload(Convert.ToInt64(User.UserId()), request);

            var response = new CAFFResponse { 
                Id = caff.Id,
                Name = caff.Name,
                Cost = caff.Cost,
                ImageUrl = $"{caff.Id}/download"
            };
            return Ok(response);
        }

        [Authorize]
        [HttpGet]
        [Route("{id:long}/download")]
        public async Task<ActionResult<IFormFile>> DownloadCaffAsync([FromRoute] long id)
        {
            var file = await _caffService.Download(Convert.ToInt64(User.UserId()), id);
            return File(file, "image/caff", id + ".caff");
        }

        [HttpGet]
        [Route("{caffId:long}/thumbnails/{thumbnailId:long}")]
        public async Task<ActionResult<IFormFile>> DownloadThumbnailAsync([FromRoute] long caffId, [FromRoute] long thumbnailId)
        {
            var file = await _caffService.DownloadThumbnail(caffId, thumbnailId);
            return File(file, "image/bmp", thumbnailId + ".bmp");
        }

        [Authorize]
        [HttpDelete]
        [Route("{id:long}")]
        public async Task<ActionResult> DeleteCaffAsync([FromRoute] long id)
        {
            if (!User.IsAdmin())
            {
                return Unauthorized();
            }

            await _caffService.Delete(id);
            return Ok();
        }

        [Authorize]
        [HttpDelete]
        [Route("{caffId:long}/comments/{commentId}")]
        public async Task<ActionResult> DeleteCommentAsync([FromRoute] long caffId, [FromRoute] string commentId)
        {
            if (!User.IsAdmin())
            {
                return Unauthorized();
            }

            await _commentService.Delete(caffId, commentId);
            return Ok();
        }
    }
}
