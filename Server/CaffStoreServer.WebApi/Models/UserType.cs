using Newtonsoft.Json;
using Newtonsoft.Json.Converters;
using System.Runtime.Serialization;

namespace CaffStoreServer.WebApi.Models
{
    [JsonConverter(typeof(StringEnumConverter))]
    public enum UserType
    {
        [EnumMember(Value = "User")]
        User,

        [EnumMember(Value = "Admin")]
        Admin
    }
}
