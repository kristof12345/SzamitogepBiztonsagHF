using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Requests;
using CaffStoreServer.WebApi.Models.Responses;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Services
{
    public class UserService : IUserService
    {

        private readonly UserManager<User> _userManager;
        private readonly SignInManager<User> _signInManager;
        private readonly ITokenService _tokenService;

        public UserService(UserManager<User> userManager,
                           SignInManager<User> signInManager,
                           ITokenService tokenService)
        {
            _userManager = userManager;
            _signInManager = signInManager;
            _tokenService = tokenService;
        }

        public async Task<IEnumerable<User>> GetAsync()
        {
            var users = _userManager.Users.Include(u => u.UserRoles).ThenInclude(ur => ur.Role);
            return await _userManager.Users
                .AsNoTracking()
                .ToListAsync();
        }


        public async Task<User> GetByIdAsync(long id)
        {
            var users = _userManager.Users.Include(u => u.UserRoles).ThenInclude(ur => ur.Role);
            return await users.FirstOrDefaultAsync(u => u.Id == id) ?? throw new Exception("Invalid id");
        }

        public async Task<User> GetByUserNameAsync(string userName)
        {
            var users = _userManager.Users.Include(u => u.UserRoles).ThenInclude(ur => ur.Role);
            return await users.FirstOrDefaultAsync(u => u.UserName == userName) ?? throw new Exception("Invalid userName");
        }

        public async Task<LoginResponse> LoginAsync(LoginRequest request)
        {
            var signInResult = await _signInManager.PasswordSignInAsync(request.Username, request.Password, false, false);
            if (!signInResult.Succeeded)
            {
                throw new LoginFailedException("Invalid username or password.");
            }
            var user = await GetByUserNameAsync(request.Username);

            var role = user.UserRoles.Any(ur => ur.Role.NormalizedName == "ADMINISTRATOR") ? UserType.Admin : UserType.User;
            var response = new LoginResponse
            {
                IsSuccess = true,
                Token = _tokenService.GenerateToken(user),
                UserId = user.Id,
                UserType = role
            };
            return response;
        }

        public async Task<RegisterResponse> CreateUserAsync(RegisterRequest request)
        {
            var user = new User
            {
                UserName = request.Username,
                Email = request.Email
            };

            var result = await _userManager.CreateAsync(user, request.Password);
            if (result.Succeeded)
            {
                await _userManager.AddToRoleAsync(user, "User");
                UserType userType = UserType.User;

                return new RegisterResponse
                {
                    IsSuccess = true,
                    UserId = user.Id,
                    Token = _tokenService.GenerateToken(user),
                    UserType = userType
                };
            }
            else
            {
                throw new Exception("User create error");
            }
        }

        public async Task UpdateAsync(UpdateRequest request)
        {
            var user = await GetByIdAsync(request.UserId);

            user.UserName = request.Username;
            user.Email = request.Email;

            if (!(await _userManager.UpdateAsync(user)).Succeeded)
            {
                throw new Exception("User update error");
            }
        }

        public async Task DeleteAsync(long id)
        {
            var result = await _userManager.DeleteAsync(await GetByIdAsync(id));
            if (!result.Succeeded)
            {
                throw new Exception("User delete error");
            }
        }
    }
}
