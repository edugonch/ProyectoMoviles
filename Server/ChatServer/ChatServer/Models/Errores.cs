using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ChatServer.Models
{
    class Errores
    {
        public static string LogInError(string error)
        {
            return "{\"type\": \"logInError\", \"message\":\"" + error + "\"}";
        }

        public static string LogInErrorUserExists(string error)
        {
            return "{\"type\": \"logInErrorUserExists\", \"message\":\"" + error + "\"}";
        }

        public static string LogInSuccess(string error, string username, string password)
        {
            return "{\"type\": \"logInSuccess\", \"message\":\"" + error + "\", \"username\": \"" + username + "\",\"password\": \"" + password + "\"}";
        }


        public static string SuccessNewUser(string error, string username, string password)
        {
            return "{\"type\": \"successNewUser\", \"message\":\"" + error + "\", \"username\": \"" + username + "\",\"password\": \"" + password + "\"}";
        }

        public static string NewUserLogIn(string username, string message)
        {
            return "{\"type\": \"newUserLogIn\", \"message\":\"" + message + "\", \"username\": \"" + username + "\"}";
        }
    }
}
