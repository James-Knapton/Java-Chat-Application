import java.net.*;

public class User 
{
	private String username;
	private Socket user;
	
	public User(String username, Socket user)
	{
		this.username = username;
		this.user =  user;
	}

	public String getUserName()
	{
		return username;
	}
	
	public Socket getUserSocket()
	{
		return user;
	}
}
