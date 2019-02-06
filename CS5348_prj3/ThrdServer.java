import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ThrdServer {
	ServerSocket server = null;
	
	public void listenerSocket(int port)
	{
		try
		{
			server = new ServerSocket(port);
			System.out.println("Server running on port " + port + ", use ctrl-C to end");			
		} catch(IOException e){
			System.out.println("Error creating socket");
			System.exit(-1);
		}
		
		while(true)
		{
			ClientWorker w;
			try{
				w = new ClientWorker (server.accept());
				Thread t = new Thread(w);
				t.start();
			}catch(IOException e)
			{
				System.out.println("Accept failed");
				System.exit(-1);
			}
		}
		
	}
	
	protected void finalize()
	{
		try
		{
			server.close();
		} catch(IOException e){
			System.out.println("Could not close socket");
			System.exit(-1);
		}
	}	
	

	static HashMap<String, ArrayList<String>> msgTable = new HashMap<>();
	static HashSet<String> name = new HashSet<>();
	static HashSet<String> online = new HashSet<>();
	
	
	public static void main(String[] args)
	{
		if (args.length != 1)
		{
			System.out.println("Usage: java SocketSerave port");
			System.exit(-1);	
		}
		
		ThrdServer server = new ThrdServer();
		int port = Integer.valueOf(args[0]);
		server.listenerSocket(port);
	}
}
