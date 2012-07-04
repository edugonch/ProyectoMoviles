package Controladores;

import de.roderick.weberknecht.WebSocket;
import de.roderick.weberknecht.WebSocketConnection;
import de.roderick.weberknecht.WebSocketEventHandler;
import de.roderick.weberknecht.WebSocketException;
import de.roderick.weberknecht.WebSocketMessage;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

import Models.Respuesta;

import android.app.Activity;
import android.content.Intent;
import android.os.StrictMode;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class ChatController extends Activity {
	private URI url;
	private WebSocket websocket;
	Gson gson;
	
    private ChatController() {
    	try
    	{
    		/* apagar StrictMode */
    		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
    		//url = new URI("ws://172.16.17.46:81");
    		url = new URI("ws://10.0.2.2:81");
    		this.websocket = new WebSocketConnection(url);
    		// Register Event Handlers
    		this.websocket.setEventHandler(new WebSocketEventHandler() {
                    public void onOpen()
                    {
                            System.out.println("--open");
                    }
                                    
                    public void onMessage(WebSocketMessage message)
                    {
                            //System.out.println("--received message: " + message.getText());
                            try
                            {
	                            Respuesta obj = new Respuesta();
	                            //Type respuestaType = new TypeToken<Respuesta>() {}.getType();
	                            obj = gson.fromJson(message.getText(), Respuesta.class);
	                         
	                            if(obj.getType() == "logInSuccess")
	                            {
	                            	logInSuccess(obj);
	                            }
	                            else if(obj.getType() == "logInError")
	                            {
	                            	logInError(obj);
	                            }
	                            else if(obj.getType() == "logInErrorUserExists")
	                            {
	                            	logInErrorUserExists(obj);
	                            }
	
	                            else if(obj.getType() == "successNewUser")
	                            {
	                            	successNewUser(obj);
	                            }
	
	                            else if(obj.getType() == "userList")
	                            {
	                            	loadUserList(obj);
	                            }
	                            else if(obj.getType() == "newUserLogIn")
	                            {
	                            	newUserLogIn(obj);
	                            }
	
	                            else if(obj.getType() == "message")
	                            {
	                            	_menssage(obj);
	                            }
	
	                            else if(obj.getType() == "disconnect")
	                            {
	                            	disconnect(obj);
	                            }
                            }catch(Exception ex){
                            	ex.printStackTrace();
                            }
                    }
                                    
                    public void onClose()
                    {
                            System.out.println("--close");
                    }
            });
            
    		this.websocket.connect();
    	}
    	catch (WebSocketException wse) {
            wse.printStackTrace();
    	}
    	catch (URISyntaxException use) {
            use.printStackTrace();
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    public void disconnect(Respuesta obj)
    {
    	
    }
    
    public void _menssage(Respuesta obj)
    {
    	
    }
    
    public void newUserLogIn(Respuesta obj)
    {
    	
    }
    
    public void loadUserList(Respuesta obj)
    {
    	
    }
    
    public void logInSuccess(Respuesta obj)
    {
    	
    }
    
    public void logInError(Respuesta obj)
    {
    	Intent openLoginError = new Intent("com.proyectochat.LOGINERRORACTIVITY");
    	
    	startActivity(openLoginError);
    }
    
    public void logInErrorUserExists(Respuesta obj)
    {
    	
    }
    
    public void successNewUser(Respuesta obj)
    {
    	Intent openChat = new Intent("com.proyectochat.CHATACTIVITY");
    	
    	startActivity(openChat);
    }
    
    /************* Área del singleton *********************/
	
	private static ChatController INSTANCE = null;
 

    private synchronized static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new ChatController();
        }
    }
 
    public static ChatController getInstance() {
        createInstance();
        return INSTANCE;
    }

	public WebSocket getWebsocket() {
		return websocket;
	}

	public void setWebsocket(WebSocket websocket) {
		this.websocket = websocket;
	}
}
