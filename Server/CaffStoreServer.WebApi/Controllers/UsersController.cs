using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Exceptions;
using CaffStoreServer.WebApi.Models.Requests;
using CaffStoreServer.WebApi.Models.Responses;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Security.Claims;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Controllers
{
    [ApiController]
    [Route("users")]
    public class UsersController : ControllerBase
    {
        private readonly IUserService _userService;
        private readonly ITokenService _tokenService;

        public UsersController(IUserService userService, ITokenService tokenService)
        {
            _userService = userService;
            _tokenService = tokenService;
        }

        [AllowAnonymous]
        [HttpPut]
        public async Task<ActionResult<LoginResponse>> Login([FromBody] LoginRequest request)
        {
            try
            {
                var result = await _userService.LoginAsync(request);
                return Ok(result);
            }
            catch (LoginFailedException e)
            {
                return Unauthorized(e.Message);
            }
        }

        [AllowAnonymous]
        [HttpPost]
        public async Task<ActionResult<RegisterResponse>> Register([FromBody] RegisterRequest request)
        {
            try
            {
                var result = await _userService.CreateUserAsync(request);
                return Ok(result);
            }
            catch (BadRequestException e)
            {
                return Conflict(e.Message);
            }
        }

        [Authorize]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<User>>> GetUsersAsync()
        {
            if (!IsAdmin())
            {
                return Unauthorized();
            }

            return Ok(await _userService.GetAsync());
        }

        [Authorize]
        [HttpPut("update")]
        public async Task<ActionResult> UpdateAsync([FromBody] UpdateRequest request)
        {
            if (!User.HasClaim(c => c.Type == "userid" && c.Value == request.UserId.ToString())
                && !User.HasClaim(c => c.Type == ClaimTypes.Role && c.Value == RoleConstants.AdminRoleName))
            {
                return Unauthorized();
            }
            await _userService.UpdateAsync(request);

            return NoContent();
        }

        [Authorize]
        [HttpDelete("{id}")]
        public async Task<ActionResult> DeleteAsync(long id)
        {
            if (!User.HasClaim(c => c.Type == "userid" && c.Value == id.ToString())
                && !User.HasClaim(c => c.Type == ClaimTypes.Role && c.Value == RoleConstants.AdminRoleName))
            {
                return Unauthorized();
            }
            await _userService.DeleteAsync(id);
            return NoContent();
        }

        private bool IsAdmin()
        {
            return User.HasClaim(c => c.Type == ClaimTypes.Role && c.Value == RoleConstants.AdminRoleName);
        }
    }
}
