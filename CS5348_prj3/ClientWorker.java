import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;

class ClientWorker implements Runnable
{
	BufferedReader in;
	PrintWriter out;
	private Socket client;
	private String line;
	private  String receiver;
	private String msg;// to store message that this user want to send
	private String back = ""; //String that send message back to client
	private String me; //Store the name of current client
	private ArrayList<String> ms = new ArrayList<>();
    static Semaphore sem = new Semaphore(1, true); //to protect critical section
	
	final int maxUser = 100;

	//Constructor
	ClientWorker (Socket client)
	{
		this.client = client;
	}
	
	public void run() {
		
		try 
		{
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(),true); 
		}catch(IOException e){
			System.out.println("in or out failed");
			System.exit(-1);
		}
		
		try //receive message from client and check the message is name or choice;
		{
			while(true)
			{
				//Receive text from client;
				line = in.readLine();
				
				if (line == null)
					{}
				else
				{
					if(isInt(line)) //a client make a choice
					{
						int choice = Integer.parseInt(line);
						if (choice < 1 || choice > 7)
							System.out.println("Choice passing error");
						else // in this situation client make a valid choice from a to g
						{
							try {
								sem.acquire();
								exec(choice);
								sem.release();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								System.out.println("Failed mutual exclustion");
							}
						}
					}
					
					else // in this situation a client want to connects to the server
					{
						try {
							
							sem.acquire();
							me = line;
							// check if that client is known							
							if(!nameKnown(me)) //want to add a unknown user that already have more than 50 users known;
							{
								if (ThrdServer.name.size() >= maxUser)
									
									out.println("overflowUser");
								else 
								{
									ThrdServer.name.add(me); //add user name to ArrayList name and show this unknown user
									ThrdServer.online.add(me);
									ThrdServer.msgTable.put(me, ms);
									System.out.println(time() + ", Connection by unknown user " + me);
									out.println(me);
								}
							}	
							
							else //known user wants to connect
							{
								System.out.println(time() + ", Connection by known user " + me);  
								ThrdServer.online.add(me);
								out.println(me);
							}
							sem.release();	
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							System.out.println("Failed mutual exclustion");
						}
					}
				}
			}
		}catch(IOException e){
			System.out.println("Read failed");
			System.exit(-1);
		}
		
