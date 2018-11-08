import java.net.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.applet.*;

public class MultiChatClient extends JFrame implements ActionListener {

	private JPanel contentPane;
	private JTextField msgToSend;
	private static JTextArea chatroom, listOfUsers;
	private JButton send;
	private JButton closeConnection;
	private JButton btnRequestTextFile;
	private static JLabel lblNewLabel;
	private JButton btnRequestImage;
	private static ImageIcon image;
	private static Scanner input;
	private static PrintWriter output;
	private static String username;
	private static String password;
	private static InetAddress host;
	private static Socket socket;
	private static ObjectInputStream inStream;
	private JButton btnPlaySound;
	private JButton btnRequestSound;
	private static AudioClip clip;

	public static void main(String[] args)
			throws IOException
	{
		MultiChatClient frame = new MultiChatClient();
		frame.setTitle("Chat Client GUI");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setUpConnection();
	}

	public MultiChatClient() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1270, 704);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel entryPanel, readPanel;
		JLabel messagePrompt;

		entryPanel = new JPanel();
		entryPanel.setBounds(10, 89, 501, 78);
		messagePrompt = new JLabel("Enter a message: ");
		msgToSend = new JTextField(15);
		msgToSend.setEditable(true);
		send = new JButton("Send");
		send.addActionListener(this);
		contentPane.setLayout(null);
		entryPanel.add(messagePrompt);
		entryPanel.add(msgToSend);
		entryPanel.add(send);
		getContentPane().add(entryPanel);

		readPanel = new JPanel();
		readPanel.setBounds(779, 42, 454, 306);
		readPanel.setLayout(null);
		listOfUsers = new JTextArea(10, 15);
		JScrollPane scrollPane = new JScrollPane(listOfUsers);
		scrollPane.setBounds(26, 76, 126, 186);
		readPanel.add(scrollPane);
		chatroom = new JTextArea(10, 15);
		JScrollPane scrollPane_1 = new JScrollPane(chatroom);
		scrollPane_1.setBounds(259, 76, 126, 186);
		readPanel.add(scrollPane_1);
		getContentPane().add(readPanel);

		closeConnection = new JButton("Close Connection");
		closeConnection.setBounds(26, 573, 195, 23);
		closeConnection.addActionListener(this);
		getContentPane().add(closeConnection);	
		
		btnRequestTextFile = new JButton("Request Text File");
		btnRequestTextFile.setBounds(1012, 407, 174, 29);
		btnRequestTextFile.addActionListener(this);
		contentPane.add(btnRequestTextFile);
		
		lblNewLabel = new JLabel(image);
		lblNewLabel.setBounds(319, 373, 223, 213);
		contentPane.add(lblNewLabel);
		
		btnRequestImage = new JButton("Request Image");
		btnRequestImage.setBounds(1012, 452, 172, 29);
		btnRequestImage.addActionListener(this);
		contentPane.add(btnRequestImage);
		
		btnPlaySound = new JButton("Play Sound");
		btnPlaySound.setBounds(1012, 543, 174, 29);
		btnPlaySound.addActionListener(this);
		contentPane.add(btnPlaySound);
		
		btnRequestSound = new JButton("Request Sound");
		btnRequestSound.setBounds(1012, 497, 174, 29);
		btnRequestSound.addActionListener(this);
		contentPane.add(btnRequestSound);
	}
	
	
	public static void setUpConnection()
	{
		final int PORT = 1234;

		try
		{
			host = InetAddress.getLocalHost();
		}

		catch (UnknownHostException uhEx)
		{
			chatroom.append("No such host");
		}

		try
		{
			socket = new Socket(host, PORT);
			input = new Scanner(socket.getInputStream());
			output = new PrintWriter(socket.getOutputStream(), true); 
		}

		catch (IOException ioEx)
		{
			chatroom.append("Error setting up input and output streams");
		}
		
		username = JOptionPane.showInputDialog(null, "Please enter your username :");
		output.println(username);

		Thread listen = new Thread(new Runnable() 
        {
			String messageReceived;

            public void run() {
            	
            	while ((messageReceived = input.nextLine()) != null)
      	        {
            		      	        	
      	        	if(!messageReceived.contains(" : ") && !messageReceived.startsWith("Sending"))
      	        	{
      	        		listOfUsers.append(messageReceived + "\n");
      	        	}      	        	
      	        	if(messageReceived.startsWith("Sending Image"))
      	        	{
      	        		 try
 						{
      	   				inStream = new ObjectInputStream(socket.getInputStream());

 						byte[] byteArray = (byte[])inStream.readObject();

 						FileOutputStream mediaStream;
 				      	
 				      	mediaStream = 
 								new FileOutputStream("ball.png");
 				      	
 				    	mediaStream.write(byteArray);			
 				   	image = new ImageIcon(byteArray);
					
					lblNewLabel.setIcon(image);
 			      }catch (FileNotFoundException | ClassNotFoundException e) 
 						{
 						e.printStackTrace();
 					} 
 					catch (IOException e) 
 					{
 						e.printStackTrace();
 					}
      	        	}
      	        	if(messageReceived.startsWith("Sending audio"))
      	        	{
      	        		try {

    						inStream = new ObjectInputStream(socket.getInputStream());

    						byte[] byteArray = (byte[])inStream.readObject();
    						FileOutputStream mediaStream;
    						
    						mediaStream = 
    								new FileOutputStream("sound.au");
    						
    						mediaStream.write(byteArray);
    						clip = Applet.newAudioClip(new URL("file:sound.au"));
    					} catch (ClassNotFoundException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					} catch (FileNotFoundException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					} catch (MalformedURLException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}	
      	        	}
      	        	else if(messageReceived.contains(" : "))
      	        	{
      	        		
      	        		chatroom.append(messageReceived + "\n");
      				}
      	        } 
            }
        });
		listen.start();
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == send)
		{
			String message = msgToSend.getText();
			output.println(message);
			chatroom.append(username+" : "+ message + "\n");
			msgToSend.setText("");
		}
		
		if(event.getSource() == btnRequestTextFile)
		{
			output.println("TEXT");
			try {	
		
				InputStream is=socket.getInputStream();
				
				int fileLength = is.read();
				
				byte[] byteArray = new byte[fileLength]; 

				
				int bytesRead = 0;
				FileOutputStream fr=new FileOutputStream("exchangerate.txt");
				
				while((bytesRead = is.read(byteArray)) >= 0)
				{
					for (int i = 0; i < bytesRead; i++){
						is.read(byteArray,0,fileLength);
					}
				}
				
				is.close();
				
				fr.write(byteArray, 0, fileLength);
				fr.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(event.getSource() == btnRequestSound)
		{
			output.println("SOUND");
		}
		
		if(event.getSource() == btnRequestImage)
		{
			output.println("IMAGE");	
		}
		
		if(event.getSource() == btnPlaySound)
		{
			clip.play();
		}

		if(event.getSource() == closeConnection)
		{
			output.println("QUIT");
			System.exit(0);
		}
	}
}