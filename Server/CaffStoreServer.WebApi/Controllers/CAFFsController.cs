using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Requests;
using CaffStoreServer.WebApi.Models.Responses;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;

namespace CaffStoreServer.WebApi.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class CAFFsController : ControllerBase
    {
        [HttpGet]
        public ActionResult<List<CAFFResponse>> Search([FromQuery] string creator, [FromQuery] string title, [FromQuery] bool free, [FromQuery] bool bought)
        {
            //TODO: Authorization token from header
            var list = new List<CAFFResponse>();

            return Ok(list);
        }

        [HttpGet]
        [Route("{id}/comments")]
        public ActionResult<List<CommentResponse>> GetComments([FromRoute] string id)
        {
            //TODO: Authorization token from header
            var list = new List<CommentResponse>();

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
        [Route("{id}/comment")]
        public ActionResult Comment([FromRoute] string id)
        {
            //TODO: Authorization token from header

            return Ok();
        }

        [HttpPost]
        public ActionResult<CommentResponse> UploadImage([FromBody] UploadCAFFRequest caff)
        {
            //TODO: Authorization token from header

            var response = new CAFFResponse();
            return Ok(response);
        }

        [HttpGet]
        [Route("{id}/download")]
        public ActionResult<IFormFile> DownloadImage([FromBody] UploadCAFFRequest caff)
        {
            //TODO: Authorization token from header

            //TODO: return the file
            return Ok();
        }
    }
}
