package com.proyectochat;

import Controladores.ChatController;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class LoginErrorActivity extends Activity {

	private ChatController controller = ChatController.getInstance();
	private TextView lblError;
	private Button btnAceptar;
	
	public LoginErrorActivity()
	{
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginerror);
		
		this.setLblError((TextView)findViewById(R.id.lblError));
		this.btnAceptar = (Button)findViewById(R.id.btnAceptar);
	}

	private TextView getLblError() {
		return lblError;
	}

	private void setLblError(TextView lblError) {
		this.lblError = lblError;
	}
}
