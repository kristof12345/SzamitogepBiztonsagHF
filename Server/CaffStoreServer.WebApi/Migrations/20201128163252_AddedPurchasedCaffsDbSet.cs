using Microsoft.EntityFrameworkCore.Migrations;

namespace CaffStoreServer.WebApi.Migrations
{
    public partial class AddedPurchasedCaffsDbSet : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Comments_Caff_CaffId",
                table: "Comments");

            migrationBuilder.DropForeignKey(
                name: "FK_PurchasedCaff_Caff_CaffId",
                table: "PurchasedCaff");

            migrationBuilder.DropForeignKey(
                name: "FK_PurchasedCaff_AspNetUsers_UserId",
                table: "PurchasedCaff");

            migrationBuilder.DropForeignKey(
                name: "FK_Thumbnail_Caff_CaffId",
                table: "Thumbnail");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Thumbnail",
                table: "Thumbnail");

            migrationBuilder.DropPrimaryKey(
                name: "PK_PurchasedCaff",
                table: "PurchasedCaff");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Caff",
                table: "Caff");

            migrationBuilder.RenameTable(
                name: "Thumbnail",
                newName: "Thumbnails");

            migrationBuilder.RenameTable(
                name: "PurchasedCaff",
                newName: "PurchasedCaffs");

            migrationBuilder.RenameTable(
                name: "Caff",
                newName: "Caffs");

            migrationBuilder.RenameIndex(
                name: "IX_Thumbnail_CaffId",
                table: "Thumbnails",
                newName: "IX_Thumbnails_CaffId");

            migrationBuilder.RenameIndex(
                name: "IX_PurchasedCaff_UserId",
                table: "PurchasedCaffs",
                newName: "IX_PurchasedCaffs_UserId");

            migrationBuilder.RenameIndex(
                name: "IX_PurchasedCaff_CaffId",
                table: "PurchasedCaffs",
                newName: "IX_PurchasedCaffs_CaffId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Thumbnails",
                table: "Thumbnails",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_PurchasedCaffs",
                table: "PurchasedCaffs",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Caffs",
                table: "Caffs",
                column: "Id");

            migrationBuilder.UpdateData(
                table: "AspNetRoles",
                keyColumn: "Id",
                keyValue: 1L,
                column: "ConcurrencyStamp",
                value: "d170930d-9587-49ce-94dc-f9c0252bd594");

            migrationBuilder.UpdateData(
                table: "AspNetRoles",
                keyColumn: "Id",
                keyValue: 2L,
                column: "ConcurrencyStamp",
                value: "ef16f6b7-c7aa-44a3-a3f6-6025b6fad62f");

            migrationBuilder.AddForeignKey(
                name: "FK_Comments_Caffs_CaffId",
                table: "Comments",
                column: "CaffId",
                principalTable: "Caffs",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_PurchasedCaffs_Caffs_CaffId",
                table: "PurchasedCaffs",
                column: "CaffId",
                principalTable: "Caffs",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_PurchasedCaffs_AspNetUsers_UserId",
                table: "PurchasedCaffs",
                column: "UserId",
                principalTable: "AspNetUsers",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Thumbnails_Caffs_CaffId",
                table: "Thumbnails",
                column: "CaffId",
                principalTable: "Caffs",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Comments_Caffs_CaffId",
                table: "Comments");

            migrationBuilder.DropForeignKey(
                name: "FK_PurchasedCaffs_Caffs_CaffId",
                table: "PurchasedCaffs");

            migrationBuilder.DropForeignKey(
                name: "FK_PurchasedCaffs_AspNetUsers_UserId",
                table: "PurchasedCaffs");

            migrationBuilder.DropForeignKey(
                name: "FK_Thumbnails_Caffs_CaffId",
                table: "Thumbnails");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Thumbnails",
                table: "Thumbnails");

            migrationBuilder.DropPrimaryKey(
                name: "PK_PurchasedCaffs",
                table: "PurchasedCaffs");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Caffs",
                table: "Caffs");

            migrationBuilder.RenameTable(
                name: "Thumbnails",
                newName: "Thumbnail");

            migrationBuilder.RenameTable(
                name: "PurchasedCaffs",
                newName: "PurchasedCaff");

            migrationBuilder.RenameTable(
                name: "Caffs",
                newName: "Caff");

            migrationBuilder.RenameIndex(
                name: "IX_Thumbnails_CaffId",
                table: "Thumbnail",
                newName: "IX_Thumbnail_CaffId");

            migrationBuilder.RenameIndex(
                name: "IX_PurchasedCaffs_UserId",
                table: "PurchasedCaff",
                newName: "IX_PurchasedCaff_UserId");

            migrationBuilder.RenameIndex(
                name: "IX_PurchasedCaffs_CaffId",
                table: "PurchasedCaff",
                newName: "IX_PurchasedCaff_CaffId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Thumbnail",
                table: "Thumbnail",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_PurchasedCaff",
                table: "PurchasedCaff",
                column: "Id");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Caff",
                table: "Caff",
                column: "Id");

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

            migrationBuilder.AddForeignKey(
                name: "FK_Comments_Caff_CaffId",
                table: "Comments",
                column: "CaffId",
                principalTable: "Caff",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_PurchasedCaff_Caff_CaffId",
                table: "PurchasedCaff",
                column: "CaffId",
                principalTable: "Caff",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_PurchasedCaff_AspNetUsers_UserId",
                table: "PurchasedCaff",
                column: "UserId",
                principalTable: "AspNetUsers",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Thumbnail_Caff_CaffId",
                table: "Thumbnail",
                column: "CaffId",
                principalTable: "Caff",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
