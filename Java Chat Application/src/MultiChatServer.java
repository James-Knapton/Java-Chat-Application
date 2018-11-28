import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class MultiChatServer
{	
	public static void main(String[] args)
							throws IOException
	{
		ServerSocket serverSocket = null;
		final int PORT = 1234;
		Socket client;
		ClientHandler handler;

		try
		{
			serverSocket = new ServerSocket(PORT);
		}
		catch (IOException ioEx)
		{
			System.out.println("\nUnable to set up port!");
			System.exit(1);
		}
			
		System.out.println("\nServer running...\n");

		while(true)
		{
			//Wait for client.
			client = serverSocket.accept();
			Scanner input = new Scanner(client.getInputStream());
			PrintWriter output = new PrintWriter(client.getOutputStream(),true);
			String username = input.nextLine();	   		
			
			System.out.println("New client accepted " + username);

			handler = new ClientHandler(username, client);
			handler.start();
		}
	}
}

class ClientHandler extends Thread
{
	private Scanner inputStream;
	private PrintWriter display;
	private Socket client;
	private String username;
	private static ConcurrentLinkedDeque<User> list = new ConcurrentLinkedDeque<User>();

	public ClientHandler(String usernameReceived, Socket socket) throws IOException
	{
		client = socket;
		username = usernameReceived;
		inputStream = new Scanner(client.getInputStream());
	}
	
	public void run()
	{
		try
		{
			User newUser = new User(username, client);
			list.add(newUser);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		if(list.size() >= 2)
		{
			for(User ch : list)
			{
				try
				{
					display = new PrintWriter(ch.getUserSocket()
											.getOutputStream(),true);
					display.println("List of users connected");
					display.println(list.element().getUserName());
					display.println(list.peekLast().getUserName());
				}
				catch(IOException ioEx)
				{
					ioEx.printStackTrace();
				}
			}
		}
		
		String received = inputStream.nextLine();

		while (!received.equals("QUIT"))
		{	
			if(received.equals("TEXT"))
			{
				for(User ch : list)	
				{
					if(ch.getUserName().equals(username))
					{
						try 
						{
							// Feel free to change the value of attachment
							// The file path below is just an example
							String attachment = "C:\\Users\\Home\\Documents\\ExchangeRate.txt";

							FileInputStream fileIn = 
												new FileInputStream(attachment);
							
							byte[] byteArray = new byte[(int) attachment.length()];
							fileIn.read(byteArray, 0, attachment.length());
							fileIn.close();
					        
							OutputStream display = (ch.getUserSocket().getOutputStream());
							
							display.write(attachment.length());
							
							display.write(byteArray, 0, attachment.length());
							display.close();
						} 
						catch (IOException e) 
						{
							e.printStackTrace();
						}
					}
				}
			}
			else if(received.equals("IMAGE"))
			{
				for(User ch : list)	
				{
					if(ch.getUserName().equals(username))
					{
					
					// Feel free to change the value of attachment.
					// The file path below is just an example.
					String attachment = "C:\\Users\\Home\\Pictures\\football.png";
				
					try {
					FileInputStream fileIn = 
										new FileInputStream(attachment);
					
					long fileLen =  (new File(attachment)).length();

					int intFileLen = (int)fileLen;
    
					byte[] byteArray = new byte[intFileLen];
					
					fileIn.read(byteArray);
					fileIn.close();
					
					PrintWriter pw = new PrintWriter(ch.getUserSocket().getOutputStream(),true);
					pw.println("Sending Image");
					ObjectOutputStream os = new ObjectOutputStream(ch.getUserSocket().getOutputStream());
				
					os.writeObject(byteArray);
					os.flush();
				} 
				catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
					}
				}
			}
			else if(received.equals("SOUND"))
			{
				for(User ch : list)
				{
					if(ch.getUserName().equals(username))
					{
					
					// Feel free to change the value of attachment.
					// The file path below is just an example.
					String attachment = "C:\\Users\\Home\\Documents\\cuckoo.au";
					
					try 
					{
					
						FileInputStream fileIn = 
										new FileInputStream(attachment);
					
					long fileLen =  (new File(attachment)).length();

					int intFileLen = (int)fileLen;
    
					byte[] byteArray = new byte[intFileLen];
					
					fileIn.read(byteArray);
					fileIn.close();
					PrintWriter pw = new PrintWriter(ch.getUserSocket().getOutputStream(),true);
					pw.println("Sending audio");
					ObjectOutputStream os = new ObjectOutputStream(ch.getUserSocket().getOutputStream());
				
					os.writeObject(byteArray);
					os.flush();
				}
					catch (FileNotFoundException e) 
					{
						e.printStackTrace();
					} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
					}
				}
			}
			else
			{
				for(User ch : list)	
				{
					if(!ch.getUserName().equals(username))
				{
					try {
						PrintWriter display = new PrintWriter(ch.getUserSocket().getOutputStream(),true);
						display.println(username + " : " + received);
						break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			}
		received = inputStream.nextLine();
		}

	
		for(User ch : list)
		{
			if(ch.getUserName().equals(username))
			{
				list.remove(ch);
			}
			if(!(list.size() < 1))
			{
				try {
					PrintWriter display = new PrintWriter(ch.getUserSocket().getOutputStream(),true);
					display.println("List of users connected");
					display.println(ch.getUserName());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}	
