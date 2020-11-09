using System;
using Newtonsoft.Json;
using JWT.Algorithms;
using JWT.Builder;
using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Responses;

namespace CaffStoreServer.WebApi.Services
{
    public static class TokenService
    {
        private const string Secret = "db3OIsj+BXE9NZDy0t8W3TcNekrF+2d/1sFnWG4HnV8TZY30iTOdtVWJG8abWvB1GlOgJuQZdcF2Luqm/hccMw==";

        public static string GenerateToken(string username, UserType type, int expireMinutes = 60)
        {
            var token = new JwtBuilder()
                .WithAlgorithm(new HMACSHA256Algorithm())
                .WithSecret(Secret)
                .AddClaim("exp", DateTimeOffset.UtcNow.AddMinutes(expireMinutes).ToUnixTimeSeconds())
                .AddClaim("username", username)
                .AddClaim("type", type)
                .Encode();

            return token;
        }

        public static LoginResponse DecodeToken(string token)
        {
            try
            {
                string json = new JwtBuilder()
                    .WithAlgorithm(new HMACSHA256Algorithm())
                    .WithSecret(Secret)
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
