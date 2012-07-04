using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Alchemy.Classes;

namespace ChatServer.Models
{
    public class Usuario
    {
        public string username { get; set; }
        public string password { get; set; }
        public string avatar { get; set; }

        public UserContext Context { get; set; }
    }
}
