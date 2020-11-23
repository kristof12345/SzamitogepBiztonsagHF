using AutoMapper;
using CaffStoreServer.WebApi.Extensions;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models.Requests;
using CaffStoreServer.WebApi.Models.Responses;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
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
            var list = await _caffService.SearchAsync(User.UserId(), creator, title, free, bought);
            var result = _mapper.Map<List<CAFFResponse>>(list);
            return Ok(result);
        }

        [Authorize]
        [HttpGet]
        [Route("{id}/comments")]
        public async Task<ActionResult<List<CommentResponse>>> GetCommentsAsync([FromRoute] long id)
        {
            var list = await _commentService.GetForCaffAsync(id);
            var result = _mapper.Map<List<CommentResponse>>(list);
            return Ok(result);
        }

        [Authorize]
        [HttpPost]
        [Route("{id}/buy")]
        public async Task<ActionResult> BuyAsync([FromRoute] string id)
        {
            await _caffService.BuyAsync(User.UserId(), id);
            return Ok();
        }

        [Authorize]
        [HttpPost]
        [Route("{id}/comments")]
        public async Task<ActionResult> CommentAsync([FromRoute] string id, [FromBody] string text)
        {
            await _commentService.Add(User.UserId(), id, text);
            return Ok();
        }

        [Authorize]
        [HttpPost]
        [Route("{name}/{price}")]
        public async Task<ActionResult<CommentResponse>> UploadImageAsync(IFormFile file, [FromRoute] string name, [FromRoute] string price)
        {
            var request = new UploadCAFFRequest
            {
                Image = file,
                Name = name,
                Price = double.Parse(price, CultureInfo.InvariantCulture)
            };

            await _caffService.Upload(User.UserId(), request);

            var response = new CAFFResponse();
            return Ok(response);
        }

        [Authorize]
        [HttpGet]
        [Route("{id}/download")]
        public async Task<ActionResult<IFormFile>> DownloadImageAsync([FromRoute] string id)
        {
            var file = await _caffService.Download(User.UserId(), id);
            return File(file, "image/caff", id + ".caff");
        }

        [Authorize]
        [HttpDelete]
        [Route("{id}")]
        public async Task<ActionResult<IFormFile>> DeleteCaffAsync([FromRoute] string id)
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
        [Route("{caffId}/comments/{commentId}")]
        public async Task<ActionResult<IFormFile>> DeleteCommentAsync([FromRoute] string caffId, [FromRoute] string commentId)
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
