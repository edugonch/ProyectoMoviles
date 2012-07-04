/*Variables*/
var socket;  
var host = "ws://localhost:81/";
/* Eventos */
	$(function(){
		/*Envio de mensajes*/
		$('#enviarMensaje').live('click', function(){
			var mensage = new Message();
			mensage.usuario.username = sessionStorage.getItem('username');
			mensage.message = $('#inputMessage').val();
			socket.send(JSON.stringify(mensage));
		});
		/* registro */
		$('#loginNew').live('click', function(){
			$('#loginContainer').hide();
			if($('#registerContainer').length > 0)
			{
				$('#registerContainer').show();
			}
			else
			{
				$('body').append($('#tmpRegister').html());
			}
		});
		$('#registerClose').live('click', function(){
			$('#registerContainer').hide();
			$('#loginContainer').show();
		});
		/* Connectar al socket*/
		socketConnect();
		//Login Form
		$('#login').live("submit", function(){
			var login = new LogInRequest();
			login.username = $('#loginUsername').val();
			login.password = $('#loginPassword').val();
			socket.send(JSON.stringify(login));
			return false;
		});
		//Register Form
		$('#register').live('submit', function(){
			/*validar contraseña*/
			if($('#registerPassword').val() != $('#registerPasswordConfirmation').val())
			{
				alert("Las contraseñas no coinciden.");
				 $('#registerPasswordConfirmation').focus();
				 return false;
			}
			var _newUser = new NewUserRequest();
			_newUser.username = $('#registerUsername').val();
			_newUser.password = $('#registerPassword').val();
			socket.send(JSON.stringify(_newUser));
		});
	});
/***********/
function showLogInForm()
{
	if(!sessionStorage.getItem('isLogIn'))
	{
		$('body').append($('#tmpLogin').html());
	}
}

function socketConnect()
{
	if(("WebSocket" in window)){
		try{   
    	socket = new WebSocket(host);  
  
        //message('<p class="event">Socket Status: '+socket.readyState);  
  
        socket.onopen = onopen;
  
        socket.onmessage = onmessage;
  
        socket.onclose = onclose;
  
    } catch(exception){  
         message('<p>Error'+exception);  
    }  
	}
}

/* Funciones de Socket */
var onopen = function()
{
	if(sessionStorage.getItem('isLogIn'))
	{
		var tmpreq = new ReConnectionRequest();
		tmpreq.username = sessionStorage.getItem('username');
		socket.send(JSON.stringify(tmpreq));
	}
	
	//message('<p class="event">Socket Status: '+socket.readyState+' (open)');  
};
var onmessage = function(msg)
{
	//message('<p class="message">Received: '+msg.data); 
	//alert(msg.data);
	var obj = jQuery.parseJSON(msg.data);
	switch(obj.type)
	{
		case "logInSuccess":
			logInSuccess(obj);
			break;
		case "logInError":
			logInError(obj);
			break;
		case "logInErrorUserExists":
			logInErrorUserExists(obj);
			break;
		case "successNewUser":
			successNewUser(obj);
			break;
		case "userList":
			loadUserList(obj);
			break;
		case "newUserLogIn":
			newUserLogIn(obj);
			break;
		case "message":
			_menssage(obj);
			break;
		case "disconnect":
			disconnect(obj);
			break;
	}
};

var disconnect = function(obj)
{
	var listChat = $('#listChat');
	var liChat = $('<li>');

	liChat.append($('<span>').append('Sistema: '));
	liChat.append($('<span>').append(obj.username + ' ' + obj.message));
	listChat.append(liChat);
}

var _menssage = function(obj)
{
	var listChat = $('#listChat');
	var liChat = $('<li>');

	liChat.append($('<span>').append(obj.usuario.username + ': '));
	liChat.append($('<span>').append(obj.message));

	listChat.append(liChat);
};

var newUserLogIn = function(obj)
{
	var ulFriends = $('#friendsList');
	
	ulFriends.append($('<li>').html(obj.username));

	var listChat = $('#listChat');
	var liChat = $('<li>');
	
	if(obj.message)
	{
		liChat.append($('<span>').append('Sistema: '));
		liChat.append($('<span>').append(obj.username + ' ' + obj.message));
	}
	else
	{
		liChat.append($('<span>').append('Nombre: ' + obj.username));
	}
	listChat.append(liChat);
	
}

var loadUserList = function(obj)
{
	var ulFriends = $('#friendsList');
	ulFriends.empty();

	$.each(obj.users, function(index, value){
		ulFriends.append($('<li>').html(value.username));
	});
}

var logInSuccess = function(obj)
{
	$('#registerContainer').hide();
	$('#loginContainer').hide();
	sessionStorage.setItem('username', obj.username);  
	sessionStorage.setItem('isLogIn', true);  
}

var logInError = function(obj)
{
	alert(obj.message);
}
var logInErrorUserExists = function(obj)
{
	alert(obj.message);
}
var successNewUser = function(obj)
{
	alert(obj.message);
	logInSuccess(obj);
}

var onclose = function()
{
	//message('<p class="event">Socket Status: '+socket.readyState+' (Closed)');  
};
/******** Objetos ***********/
var SimpleUser = function()
{
	this.username = "";
	this.password = "";
};
var LogInRequest = function()
{
	this.type = "login";
};
LogInRequest.prototype = new SimpleUser();

var NewUserRequest = function()
{
	this.type = "newUser";
};
NewUserRequest.prototype = new SimpleUser();

var ReConnectionRequest = function()
{
	this.type = "reConnection";
};
ReConnectionRequest.prototype = new SimpleUser();

var DisconnectRequest = function()
{
	this.type = "disconnect";
};
DisconnectRequest.prototype = new SimpleUser();

var Message = function()
{
	this.usuario = new SimpleUser();
	this.message = "";
	this.type = "message";
}