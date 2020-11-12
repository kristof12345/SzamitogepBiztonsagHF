using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Requests;
using CaffStoreServer.WebApi.Models.Responses;
using CaffStoreServer.WebApi.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore.ChangeTracking;
using Microsoft.EntityFrameworkCore.Query.Internal;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class UsersController : ControllerBase
    {
        private readonly IUserService _userService;
        private readonly TokenService _tokenService;

        public UsersController(IUserService userService,
                               TokenService tokenService)
        {
            _userService = userService;
            _tokenService = tokenService;
        }

        [AllowAnonymous]
        [HttpPut]
        public ActionResult<LoginResponse> Login([FromBody] LoginRequest request)
        {
            if (request.Username == "admin")
            {
                var response = new LoginResponse
                {
                    IsSuccess = true,
                    Token = _tokenService.GenerateToken(request.Username, UserType.Admin),
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

        [Authorize(Roles = "Administrator")]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<User>>> GetUsersAsync()
            => Ok(await _userService.GetAsync());

        [Authorize(Roles = "Administrator,User")]
        [HttpPut("update")]
        public async Task<ActionResult> UpdateAsync([FromBody] UpdateRequest request)
        {
            await _userService.UpdateAsync(request);

            return NoContent();
        }

        [Authorize(Roles = "Administrator,User")]
        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteAsync(long id)
        {
            await _userService.DeleteAsync(id);

            return NoContent();
        }

        protected new LoginResponse User
        {
            get
            {
                string token = Request.Headers["Authorization"];
                return _tokenService.DecodeToken(token?.Substring(7));
            }
        }
    }
}
