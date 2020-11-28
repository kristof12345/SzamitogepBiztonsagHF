﻿using System;

namespace CaffStoreServer.WebApi.Entities
{
    public class Comment
    {
        public long Id { get; set; }
        public long CaffId { get; set; }
        public string UserName { get; set; }
        public DateTime AddTime { get; set; }
        public string Text { get; set; }
    }
}
