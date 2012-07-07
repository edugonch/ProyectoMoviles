using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Alchemy;
using Alchemy.Classes;
using System.Net;
using Newtonsoft.Json;
using System.Data.SQLite;
using ChatServer.Models;
using System.Collections;

namespace ChatServer
{
    class Program
    {
            /// <summary>
        /// Store the list of online users. Wish I had a ConcurrentList. 
        /// </summary>
        protected static ConcurrentDictionary<string, Usuario> OnlineUsers = new ConcurrentDictionary<string, Usuario>();
        private static SQLiteConnection conexion = new SQLiteConnection(@"Data Source=db.dba;");
        private static List<Usuario> usuarios = new List<Usuario>();

        /// <summary>
        /// Initialize the application and start the Alchemy Websockets server
        /// </summary>
        /// <param name="args"></param>
        static void Main(string[] args)
        {
            
            // Initialize the server on port 81, accept any IPs, and bind events.
            var aServer = new WebSocketServer(81, IPAddress.Any)
                              {
                                  OnReceive = OnReceive,
                                  OnSend = OnSend,
                                  OnConnected = OnConnect,
                                  OnDisconnect = OnDisconnect,
                                  TimeOut = new TimeSpan(0, 5, 0)
                              };

            aServer.Start();

            // Accept commands on the console and keep it alive
            var command = string.Empty;
            while (command != "exit")
            {
                command = Console.ReadLine();
            }

            aServer.Stop();
        }

        private static bool usuarioAlreadyLogIn(Usuario user)
        {
            foreach (Usuario tmpUser in usuarios)
            {
                if (tmpUser.username == user.username)
                {
                    return true;
                }
            }

            return false;
        }

        /// <summary>
        /// Event fired when a client connects to the Alchemy Websockets server instance.
        /// Adds the client to the online users list.
        /// </summary>
        /// <param name="context">The user's connection context</param>
        public static void OnConnect(UserContext context)
        {
            Console.WriteLine("Client Connection From : " + context.ClientAddress);

            
        }

        public static string getUserList()
        {
            string list = "{\"type\": \"userList\", \"users\": [";
            int count = 1;
            int total = OnlineUsers.Keys.Count;
            foreach (Usuario tmpUser in OnlineUsers.Values)
            {
                list += "{\"username\": \"" + tmpUser.username + "\"}";
                if (count < total)
                {
                    list += ",";
                }
                count++;
            }

            list += "]}";
            return list;
        }
        /// <summary>
        /// Event fired when a data is received from the Alchemy Websockets server instance.
        /// Parses data as JSON and calls the appropriate message or sends an error message.
        /// </summary>
        /// <param name="context">The user's connection context</param>
        public static void OnReceive(UserContext context)
        {
            Console.WriteLine("Received Data From :" + context.ClientAddress);

            try
            {
                var json = context.DataFrame.ToString();

                // <3 dynamics
                dynamic obj = JsonConvert.DeserializeObject(json);

                switch ((CommandType)Enum.Parse(typeof(CommandType), (string)obj.type))
                {
                    case CommandType.login:
                        LogIn(obj, context);
                        break;
                    case CommandType.message:
                        ChatMessage(json, context);
                        break;
                    case CommandType.newUser:
                        CreateNewUser(obj, context);
                        break;
                    case CommandType.reConnection:
                        ReConnectUser(obj, context);
                        break;
                }
            }
            catch (Exception e) // Bad JSON! For shame.
            {
                var r = new Response {Type = ResponseType.Error, Data = new {e.Message}};

                context.Send(JsonConvert.SerializeObject(r));
            }
        }

        private static void ReConnectUser(dynamic obj, UserContext context)
        {
            var me = new Usuario { Context = context };

            me.username = obj.username;

           OnlineUsers.TryAdd(me.username, me);

           context.Send(getUserList());

           TellAllIamHere(me);
        }

