namespace CaffStoreServer.WebApi.Models
{
    public class FileSettings : IFileSettings
    {
        public string FilePath { get; set; }
        public int MaxSizeInMegaBytes { get; set; }
    }

    public interface IFileSettings
    {
        public string FilePath { get; }
        public int MaxSizeInMegaBytes { get; }
    }
}
