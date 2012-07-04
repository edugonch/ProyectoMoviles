package Models;

public class Message {
	private String type;
	private SimpleUser usuario;
	private String message;
	
	public Message()
	{
		this.usuario = new SimpleUser();
		this.type = "message";
		this.message = "";
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


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