        private static void TellAllIamHere(Usuario tmpUser)
        {
            var users = OnlineUsers.Values.Where(o => o.username != tmpUser.username).ToArray();
            Broadcast(Errores.NewUserLogIn(tmpUser.username, "ha iniciado sesion."), users);
        }

        private static void CreateNewUser(dynamic obj, UserContext context)
        {
            conexion.Open();
            //
            string userCount = "SELECT COUNT(*) FROM usuarios WHERE username = '" + (string)obj.username + "'";
            SQLiteCommand cmdCount = new SQLiteCommand(userCount, conexion);
            long count = (long)cmdCount.ExecuteScalar();

            if (count > 0)
            {
                conexion.Close();
                context.Send(Errores.LogInErrorUserExists("El usuario ya existe."));
            }
            else
            {
                string insertUser = "INSERT INTO usuarios(username, password) VALUES ('" + (string)obj.username + "','" + (string)obj.password + "')";
                SQLiteCommand cmd = new SQLiteCommand(insertUser, conexion);
                int result = cmd.ExecuteNonQuery();
                Usuario tmpUsuario = new Usuario { Context = context };

                tmpUsuario.username = (string)obj.username;

                //if (!usuarioAlreadyLogIn(tmpUsuario))
                //{
                    OnlineUsers.TryAdd(tmpUsuario.username, tmpUsuario);
                    //usuarios.Add(tmpUsuario);
                //}

                conexion.Close();
                context.Send(Errores.SuccessNewUser("Usuario Creado exitosamente.", (string)obj.username, (string)obj.password));
            }
        }

        /// <summary>
        /// Event fired when the Alchemy Websockets server instance sends data to a client.
        /// Logs the data to the console and performs no further action.
        /// </summary>
        /// <param name="context">The user's connection context</param>
        public static void OnSend(UserContext context)
        {
            Console.WriteLine("Data Send To : " + context.ClientAddress);
        }

        /// <summary>
        /// Event fired when a client disconnects from the Alchemy Websockets server instance.
        /// Removes the user from the online users list and broadcasts the disconnection message
        /// to all connected users.
        /// </summary>
        /// <param name="context">The user's connection context</param>
        public static void OnDisconnect(UserContext context)
        {
            try
            {
                Console.WriteLine("Client Disconnected : " + context.ClientAddress);
                var user = OnlineUsers.Values.Where(o => o.Context.ClientAddress == context.ClientAddress).Single();

                //object trash; // Concurrent dictionaries make things weird

                OnlineUsers.TryRemove(user.username, out user);

                if (!String.IsNullOrEmpty(user.username))
                {
                    //var r = new Response { Type = "disconnect", Data = new { user.username } };
                    string response = "{\"type\": \"disconnect\", \"username\": \"" + user.username + "\", \"message\": \"se ha desconectado del chat.\"}";

                    Broadcast(response);
                }

                Broadcast(getUserList());
            }
            catch (Exception ex) { }
        }


        private static void LogIn(dynamic obj, UserContext context)
        {
            conexion.Open();
            string searchUser = "SELECT * FROM usuarios where username = '" + (string)obj.username + "'";
            SQLiteCommand cmd = new SQLiteCommand(searchUser, conexion);
            SQLiteDataReader data = cmd.ExecuteReader();
            string password = "";
            string avatar = "";
            while (data.Read())
            {
                password = Convert.ToString(data[1]);
                avatar = Convert.ToString(data[2]);

            }
            conexion.Close();

            if (password == "" || password != (string)obj.password)
            {
                context.Send(Errores.LogInError("Nombre de usuario o contraseña incorrecta."));
            }
            else
            {
                Usuario tmpUsuario = new Usuario { Context = context };
                tmpUsuario.username = (string)obj.username;

               // if (!usuarioAlreadyLogIn(tmpUsuario))
               // {
                OnlineUsers.TryAdd(tmpUsuario.username, tmpUsuario);
               // }
                context.Send(Errores.LogInSuccess("", (string)obj.username, (string)obj.password));
            }
            
        }

