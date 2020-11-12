using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models;
using Microsoft.IdentityModel.Tokens;
using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Linq;
using System.Text;
using CaffStoreServer.WebApi.Entities;

namespace CaffStoreServer.WebApi.Services
{
    public class TokenService : ITokenService
    {
        private readonly ITokenSettings _tokenSettings;

        private readonly JwtSecurityTokenHandler _tokenHandler = new JwtSecurityTokenHandler();

        public TokenService(ITokenSettings tokenSettings)
        {
            _tokenSettings = tokenSettings;
        }

        public string GenerateToken(string username, User user, UserType type, int expireMinutes = 60)
        {            var claims = new List<Claim>
            {
                new Claim("username", username),
                new Claim("userid", user.Id.ToString()),
                new Claim(ClaimTypes.Role, type == UserType.User ? "User" : "Administrator")
            };
            var secretBytes = Encoding.UTF8.GetBytes(_tokenSettings.Secret);
            var key = new SymmetricSecurityKey(secretBytes);
            var algorithm = SecurityAlgorithms.HmacSha256;

            var signingCredentials = new SigningCredentials(key, algorithm);

        
            var token = new JwtSecurityToken(
                _tokenSettings.Issuer,
                _tokenSettings.Audience,
                claims,
                notBefore: DateTime.Now,
                expires: DateTime.Now.AddMinutes(expireMinutes),
                signingCredentials
                );

            var tokenJson = _tokenHandler.WriteToken(token);

            return tokenJson;
        }
    }
}
