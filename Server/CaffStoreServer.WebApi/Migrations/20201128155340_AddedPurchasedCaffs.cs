using System;
using Microsoft.EntityFrameworkCore.Migrations;

namespace CaffStoreServer.WebApi.Migrations
{
    public partial class AddedPurchasedCaffs : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "PurchasedCaff",
                columns: table => new
                {
                    Id = table.Column<long>(nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    UserId = table.Column<long>(nullable: false),
                    CaffId = table.Column<long>(nullable: false),
                    PurchasedOn = table.Column<DateTime>(nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_PurchasedCaff", x => x.Id);
                    table.ForeignKey(
                        name: "FK_PurchasedCaff_Caff_CaffId",
                        column: x => x.CaffId,
                        principalTable: "Caff",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_PurchasedCaff_AspNetUsers_UserId",
                        column: x => x.UserId,
                        principalTable: "AspNetUsers",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.UpdateData(
                table: "AspNetRoles",
                keyColumn: "Id",
                keyValue: 1L,
                column: "ConcurrencyStamp",
                value: "8f673d15-5299-434a-a256-bee07e58babc");

            migrationBuilder.UpdateData(
                table: "AspNetRoles",
                keyColumn: "Id",
                keyValue: 2L,
                column: "ConcurrencyStamp",
                value: "fc597829-26a0-4eb5-ab33-044874a1249b");

            migrationBuilder.CreateIndex(
                name: "IX_PurchasedCaff_CaffId",
                table: "PurchasedCaff",
                column: "CaffId");

            migrationBuilder.CreateIndex(
                name: "IX_PurchasedCaff_UserId",
                table: "PurchasedCaff",
                column: "UserId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "PurchasedCaff");

            migrationBuilder.UpdateData(
                table: "AspNetRoles",
                keyColumn: "Id",
                keyValue: 1L,
                column: "ConcurrencyStamp",
                value: "d8d502ae-8f95-47cb-9715-2177df761706");

            migrationBuilder.UpdateData(
                table: "AspNetRoles",
                keyColumn: "Id",
                keyValue: 2L,
                column: "ConcurrencyStamp",
                value: "7454af5f-4e8d-490c-92ef-1ea9ecbbce84");
        }
    }
}
