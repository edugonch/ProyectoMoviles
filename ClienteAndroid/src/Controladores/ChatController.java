package Controladores;

import de.roderick.weberknecht.WebSocket;
import de.roderick.weberknecht.WebSocketConnection;
import de.roderick.weberknecht.WebSocketEventHandler;
import de.roderick.weberknecht.WebSocketException;
import de.roderick.weberknecht.WebSocketMessage;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import Models.Respuesta;
import Models.SimpleUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.proyectochat.ChatActivity;

public class ChatController{
	private URI url;
	private WebSocket websocket;
	private Gson gson;
	private Activity currentActivity;
	private SimpleUser currentUser;
	private ChatActivity chatActivity;

	
    private ChatController() {
    	try
    	{
    		this.gson = new GsonBuilder().setPrettyPrinting().create();
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
	                         
	                            if(obj.getType().equals("logInSuccess"))
	                            {
	                            	logInSuccess(obj);
	                            }
	                            else if(obj.getType().equals("logInError"))
	                            {
	                            	logInError(obj);
	                            }
	                            else if(obj.getType().equals("logInErrorUserExists"))
	                            {
	                            	logInErrorUserExists(obj);
	                            }
	
	                            else if(obj.getType().equals("successNewUser"))
	                            {
	                            	successNewUser(obj);
	                            }
	
	                            else if(obj.getType().equals("userList"))
	                            {
	                            	chatActivity.friends(obj);
	                            }
	                            else if(obj.getType().equals("newUserLogIn"))
	                            {
	                            	chatActivity.newUserLogIn(obj);
	                            }
	
	                            else if(obj.getType().equals("message"))
	                            {
	                            	if(getChatActivity() != null)
	                            	{
	                            		getChatActivity()._message(obj);
	                            	}
	                            }
	
	                            else if(obj.getType().equals("disconnect"))
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

    
    public void newUserLogIn(Respuesta obj)
    {
    	
    }
 
    
    public void logInSuccess(Respuesta obj)
    {
    	Intent openChat = new Intent("com.proyectochat.CHATACTIVITY");
    	
    	changeToIntent(openChat);
    }
    
    public void logInError(Respuesta obj)
    {
    	Intent openLoginError = new Intent("com.proyectochat.LOGINERRORACTIVITY");
    	
    	changeToIntent(openLoginError);
    }
    
    public void logInErrorUserExists(Respuesta obj)
    {
    	
    }
    
    public void successNewUser(Respuesta obj)
    {
    	SimpleUser tmpUser = new SimpleUser();
		tmpUser.setPassword(obj.getPassword());
		tmpUser.setUsername(obj.getUsername());
		setCurrentUser(tmpUser);
		
    	Intent openChat = new Intent("com.proyectochat.CHATACTIVITY");
    	
    	changeToIntent(openChat);
    }
    
    public void changeToIntent(Intent intent)
    {
    	this.currentActivity.startActivity(intent);
    }
    
    public void changeActivity(Activity newAct)
    {
    	this.currentActivity = newAct;
    }
    /************* �rea del singleton *********************/
	
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

	public SimpleUser getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(SimpleUser currentUser) {
		this.currentUser = currentUser;
	}

	public ChatActivity getChatActivity() {
		return chatActivity;
	}

	public void setChatActivity(ChatActivity chatActivity) {
		this.chatActivity = chatActivity;
	}
}
