using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.DataSeed
{
    public class DevelopmentDataSeed
    {
        public static async Task Initialize(IServiceProvider serviceProvider)
        {
            await SeedUsers(serviceProvider);
            await SeedCaffs(serviceProvider);
        }

        private static async Task SeedCaffs(IServiceProvider serviceProvider)
        {
            var caffService = serviceProvider.GetRequiredService<ICaffService>();

            var hasData = await caffService.Any();
            if (!hasData)
            {
                var list = new List<Caff> {
                    new Caff
                    {
                        Name = "This is caff 1",
                        Duration = 10,
                        Creator = "kristof",
                        CreationDate = DateTime.Now,
                        Cost = 3.14,
                        ImagePath = "Resources\\Files\\j3mcu3zp.ivk.caff",
                        Thumbnails = new List<Thumbnail> {
                            new Thumbnail
                            {
                                FilePath = "Resources\\Files\\j3mcu3zp.ivk\\1_img.bmp"
                            },
                            new Thumbnail
                            {
                                FilePath = "Resources\\Files\\j3mcu3zp.ivk\\2_img.bmp"
                            },
                            new Thumbnail
                            {
                                FilePath = "Resources\\Files\\j3mcu3zp.ivk\\3_img.bmp"
                            },
                            new Thumbnail
                            {
                                FilePath = "Resources\\Files\\j3mcu3zp.ivk\\4_img.bmp"
                            }
                        }
                    },
                    new Caff
                    {
                        Name = "This is caff 2",
                        Duration = 20,
                        Creator = "me",
                        CreationDate = DateTime.Now.AddDays(-1),
                        Cost = 0,
                        ImagePath = "Resources\\Files\\nsadtvi5.n33.caff",
                        Thumbnails = new List<Thumbnail> {
                            new Thumbnail
                            {
                                FilePath = "Resources\\Files\\nsadtvi5.n33\\1_img.bmp"
                            },
                            new Thumbnail
                            {
                                FilePath = "Resources\\Files\\nsadtvi5.n33\\2_img.bmp"
                            }
                        }
                    },
                    new Caff
                    {
                        Name = "This is caff 3",
                        Duration = 20,
                        Creator = "me",
                        CreationDate = DateTime.Now.AddDays(-1),
                        Cost = 5.16,
                        ImagePath = "Resources\\Files\\wdqixxwf.13i.caff",
                        Thumbnails = new List<Thumbnail> {
                            new Thumbnail
                            {
                                FilePath = "Resources\\Files\\wdqixxwf.13i\\1_img.bmp"
                            },
                            new Thumbnail
                            {
                                FilePath = "Resources\\Files\\wdqixxwf.13i\\2_img.bmp"
                            }
                        }
                    }
                };

                foreach (var caff in list)
                {
                    await caffService.Create(caff);
                }
            }
        }

        private static async Task SeedUsers(IServiceProvider serviceProvider)
        {
            const int adminCount = 1;
            const int userCount = 0;

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

                            await userManager.AddToRoleAsync(user, RoleConstants.AdminRoleName);
                        }
                        else
                        {
                            user = new User
                            {
                                Email = $"user{i}@user.xpl",
                                SecurityStamp = Guid.NewGuid().ToString(),
                                UserName = $"User{i}"
                            };

                            await userManager.CreateAsync(user, "Pass123!");

                            await userManager.AddToRoleAsync(user, RoleConstants.UserRoleName);
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
