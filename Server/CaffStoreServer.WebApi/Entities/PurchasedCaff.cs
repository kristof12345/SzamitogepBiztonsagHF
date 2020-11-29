using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace CaffStoreServer.WebApi.Entities
{
    public class PurchasedCaff
    {
        public long Id { get; set; }
        public long UserId { get; set; }
        public long CaffId { get; set; }
        public DateTime PurchasedOn { get; set; }
        public virtual User User { get; set; }
        public virtual Caff Caff { get; set; }
}
}
