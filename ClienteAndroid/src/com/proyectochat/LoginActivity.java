package com.proyectochat;

import Controladores.ChatController;
import Models.LogInRequest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.google.gson.*;

import de.roderick.weberknecht.WebSocketException;

public class LoginActivity extends Activity {

	EditText txtUsername, txtPassword;
	Button btnEntrar, btnRegistrar;
	ChatController controller = ChatController.getInstance();
	Gson gson = new Gson();
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		txtUsername = (EditText)findViewById(R.id.txtUsername);
		txtPassword = (EditText)findViewById(R.id.txtPassword);
		
		btnEntrar = (Button)findViewById(R.id.btnEntrar);
		btnRegistrar = (Button)findViewById(R.id.btnRegistrar);
		
		
		btnEntrar.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				try
				{
					LogInRequest loginRequest = new LogInRequest();
					
					loginRequest.setUsername(txtUsername.getText().toString());
					loginRequest.setPassword(txtPassword.getText().toString());
					
					try {
						controller.getWebsocket().send(gson.toJson(loginRequest));
					} catch (WebSocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		
		btnRegistrar.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
