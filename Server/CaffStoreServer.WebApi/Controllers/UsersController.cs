using CaffStoreServer.WebApi.Models;
using Microsoft.AspNetCore.Mvc;

namespace CaffStoreServer.WebApi.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class UsersController : ControllerBase
    {
        public UsersController()
        { 

        }

        [HttpPut]
        public ActionResult<LoginResponse> Login([FromBody] LoginRequest request)
        {
            if(request.Username == "admin")
            {
                var response = new LoginResponse
                {
                    IsSuccess = true,
                    Token = "asdfgh",
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
        
    }
}
