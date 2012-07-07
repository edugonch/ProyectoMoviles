package com.proyectochat;

import com.google.gson.Gson;

import de.roderick.weberknecht.WebSocketException;

import Controladores.ChatController;
import Models.NewUserRequest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {

	EditText txtUsername, txtPassword, txtPasswordConfirmation;
	Button btnEntrar, btnCancelar;
	ChatController controller = ChatController.getInstance();
	Gson gson = new Gson();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		controller.changeActivity(this);
		setContentView(R.layout.register);
		
		txtUsername = (EditText)findViewById(R.id.txtUsername);
		txtPassword = (EditText)findViewById(R.id.txtPassword);
		txtPasswordConfirmation = (EditText)findViewById(R.id.txtPasswordConfirmation);
		
		btnEntrar = (Button)findViewById(R.id.btnEntrar);
		btnCancelar = (Button)findViewById(R.id.btnCancelar);
		
		btnEntrar.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				try
				{
					NewUserRequest newUserRequest = new NewUserRequest();
					
					newUserRequest.setUsername(txtUsername.getText().toString());
					newUserRequest.setPassword(txtPassword.getText().toString());
										
					try {
						controller.getWebsocket().send(gson.toJson(newUserRequest));
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
	}

}
