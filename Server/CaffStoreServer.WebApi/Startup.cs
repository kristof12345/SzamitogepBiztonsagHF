using AutoMapper;
using CaffStoreServer.WebApi.Context;
using CaffStoreServer.WebApi.DataSeed;
using CaffStoreServer.WebApi.Entities;
using CaffStoreServer.WebApi.Extensions;
using CaffStoreServer.WebApi.Interfaces;
using CaffStoreServer.WebApi.Models;
using CaffStoreServer.WebApi.Models.Exceptions;
using CaffStoreServer.WebApi.Services;
using Hellang.Middleware.ProblemDetails;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace CaffStoreServer.WebApi
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddProblemDetails(options =>
            {
                options.IncludeExceptionDetails = (ctx, ex) => false;
                options.Map<LoginFailedException>(
                    (ctx, ex) =>
                    {
                        var pd = StatusCodeProblemDetails.Create(StatusCodes.Status401Unauthorized);
                        pd.Title = ex.Message;
                        return pd;
                    }
                );
                options.Map<AuthorizationException>(
                    (ctx, ex) =>
                    {
                        var pd = StatusCodeProblemDetails.Create(StatusCodes.Status401Unauthorized);
                        pd.Title = ex.Message;
                        return pd;
                    }
                );
                options.Map<BadRequestException>(
                    (ctx, ex) =>
                    {
                        var pd = StatusCodeProblemDetails.Create(StatusCodes.Status400BadRequest);
                        pd.Title = ex.Message;
                        return pd;
                    }
                );
            });
            services.AddDbContext<CaffStoreDbContext>(o =>
                o.UseSqlServer(Configuration.GetConnectionString("LocalConnection")));

            var tokenSettings = Configuration.GetSection(nameof(TokenSettings));
            services.Configure<TokenSettings>(
                tokenSettings);
            services.AddSingleton<ITokenSettings>(sp =>
                sp.GetRequiredService<IOptions<TokenSettings>>().Value);

            var fileSettings = Configuration.GetSection(nameof(FileSettings));
            services.Configure<FileSettings>(
                fileSettings);
            services.AddSingleton<IFileSettings>(sp =>
                sp.GetRequiredService<IOptions<FileSettings>>().Value);

            var caffparserSettings = Configuration.GetSection(nameof(CAFFparserSettings));
            services.Configure<CAFFparserSettings>(
                caffparserSettings);
            services.AddSingleton<ICAFFparserSettings>(sp =>
                sp.GetRequiredService<IOptions<CAFFparserSettings>>().Value);


            services.AddIdentity<User, Role>(o =>
            {
                o.Password.RequireDigit = true;
                o.Password.RequireLowercase = true;
                o.Password.RequireUppercase = true;
                o.Password.RequireNonAlphanumeric = true;
                o.Password.RequiredLength = 7;
            })
            .AddEntityFrameworkStores<CaffStoreDbContext>()
            .AddDefaultTokenProviders();

            services.AddAuthentication(options =>
            {
                options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
                options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
            })
            .AddJwtBearer(options =>
            {
                options.SaveToken = true;
                options.RequireHttpsMetadata = false;
                options.TokenValidationParameters = new TokenValidationParameters()
                {
                    ValidateLifetime = true,
                    ValidIssuer = tokenSettings["Issuer"],
                    ValidAudience = tokenSettings["Audience"],
                    ValidateIssuerSigningKey = true,
                    IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(tokenSettings["Secret"])),
                    ClockSkew = TimeSpan.Zero
                };
                options.Events = new JwtBearerEvents()
                {
                    OnTokenValidated =  async context =>
                    {
                        var userService = context.HttpContext.RequestServices.GetRequiredService<IUserService>();
                        try
                        {
                            var userid = context.Principal.UserId();
                            var user = await userService.GetByIdAsync(Convert.ToInt64(userid));
                            if (context.Principal.IsAdmin() && !user.IsAdmin())
                                context.Fail("invaild token");
                        } catch
                        {
                            context.Fail("invaild token");
                        }
                    },

                };
            });

            services.AddTransient<ITokenService, TokenService>();
            services.AddTransient<IUserService, UserService>();
            services.AddTransient<ICommentService, CommentService>();
            services.AddTransient<ICaffService, CaffService>();

            services.AddAutoMapper(typeof(Startup));

            services.AddControllers();
            services.AddSwaggerGen(c =>
            {
                c.SwaggerDoc("v1", new OpenApiInfo { Title = "CaffStoreServer webAPI", Version = "v1" });
                c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
                {
                    Description = @"JWT Authorization header using the Bearer scheme. \r\n\r\n 
                      Enter 'Bearer' [space] and then your token in the text input below.
                      \r\n\r\nExample: 'Bearer 12345abcdef'",
                    Name = "Authorization",
                    In = ParameterLocation.Header,
                    Type = SecuritySchemeType.ApiKey,
                    Scheme = "Bearer"
                });
                c.AddSecurityRequirement(new OpenApiSecurityRequirement()
                {
                {
                    new OpenApiSecurityScheme
                    {
                    Reference = new OpenApiReference
                        {
                        Type = ReferenceType.SecurityScheme,
                        Id = "Bearer"
                        },
                        Scheme = "oauth2",
                        Name = "Bearer",
                        In = ParameterLocation.Header,

                    },
                    new List<string>()
                    }
                });
            });

            services.AddMvc();
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IWebHostEnvironment env)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();

                DevelopmentDataSeed.Initialize(app.ApplicationServices
                   .GetRequiredService<IServiceScopeFactory>()
                   .CreateScope()
                   .ServiceProvider);
            } else
            {
                app.UseProblemDetails();
            }

            app.UseHttpsRedirection();

            app.UseRouting();

            app.UseAuthentication();

            app.UseAuthorization();

            app.UseSwagger();
            app.UseSwaggerUI(c =>
            {
                c.SwaggerEndpoint("/swagger/v1/swagger.json", "CaffStoreServer webAPI V1");
                c.RoutePrefix = string.Empty;
            });

            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
            });
        }
    }
}