		try
		{
			client.close();
		} catch(IOException e){
			System.out.println("Close failed");
			System.exit(-1);
		}		
	}
	
	public void exec(int choice)
	{
		try
		{
			switch(choice)
			{
				case 1: //a. Display the names of all known users.
					for (String str:ThrdServer.name)
						back += str + "\t"; //there add a 9 for pattern recognition
					out.println(back);     
					System.out.println(time() + ", " + me + " displays all known users.");
					back = "";
					break;
				
				case 2: //b. Display the names of all currently connected users.
					for(String str:ThrdServer.online)
						back += str + "\t";
					out.println(back);  
					System.out.println(time() + ", " + me + " displays all currently connected users.");
					back = "";
					break;
					
				case 3: //c. Send a text message to a particular user.
					out.println("Enter recipient's name: ");
					line = in.readLine();
					receiver = line;
					
					boolean contains3 = ThrdServer.name.contains(receiver);
					boolean isfull3 = false;
					
					if(contains3 == true)
					{
						isfull3 = (ThrdServer.msgTable.get(receiver).size() > 9);
					}
					
					out.println("Enter a message: ");
					
					//receive msg is invalid
					line = in.readLine();
					if (line.equals("true"))
					{
						//if invalid msg do nothing
					}
					else
					{
						line = in.readLine();
						msg = "From " + me + ", " + time() + ", " + line;
						
						//output if receiver contains in the list and if message is full
						out.println(contains3);
						out.println(isfull3);
									
						if (!contains3)
						{
							if(ThrdServer.name.size() >= maxUser)
								out.println("name list full");
							else
							{
								ArrayList<String> ms1 = new ArrayList<>();
								// unknown user						
								ThrdServer.name.add(receiver); //make this user known
								ThrdServer.msgTable.put(receiver, ms1); // add this user to message table
								ThrdServer.msgTable.get(receiver).add(msg); // add the message to the message table
								out.println("Message posted to " + receiver);
								System.out.println(time() + ", " + me + " posts a message for " + receiver);
							}
						}
						else if (isfull3 == true)
						{
							System.out.println("fuck!");
							// if that user have more than 10 message do nothing
						}
						else
						{System.out.println("fuck2");
							ThrdServer.msgTable.get(receiver).add(msg);
							out.println("Message posted to " + receiver);
							System.out.println(time() + ", " + me + " posts a message for " + receiver);

						}
					}
				
					
					
					break;
				
				case 4: //d.Send a text message to all currently connected users.
					out.println("Enter a message: ");
					
					boolean isfull4 = false;
					int count4 = 0;
					
					//get message status
					line = in.readLine();
					
					if(line.equals("true")) // error message
					{
						// if invalid message do nothing
					}
					else //valid message
					{
						for(String str:ThrdServer.online)
						{
							if (ThrdServer.msgTable.get(str).size() > 9)
							{
								if (count4 == 0)
									back += str;
								else
									back += ", " + str;
								isfull4 = true;
							}
							count4++;
						}
						out.println(isfull4);
						if(isfull4 == true)
							out.println(back);
						else	
						{
							//read message
							line = in.readLine();
							msg = "From " + me + ", " + time() + ", " + line; 
							for(String str:ThrdServer.online)
								if(!me.equals(str))
									ThrdServer.msgTable.get(str).add(msg);
								
							out.println("Message posted to to all known users ");
							System.out.println(time() + ", " + me + " posts a message for all known users.");
						}
						back = "";
					}
					break;
					
				case 5: //e. Send a text message to all known users.
					out.println("Enter a message: ");
					
					boolean isfull5 = false;
					int count = 0;
					
					//get message status
					line = in.readLine();
					
					if(line.equals("true")) // error message
					{
						// if invalid message do nothing 
					}
					else //valid message
					{
						for(String str:ThrdServer.name)
						{
							if (ThrdServer.msgTable.get(str).size() > 9)
							{
								if (count == 0)
									back += str;
								else
									back += ", " + str;
								isfull5 = true;
							}
							count++;
						}
						out.println(isfull5);
						if(isfull5 == true)
							out.println(back);
						else	
						{
							//read message
							line = in.readLine();
							msg = "From " + me + ", " + time() + ", " + line; 
							for(String str:ThrdServer.name)
								if(!me.equals(str))
									ThrdServer.msgTable.get(str).add(msg);
								
							out.println("Message posted to to all known users ");
							System.out.println(time() + ", " + me + " posts a message for all known users.");
						}
						back = "";
					}
					break;
					
				case 6: //f. Get my messages.
					out.println("my messages: ");
					ArrayList<String> ms2 = ThrdServer.msgTable.get(me);
					
					int size = ms2.size();
					//output size of message to the client
					out.println(size);
					
					for (String str : ms2)
						out.println(str);
					
					ms2.clear();
					System.out.println(time() + ", " + me + " gets messages.");
					break;
					
				case 7: //g. Exit
					ThrdServer.online.remove(me);	
					System.out.println(time() +", "+ me + " exits.");
					break;
				
			}
			
		} catch(IOException e){
			System.out.println("Failed choice execution");
		}
		
	}
	
	public boolean isInt(String s)
	{
		 try
	        {
	        	Integer.parseInt(s);
	        }catch(NumberFormatException e){
	        	return false;
	        }catch(NullPointerException e ){
	        	return false;
	        }
		 return true;
	}
	
	public boolean nameKnown(String s)
	{
		return ThrdServer.name.contains(s);
	}
	

	
	public String time()
	{
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy, h:mm:ss a");
        String t = format.format(new Date());
        return t;
	}
	
}