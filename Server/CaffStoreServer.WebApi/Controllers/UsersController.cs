using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Requests;
using CaffStoreServer.WebApi.Models.Responses;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class UsersController : ControllerBase
    {
        private readonly IUserService _userService;
        private readonly ITokenService _tokenService;

        public UsersController(IUserService userService,
                               ITokenService tokenService)
        {
            _userService = userService;
            _tokenService = tokenService;
        }

        [AllowAnonymous]
        [HttpPut]
        public async Task<ActionResult<LoginResponse>> Login([FromBody] LoginRequest request)
        {
            if (request.Username == "admin")
            {
                var user = await _userService.GetByUserNameAsync(request.Username);
                var response = new LoginResponse
                {
                    IsSuccess = true,
                    Token = _tokenService.GenerateToken(request.Username,
                                                        user),
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

        [AllowAnonymous]
        [HttpPost]
        public async Task<ActionResult<RegisterResponse>> Register([FromBody] RegisterRequest request) 
            => await _userService.CreateUserAsync(request);

        [Authorize]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<User>>> GetUsersAsync()
            => Ok(await _userService.GetAsync());

        [Authorize]
        [HttpPut("update")]
        public async Task<ActionResult> UpdateAsync([FromBody] UpdateRequest request)
        {
            await _userService.UpdateAsync(request);

            return NoContent();
        }

        [Authorize]
        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteAsync(long id)
        {
            if (User.HasClaim(c => c.Type == "userid" && c.Value == id.ToString()))
            {
                await _userService.DeleteAsync(id);
                return NoContent();
            }
            return Unauthorized();
        }

    }
}
