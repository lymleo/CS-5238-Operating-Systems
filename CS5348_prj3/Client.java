import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
	
	
	private String menu = "1. Display the names of all known users.\n" + "2. Display the names of all currently connected users.\n" + "3. Send a text message to a particular user.\n" +"4. Send a text message to all currently connected users.\n" +"5. Send a text message to all known users.\n" +"6. Get my messages.\n" +"7. Exit. \n";
	private int choice;
	
	private String line; //message from socketServer
	
	//for input string processing
	private String pattern;
	private Pattern P;
	private Matcher mat;
	private int i;
	
	Socket socket = null;
	PrintWriter out = null;
	BufferedReader in = null;
	
	
	public void communicate()
	{
		passName();
		
		try {
			//test communication by get client name back
			line = in.readLine();
			
			if (line.equals("overflowUser")) //maximum of 100 known users
				System.out.println("Reached max user capacity.");
			else
			{
				System.out.println("Hi " + line);
				
				System.out.println(menu);
				
				while (true)
				{
					//Then ask user to make choice
					choice = makeChoice();
					if(choice == 0)
					{
						System.out.println("Invalid Input\n");
						System.out.println(menu);
					}
					else
						execChoice(choice);
						System.out.println(menu);
				}
			}
		} catch (IOException e) {
			
			System.out.println("Read failed");
			System.exit(1);
		}

	}
	
    public void listenSocket(String host, int port)
    {
        //Create socket connection
        try
        {
	      socket = new Socket(host, port);
	      out = new PrintWriter(socket.getOutputStream(), true);
	      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }
        catch (UnknownHostException e)
        {
	      System.out.println("Unknown host");
	      System.exit(1);
        }
        catch (IOException e)
        {
	      System.out.println("No I/O");
	      System.exit(1);
        }
    }
 
	// get this client's name and pass to server;
	public void passName()
	{
		Scanner sc = new Scanner (System.in);
		System.out.println("Please enter your name: ");
		String name  = sc.nextLine();
		
		
		// pass name to the server;
		out.println(name);
	}
	
	
	private int count = 0;
	HashMap<Character, Integer> m = new HashMap<>();
	public int makeChoice()
	{
		//link each choice with a number when first time call this method
		if (count == 0)
		{
			
			for (int i = 0; i < 7; i++)
				m.put((char)(49+i), i+1);
			count++;
		}
		
		//then read choice from user
		System.out.println("Enter your choice: ");
		Scanner sc = new Scanner(System.in);
		char ch = sc.next().charAt(0);		
		
		
		if (!m.containsKey(ch))
			return 0;
		else
		{
			int choice = m.get(ch); 
			return choice;
		}
	}
	
	public void execChoice(int choice)
	{
		try
		{
			switch(choice)
			{
				
				case 1: //a. Display the names of all known users.
					out.println(Integer.toString(choice));
					line = in.readLine();
					pattern = "[^\\t]+";
					P = Pattern.compile(pattern);
					mat = P.matcher(line);
					i = 1;
					System.out.println("Known users:");
					while(mat.find())
					{
						System.out.println("\t" + i + ". " + mat.group());
						i++;
					}
					System.out.println("");
					break; 
				
				
				case 2: //b. Display the names of all currently connected users.
					out.println(Integer.toString(choice));
					line = in.readLine();
					pattern = "[^\\t]+";
					P = Pattern.compile(pattern);
					mat = P.matcher(line);
					i = 1;
					System.out.println("Connected users:");
					while(mat.find())
					{
						System.out.println("\t" + i + ". " + mat.group());
						i++;
					}
					System.out.println();
					break; 
				
				
				case 3:  //c. Send a text message to a particular user.
					out.println(Integer.toString(choice));
					
					//read prompt from server;
					line = in.readLine();
					//output receiver name
					System.out.println(line);
					
					Scanner sc1 = new Scanner(System.in);
					//input receiver's name
					String rname = sc1.nextLine();
					out.println(rname);
					
					//read prompt from server
					line = in.readLine();
					System.out.println(line);
					
					boolean msgerr3 = false;
					//input message;
					String msg3 = sc1.nextLine();
					if(msg3.length() > 80)
					{
						msgerr3 = true;
						out.println(msgerr3);
						System.out.println("Invalid message: all messages should be less than 80 characters\n");
					}
					else
					{
						out.println(msgerr3);
						out.println(msg3);
						
						String contains3 = in.readLine();
						
						String isfull3 = in.readLine();
						
						if(contains3.equals("true") && isfull3.equals("true"))
						{
							System.out.println("this user reach limit of 10 messages\n");
						}
						else
						{
							//read prompt from server
							line = in.readLine();
							if(line.equals("name list full"))
								System.out.println("There are already 50 known users exists, you can not add one more known user\n");
							else
								System.out.println(line + "\n");
							
						}
						
					}
					
					break;
					
				
				
				case 4: //d. Send a text message to all currently connected users.
					out.println(Integer.toString(choice));
					
					//read prompt
					line = in.readLine();
					System.out.println(line);
					boolean msgErr4 = false;
					
					//input message
					Scanner sc4 = new Scanner(System.in);
					String msgCurr4 = sc4.nextLine();
					if(msgCurr4.length() >= 80) //invalid message
					{
						msgErr4 = true;
						out.println(msgErr4);
						System.out.println("Invalid message: all messages should be less than 80 characters\n");
					}
						
					else //valid message
					{
						out.println(msgErr4);
						
						//read name list is full
						line = in.readLine();
						if(line.equals("true"))
						{
							
							line = in.readLine();
							System.out.println("message send failed");
							System.out.println(line + "can not hold more than 10 messages\n");
						}
							
						else //all expected users can get their message
						{
							//output message
							out.println(msgCurr4);
							//read prompt
							line = in.readLine();
							System.out.println(line);	
						}
						
					}
					System.out.println();
					break;
				
				
				case 5: //e. Send a text message to all known users.
					out.println(Integer.toString(choice));
					
					//read prompt
					line = in.readLine();
					System.out.println(line);
					boolean msgErr = false;
					
					//input message
					Scanner sc5 = new Scanner(System.in);
					String msgCurr = sc5.nextLine();
					if(msgCurr.length() >= 80) //invalid message
					{
						msgErr = true;
						out.println(msgErr);
						System.out.println("message can not be more than 80 characters\n");
					}
						
					else //valid message
					{
						out.println(msgErr);
						
						//read name list is full
						line = in.readLine();
						if(line.equals("true"))
						{
							
							line = in.readLine();
							System.out.println("message send failed");
							System.out.println(line + "can not hold more than 10 messages\n");
						}
							
						else //all expected users can get their message
						{
							//output message
							out.println(msgCurr);
							//read prompt
							line = in.readLine();
							System.out.println(line);	
						}
						
					}
					System.out.println();			
					break;
				
				
				case 6: //f. Get my messages.
					out.println(Integer.toString(choice));
					
					//read prompt
					line = in.readLine();
					System.out.println(line);
					
					//read size if message
					int size =Integer.parseInt(in.readLine()) ;
					//get messages from server
					if (size < 1)
						System.out.println("You don't hava any unread message.\n");
					else
					{
						for (int j = 1; j <= size; j++)
						{
							line = in.readLine();
							System.out.println("\t" + j + ". " + line);
						}
					}
					break;
				
				 
				case 7://g. exit
					out.println(Integer.toString(choice));
					System.out.println("Good bye");
					while(choice == 7){ }
					break;
			}
			
		} catch(Exception e){
			System.out.println("Choice execution failed");
		}
	}
	
	public static void main(String[] args)
	{
		 if (args.length != 2)
		    {
			System.out.println("Usage:  client hostname port");
			System.exit(1);
		    }
		 
		 Client client = new Client();
		 String host = args[0];
		 int port = Integer.parseInt(args[1]);
		 client.listenSocket(host, port);
		 client.communicate();

	}
	
	

}
