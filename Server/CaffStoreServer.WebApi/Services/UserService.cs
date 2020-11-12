﻿using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Requests;
using CaffStoreServer.WebApi.Models.Responses;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Services
{
    public class UserService : IUserService
    {

        private readonly UserManager<User> _userManager;
        private readonly ITokenService _tokenService;

        public UserService(UserManager<User> userManager,
                           ITokenService tokenService)
        {
            _userManager = userManager;
            _tokenService = tokenService;
        }

        public async Task<IEnumerable<User>> GetAsync()
            => await _userManager.Users
                                 .AsNoTracking()
                                 .ToListAsync();

        public async Task<User> GetByIdAsync(long id)
            => await _userManager.FindByIdAsync(id.ToString())
                ?? throw new Exception("Invalid id");

        public async Task<User> GetByUserNameAsync(string userName)
            => await _userManager.FindByNameAsync(userName)
                ?? throw new Exception("Invalid userName");

        public async Task<LoginResponse> LoginAsync(LoginRequest request)
        {
            var user = await GetByUserNameAsync(request.Username);
            if (user == null)
                throw new UserNotFoundException();

            var roles = await _userManager.GetRolesAsync(user);
            var role = roles.Contains("Administrator") ? UserType.Admin : UserType.User;
            var response = new LoginResponse
            {
                IsSuccess = true,
                Token = _tokenService.GenerateToken(request.Username,
                                                    user,
                                                    role),
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

            if ((await _userManager.CreateAsync(user, request.Password)).Succeeded)
            {
                UserType userType;
                if (user.UserName.Contains("admin"))
                {
                    await _userManager.AddToRoleAsync(user, "Administrator");
                    userType = UserType.Admin;
                }
                else
                {
                    await _userManager.AddToRoleAsync(user, "User");
                    userType = UserType.User;
                }

                return new RegisterResponse
                {
                    IsSuccess = true,
                    UserId = user.Id,
                    Token = _tokenService.GenerateToken(user.UserName, user, userType),
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