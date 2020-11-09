using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Requests;
using CaffStoreServer.WebApi.Models.Responses;
using CaffStoreServer.WebApi.Services;
using Microsoft.AspNetCore.Mvc;

namespace CaffStoreServer.WebApi.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class UsersController : ControllerBase
    {
        [HttpPut]
        public ActionResult<LoginResponse> Login([FromBody] LoginRequest request)
        {
            if (request.Username == "admin")
            {
                var response = new LoginResponse
                {
                    IsSuccess = true,
                    Token = TokenService.GenerateToken(request.Username, UserType.Admin),
                    UserId = 12,
                    UserType = UserType.Admin
                };

                return Ok(response);
            }

            if (request.Username == "me")
            {
                return Unauthorized();
            }

            return NotFound();
        }

        [HttpPost]
        public ActionResult Register([FromBody] RegisterRequest request)
        {
            //TODO: create user

            return Ok();
        }

        protected new LoginResponse User
        {
            get
            {
                string token = Request.Headers["Authorization"];
                return TokenService.DecodeToken(token?.Substring(7));
            }
        }
    }
}
