package Models;

import java.util.ArrayList;


public class Respuesta extends SimpleUser {
	
	private ArrayList<SimpleUser> users;
	private String message;
	
	public Respuesta()
	{
		this.users = new ArrayList<SimpleUser>();
	}
	
	public void setType(String type)
	{
		this.type = type;
	}

	protected String getMessage() {
		return message;
	}

	protected void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<SimpleUser> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<SimpleUser> users) {
		this.users = users;
	}
}