        /// <summary>
        /// Broadcasts a chat message to all online usrs
        /// </summary>
        /// <param name="message">The chat message to be broadcasted</param>
        /// <param name="context">The user's connection context</param>
        private static void ChatMessage(string message, UserContext context)
        {
            /*var u = OnlineUsers.Keys.Where(o => o.Context.ClientAddress == context.ClientAddress).Single();
            var r = new Response {Type = ResponseType.Message, Data = new {u.username, Message = message}};*/

            Broadcast(message);

        }

        /// <summary>
        /// Update a user's name if they sent a name-change command from the client.
        /// </summary>
        /// <param name="name">The name to be changed to</param>
        /// <param name="aContext">The user's connection context</param>
        private static void NameChange(string name, UserContext aContext)
        {
            var u = OnlineUsers.Values.Where(o => o.Context.ClientAddress == aContext.ClientAddress).Single();

            if (ValidateName(name)) { 
                var r = new Response
                                 {
                                     Type = ResponseType.NameChange,
                                     Data = new {Message = u.username + " is now known as " + name}
                                 };
                Broadcast(JsonConvert.SerializeObject(r));

                u.username = name;
                OnlineUsers[name] = u;

                Broadcast(getUserList());
            }
            else
            {
                SendError("Name is of incorrect length.", aContext);
            }
        }

        /// <summary>
        /// Broadcasts an error message to the client who caused the error
        /// </summary>
        /// <param name="errorMessage">Details of the error</param>
        /// <param name="context">The user's connection context</param>
        private static void SendError(string errorMessage, UserContext context)
        {
            var r = new Response {Type = ResponseType.Error, Data = new {Message = errorMessage}};

            context.Send(JsonConvert.SerializeObject(r));
        }

        /// <summary>
        /// Broadcasts a list of all online users to all online users
        /// </summary>
        private static void BroadcastNameList()
        {
            var r = new Response
                        {
                            Type = ResponseType.UserCount,
                            Data = new {Users = OnlineUsers.Keys.Where(o => !String.IsNullOrEmpty(o)).ToArray()}
                        };
            Broadcast(JsonConvert.SerializeObject(r));
        }

        /// <summary>
        /// Broadcasts a message to all users, or if users is populated, a select list of users
        /// </summary>
        /// <param name="message">Message to be broadcast</param>
        /// <param name="users">Optional list of users to broadcast to. If null, broadcasts to all. Defaults to null.</param>
        private static void Broadcast(string message, ICollection<Usuario> users = null)
        {
            if (users == null)
            {
                foreach (var u in OnlineUsers.Values)
                {
                    u.Context.Send(message);
                }
            }
            else
            {
                foreach (var u in OnlineUsers.Values.Where(users.Contains))
                {
                    u.Context.Send(message);
                }
            }
        }

        /// <summary>
        /// Checks validity of a user's name
        /// </summary>
        /// <param name="name">Name to check</param>
        /// <returns></returns>
        private static bool ValidateName(string name)
        {
            var isValid = false;
            if (name.Length > 3 && name.Length < 25)
            {
                isValid = true;
            }

            return isValid;
        }

        /// <summary>
        /// Defines the type of response to send back to the client for parsing logic
        /// </summary>
        public enum ResponseType
        {
            Connection = 0,
            Disconnect = 1,
            Message = 2,
            NameChange = 3,
            UserCount = 4,
            Error = 255
        }

        /// <summary>
        /// Defines the response object to send back to the client
        /// </summary>
        public class Response
        {
            public ResponseType Type { get; set; }
            public dynamic Data { get; set; }
        }

        /// <summary>
        /// Holds the name and context instance for an online user
        /// </summary>
        public class User
        {
            public string Name = String.Empty;
            public UserContext Context { get; set; }
        }

        /// <summary>
        /// Defines a type of command that the client sends to the server
        /// </summary>
        public enum CommandType
        {
            login,
            logout,
            message,
            newUser,
            reConnection
        }
    
    }
}

