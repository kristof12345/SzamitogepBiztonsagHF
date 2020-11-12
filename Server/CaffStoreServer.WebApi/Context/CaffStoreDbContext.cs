using CaffStoreServer.WebApi.Entities;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Context
{
    public class CaffStoreDbContext : IdentityDbContext<User, IdentityRole<long>, long>
    {
        public CaffStoreDbContext(DbContextOptions<CaffStoreDbContext> options) : base(options) { }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            const string AdministratorRoleName = "Administrator";

            modelBuilder.Entity<IdentityRole<long>>().HasData(new IdentityRole<long>
            {
                Id = 1,
                Name = AdministratorRoleName,
                NormalizedName = AdministratorRoleName.ToUpper()
            });

            const string UserRoleName = "User";

            modelBuilder.Entity<IdentityRole<long>>().HasData(new IdentityRole<long>
            {
                Id = 2,
                Name = UserRoleName,
                NormalizedName = UserRoleName.ToUpper()
            });
        }
    }
}