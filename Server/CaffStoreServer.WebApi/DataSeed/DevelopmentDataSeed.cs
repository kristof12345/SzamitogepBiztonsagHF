using CaffStoreServer.WebApi.Entities;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.DataSeed
{
    public class DevelopmentDataSeed
    {
        public static async void Initialize(IServiceProvider serviceProvider)
        {
            const int adminCount = 1;
            const int userCount = 2;

            var users = new List<User>();

            using (var userManager = serviceProvider.GetRequiredService<UserManager<User>>())
            {
                if (!userManager.Users.Any())
                {
                    User user;
                    for (int i = 0; i < adminCount + userCount; i++)
                    {
                        if (i < adminCount)
                        {
                            user = new User
                            {
                                Email = $"admin{i}@admin.xpl",
                                SecurityStamp = Guid.NewGuid().ToString(),
                                UserName = $"Admin{i}"
                            };

                            await userManager.CreateAsync(user, "Pass123!");

                            await userManager.AddToRoleAsync(user, "Administrator");
                        }
                        else
                        {
                            user = new User
                            {
                                Email = $"user{i}@user.xpl",
                                SecurityStamp = Guid.NewGuid().ToString(),
                                UserName = $"User{i}"
                            };

                            await userManager.CreateAsync(user, "123456");

                            await userManager.AddToRoleAsync(user, "User");
                        }

                        users.Add(user);
                    }
                }
                else
                {
                    users = await userManager.Users.ToListAsync();
                }
            }
        }
    }
}
