package com.proyectochat;

import java.util.ArrayList;

import com.google.gson.Gson;

import de.roderick.weberknecht.WebSocketException;
import Models.Respuesta;
import Models.SimpleUser;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity {
    /** Called when the activity is first created. */
	private ListView lstMensajes;
	private ListView lstFriends;
	private EditText txtMensaje;
	private  ArrayAdapter<String> adapterMensajes;
	private ArrayList<String> listItemsMensajes = new ArrayList<String>();
	
	private  ArrayAdapter<String> adapterFriends;
	private ArrayList<String> listItemsFriends = new ArrayList<String>();
	
	Gson gson = new Gson();
	
	private Button btnEnviar;
	
	private Controladores.ChatController controller = Controladores.ChatController.getInstance();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainchat);
        
        this.lstMensajes = (ListView)findViewById(R.id.lstMensajes);
        this.lstFriends = (ListView)findViewById(R.id.lstFriends);
        this.txtMensaje = (EditText)findViewById(R.id.txtMensaje);
        
        this.btnEnviar = (Button)findViewById(R.id.btnEnviar);
        
        adapterMensajes = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
        		listItemsMensajes);
        lstMensajes.setAdapter(adapterMensajes);
        
        adapterFriends = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
        		listItemsFriends);
        lstFriends.setAdapter(adapterFriends);

        controller.setChatActivity(this);
        btnEnviar.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Models.Message message = new Models.Message();
				message.setMessage(txtMensaje.getText().toString());
				message.setUsuario(controller.getCurrentUser());
				try {
					controller.getWebsocket().send(gson.toJson(message));
				} catch (WebSocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    }
    public void friends(final Respuesta obj)
    {
    	Handler refresh = new Handler(Looper.getMainLooper());
    	refresh.post(new Runnable() {
    	    public void run()
    	    {
    	    	try
    	    	{
    	    		for(SimpleUser usuario : obj.getUsers())
    	    		{
    	    			listItemsFriends.clear();
    	    			listItemsFriends.add(listItemsFriends.size(), usuario.getUsername());
    	    			adapterFriends.notifyDataSetChanged();
    	    		}
    	    	}
    	    	catch(Exception ex){
    	    		ex.printStackTrace();
    	    	}
    	    }
    	});
    	
    }
    
    public void newUserLogIn(final Respuesta obj)
    {
    	Handler refresh = new Handler(Looper.getMainLooper());
    	refresh.post(new Runnable() {
    	    public void run()
    	    {
    	    	try
    	    	{
    	    		listItemsFriends.add(listItemsMensajes.size(), obj.getUsername());
    	    		adapterFriends.notifyDataSetChanged();
    	    	}
    	    	catch(Exception ex){
    	    		ex.printStackTrace();
    	    	}
    	    }
    	});
    	
    }
    
    public void _message(final Respuesta obj)
    {
    	Handler refresh = new Handler(Looper.getMainLooper());
    	refresh.post(new Runnable() {
    	    public void run()
    	    {
    	    	try
    	    	{
    		    	String mensaje = obj.getUsuario().getUsername() + ": " + obj.getMessage();
    		    	listItemsMensajes.add(listItemsMensajes.size(), mensaje);
    		    	adapterMensajes.notifyDataSetChanged();
    	    	}
    	    	catch(Exception ex){
    	    		ex.printStackTrace();
    	    	}
    	    }
    	});
    	
    }
}