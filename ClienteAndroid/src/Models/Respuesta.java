package Models;

import java.util.ArrayList;


public class Respuesta {
	
	private ArrayList<SimpleUser> users;
	private String message;
	private String type;
	private SimpleUser usuario;
	protected String username;
	protected String password;
	
	public Respuesta()
	{
		super();
		this.users = new ArrayList<SimpleUser>();
	}
	
	public void setType(String type)
	{
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ArrayList<SimpleUser> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<SimpleUser> users) {
		this.users = users;
	}

	public String getType() {
		return type;
	}

	public SimpleUser getUsuario() {
		return usuario;
	}

	public void setUsuario(SimpleUser usuario) {
		this.usuario = usuario;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
