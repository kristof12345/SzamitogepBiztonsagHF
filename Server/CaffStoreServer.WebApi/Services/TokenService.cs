using System;
using Newtonsoft.Json;
using JWT.Algorithms;
using JWT.Builder;
using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Responses;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;

namespace CaffStoreServer.WebApi.Services
{
    public class TokenService
    {
        private readonly string _secret;

        public TokenService(string secret)
        {
            _secret = secret;  
        }

        public string GenerateToken(string username, UserType type, int expireMinutes = 60)
        {
            var token = new JwtBuilder()
                .WithAlgorithm(new HMACSHA256Algorithm())
                .WithSecret(_secret)
                .AddClaim("exp", DateTimeOffset.UtcNow.AddMinutes(expireMinutes).ToUnixTimeSeconds())
                .AddClaim("username", username)
                .AddClaim("type", type)
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
    }
}
