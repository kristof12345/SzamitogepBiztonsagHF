﻿using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

namespace CaffStoreServer.WebApi.Context
{
    public class CaffStoreDbContext : IdentityDbContext<User, Role, long,
        IdentityUserClaim<long>, UserRole, IdentityUserLogin<long>,
        IdentityRoleClaim<long>, IdentityUserToken<long>>
    {
        public DbSet<Comment> Comments { get; set; }
        public DbSet<Caff> Caffs { get; set; }
        public DbSet<Thumbnail> Thumbnails { get; set; }
        public DbSet<PurchasedCaff> PurchasedCaffs { get; set; }

        public CaffStoreDbContext(DbContextOptions<CaffStoreDbContext> options) : base(options) { }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<User>(b =>
            {
                // Each User can have many UserClaims
                b.HasMany(e => e.Claims)
                    .WithOne()
                    .HasForeignKey(uc => uc.UserId)
                    .IsRequired();

                // Each User can have many UserLogins
                b.HasMany(e => e.Logins)
                    .WithOne()
                    .HasForeignKey(ul => ul.UserId)
                    .IsRequired();

                // Each User can have many UserTokens
                b.HasMany(e => e.Tokens)
                    .WithOne()
                    .HasForeignKey(ut => ut.UserId)
                    .IsRequired();

                // Each User can have many entries in the UserRole join table
                b.HasMany(e => e.UserRoles)
                    .WithOne(e => e.User)
                    .HasForeignKey(ur => ur.UserId)
                    .IsRequired();

                // Each User can purchase many caffs
                b.HasMany(e => e.PurchasedCaffs)
                    .WithOne(e => e.User)
                    .HasForeignKey(ur => ur.UserId)
                    .IsRequired();
            });

            modelBuilder.Entity<Role>(b =>
            {
                // Each Role can have many entries in the UserRole join table
                b.HasMany(e => e.UserRoles)
                    .WithOne(e => e.Role)
                    .HasForeignKey(ur => ur.RoleId)
                    .IsRequired();
            });

            modelBuilder.Entity<Role>().HasData(new Role
            {
                Id = 1,
                Name = RoleConstants.AdminRoleName,
                NormalizedName = RoleConstants.AdminNormalizedRoleNome
            });

            modelBuilder.Entity<Role>().HasData(new Role
            {
                Id = 2,
                Name = RoleConstants.UserRoleName,
                NormalizedName = RoleConstants.UserNormalizedRoleName
            });

            modelBuilder.Entity<Caff>(b =>
            {
                // Each Caff can have multiple comments
                b.HasMany(e => e.Comments)
                    .WithOne()
                    .HasForeignKey(uc => uc.CaffId)
                    .IsRequired();

                // Each Caff can have multiple thumbnails
                b.HasMany(e => e.Thumbnails)
                    .WithOne(th => th.Caff)
                    .HasForeignKey(th => th.CaffId)
                    .IsRequired();
            });

            modelBuilder.Entity<PurchasedCaff>(b =>
            {
                b.HasOne(e => e.Caff)
                    .WithMany()
                    .HasForeignKey(pc => pc.CaffId)
                    .IsRequired();
            });
        }
    }
}