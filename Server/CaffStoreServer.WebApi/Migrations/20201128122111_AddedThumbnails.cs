using Microsoft.EntityFrameworkCore.Migrations;

namespace CaffStoreServer.WebApi.Migrations
{
    public partial class AddedThumbnails : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Comment_Caffs_CaffId",
                table: "Comment");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Caffs",
                table: "Caffs");

            migrationBuilder.DropColumn(
                name: "ImageUrl",
                table: "Caffs");

            migrationBuilder.DropColumn(
                name: "ThumbnailUrl",
                table: "Caffs");

            migrationBuilder.RenameTable(
                name: "Caffs",
                newName: "Caff");

            migrationBuilder.AddColumn<string>(
                name: "ImagePath",
                table: "Caff",
                nullable: true);

            migrationBuilder.AddPrimaryKey(
                name: "PK_Caff",
                table: "Caff",
                column: "Id");

            migrationBuilder.CreateTable(
                name: "Thumbnail",
                columns: table => new
                {
                    Id = table.Column<long>(nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    FilePath = table.Column<string>(nullable: true),
                    CaffId = table.Column<long>(nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Thumbnail", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Thumbnail_Caff_CaffId",
                        column: x => x.CaffId,
                        principalTable: "Caff",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

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

            migrationBuilder.CreateIndex(
                name: "IX_Thumbnail_CaffId",
                table: "Thumbnail",
                column: "CaffId");

            migrationBuilder.AddForeignKey(
                name: "FK_Comment_Caff_CaffId",
                table: "Comment",
                column: "CaffId",
                principalTable: "Caff",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Comment_Caff_CaffId",
                table: "Comment");

            migrationBuilder.DropTable(
                name: "Thumbnail");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Caff",
                table: "Caff");

            migrationBuilder.DropColumn(
                name: "ImagePath",
                table: "Caff");

            migrationBuilder.RenameTable(
                name: "Caff",
                newName: "Caffs");

            migrationBuilder.AddColumn<string>(
                name: "ImageUrl",
                table: "Caffs",
                type: "nvarchar(max)",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "ThumbnailUrl",
                table: "Caffs",
                type: "nvarchar(max)",
                nullable: true);

            migrationBuilder.AddPrimaryKey(
                name: "PK_Caffs",
                table: "Caffs",
                column: "Id");

            migrationBuilder.UpdateData(
                table: "AspNetRoles",
                keyColumn: "Id",
                keyValue: 1L,
                column: "ConcurrencyStamp",
                value: "f457dfaf-25c4-4c8d-a5cf-f00d677142cb");

            migrationBuilder.UpdateData(
                table: "AspNetRoles",
                keyColumn: "Id",
                keyValue: 2L,
                column: "ConcurrencyStamp",
                value: "9e29cc8a-8178-4632-b4c0-4b9c7552e8e8");

            migrationBuilder.AddForeignKey(
                name: "FK_Comment_Caffs_CaffId",
                table: "Comment",
                column: "CaffId",
                principalTable: "Caffs",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
