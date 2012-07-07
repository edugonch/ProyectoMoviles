package Models;

public class SimpleUser {
	protected String username;
	protected String password;
	protected String type;
	
	public SimpleUser()
	{
		this.username = "";
		this.password = "";
		this.type = "";
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
	public String getType() {
		return type;
	}	
	
}
