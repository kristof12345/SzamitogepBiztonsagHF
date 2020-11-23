using AutoMapper;
using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Exceptions;
using CaffStoreServer.WebApi.Models.Requests;
using CaffStoreServer.WebApi.Models.Responses;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Linq;
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
        private readonly IMapper _mapper;

        public UsersController(IUserService userService, ITokenService tokenService, IMapper _mapper)
        {
            _userService = userService;
            _tokenService = tokenService;
            this._mapper = _mapper;
        }

        [AllowAnonymous]
        [HttpPut]
        public async Task<ActionResult<LoginResponse>> Login([FromBody] LoginRequest request)
        {
            var result = await _userService.LoginAsync(request);
            return Ok(result);
        }

        [AllowAnonymous]
        [HttpPost]
        public async Task<ActionResult<RegisterResponse>> Register([FromBody] RegisterRequest request)
        {
            var result = await _userService.CreateUserAsync(request);
            return Ok(result);
        }

        [Authorize]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<UserDTO>>> GetUsersAsync()
        {
            if (!IsAdmin())
            {
                return Unauthorized();
            }

            var userEntities = await _userService.GetAsync();
            var users = _mapper.Map<List<UserDTO>>(userEntities);

            return Ok(users);
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
