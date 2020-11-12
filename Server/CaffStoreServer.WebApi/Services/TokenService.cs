using System;
using Newtonsoft.Json;
using JWT.Algorithms;
using JWT.Builder;
using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Responses;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Linq;

namespace CaffStoreServer.WebApi.Services
{
    public class TokenService
    {
        private readonly string _secret;

        private readonly JwtSecurityTokenHandler _tokenHandler = new JwtSecurityTokenHandler();

        public TokenService(string secret)
        {
            _secret = secret;  
        }

        public string GenerateToken(string username, long userId, UserType type, int expireMinutes = 60)
        {
            var token = new JwtBuilder()
                .WithAlgorithm(new HMACSHA256Algorithm())
                .WithSecret(_secret)
                .AddClaim("exp", DateTimeOffset.UtcNow.AddMinutes(expireMinutes).ToUnixTimeSeconds())
                .AddClaim("username", username)
                .AddClaim("type", type)
                .AddClaim(ClaimTypes.Name, userId.ToString())
                .AddClaim(ClaimTypes.Role, type == UserType.User ? "User" : "Administrator")
                .Encode();

            return token;
        }

        public LoginResponse DecodeToken(string token)
        {
            try
            {
                string json = new JwtBuilder()
                    .WithAlgorithm(new HMACSHA256Algorithm())
                    .WithSecret(_secret)
                    .MustVerifySignature()
                    .Decode(token);
                return JsonConvert.DeserializeObject<LoginResponse>(json);
            }
            catch (JWT.Exceptions.TokenExpiredException)
            {
                return null;
            }
        }

        public string DecodeUserId(string token)
        {
            var decodedToken = _tokenHandler.ReadToken(token) as JwtSecurityToken;

            return decodedToken.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Name).Value;
        }

        public string DecodeUserRole(string token)
        {
            var decodedToken = _tokenHandler.ReadToken(token) as JwtSecurityToken;

            return decodedToken.Claims.FirstOrDefault(c => c.Type == ClaimTypes.Role).Value;
        }
    }
}
