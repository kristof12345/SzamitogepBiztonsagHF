using Microsoft.EntityFrameworkCore.Migrations;

namespace CaffStoreServer.WebApi.Migrations
{
    public partial class CreateComments : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Comment_Caff_CaffId",
                table: "Comment");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Comment",
                table: "Comment");

            migrationBuilder.RenameTable(
                name: "Comment",
                newName: "Comments");

            migrationBuilder.RenameIndex(
                name: "IX_Comment_CaffId",
                table: "Comments",
                newName: "IX_Comments_CaffId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Comments",
                table: "Comments",
                column: "Id");

            migrationBuilder.UpdateData(
                table: "AspNetRoles",
                keyColumn: "Id",
                keyValue: 1L,
                column: "ConcurrencyStamp",
                value: "b612d1a8-ebf8-4e0e-a634-90dc193f01ee");

            migrationBuilder.UpdateData(
                table: "AspNetRoles",
                keyColumn: "Id",
                keyValue: 2L,
                column: "ConcurrencyStamp",
                value: "ada4925c-e510-4097-a0a8-c7e0ef253a6c");

            migrationBuilder.AddForeignKey(
                name: "FK_Comments_Caff_CaffId",
                table: "Comments",
                column: "CaffId",
                principalTable: "Caff",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Comments_Caff_CaffId",
                table: "Comments");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Comments",
                table: "Comments");

            migrationBuilder.RenameTable(
                name: "Comments",
                newName: "Comment");

            migrationBuilder.RenameIndex(
                name: "IX_Comments_CaffId",
                table: "Comment",
                newName: "IX_Comment_CaffId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Comment",
                table: "Comment",
                column: "Id");

            migrationBuilder.UpdateData(
                table: "AspNetRoles",
                keyColumn: "Id",
                keyValue: 1L,
                column: "ConcurrencyStamp",
                value: "01bb46b5-5c1e-4d39-867b-9303f6caaec4");

            migrationBuilder.UpdateData(
                table: "AspNetRoles",
                keyColumn: "Id",
                keyValue: 2L,
                column: "ConcurrencyStamp",
                value: "5d5a3ed9-a61b-41c6-825e-caaa9455ad05");

            migrationBuilder.AddForeignKey(
                name: "FK_Comment_Caff_CaffId",
                table: "Comment",
                column: "CaffId",
                principalTable: "Caff",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
