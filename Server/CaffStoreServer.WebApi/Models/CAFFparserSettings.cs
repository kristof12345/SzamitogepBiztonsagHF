using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Models
{
    public class CAFFparserSettings : ICAFFparserSettings
    {
        public string ParserExe { get; set; }
    }

    public interface ICAFFparserSettings
    {
        public string ParserExe { get; }
    }
}
