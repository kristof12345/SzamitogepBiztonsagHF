using AutoMapper;
using CaffStoreServer.WebApi.Extensions;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models.Requests;
using CaffStoreServer.WebApi.Models.Responses;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Controllers
{
    [ApiController]
    [Route("users")]
    public class UsersController : ControllerBase
    {
        private readonly IUserService _userService;
        private readonly IMapper _mapper;

        public UsersController(IUserService userService, IMapper _mapper)
        {
            _userService = userService;
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
            try
            {
                var result = await _userService.CreateUserAsync(request);
                return Ok(result);
            }
            catch (Exception e)
            {
                if (e.Message == "User create error")
                    return BadRequest("Password does not match requirements.");
                else
                    return Conflict(e.Message);
            }
        }

        [Authorize]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<UserDTO>>> GetUsersAsync()
        {
            if (!User.IsAdmin())
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
            if (User.UserId() != request.UserId
                && !User.IsAdmin())
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
            if (User.UserId() != id
                && !User.IsAdmin())
            {
                return Unauthorized();
            }
            await _userService.DeleteAsync(id);
            return NoContent();
        }
    }
}
