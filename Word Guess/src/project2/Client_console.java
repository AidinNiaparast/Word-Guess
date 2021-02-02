package project2;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Thread;
import java.net.Socket;

public class Client_console extends Thread{
	private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    
    public Client_console(String serverAddress) throws Exception {
        socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }
    
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		while(true) {
			try {
				String input=in.readLine();
				if(input.startsWith("MESSAGE")) {
					String[] outputs=input.substring(8).split(",");
					for(String output:outputs) 
						System.out.println(output);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	/**
	 * @param args
	 * @throws Exception
	 */
	
	public static void main(String[] args) throws Exception {
		Scanner sc=new Scanner(System.in);
		
		Client_console client=new Client_console("0.0.0.0");
	
		
		 while(true) {
			System.out.println("Please choose your username:");
			String name=sc.nextLine();
			if(name==null||name=="")	continue;
			client.out.println(name);
			if(client.in.readLine().equals("NAME_Valid")) {
				System.out.println("You logged in successfully!");
				break;
			}
			else {
				System.out.println("This name is already taken!");
				continue;
			}	
		}

		client.start();
		
		System.out.println("1- if you want to start a new game write your request in this format: \"NEWGAME\" your_oppenents_name number_of_rounds");
		System.out.println("2- if you want to play with server write \"SERVER NEWGAME\"");
		System.out.println("3- if you want to quit a game write your request in this format: \"QUITGAME\" game_ID");
		System.out.println("4- if you want to log out just write \"LOGOUT\"!");
		
		while(true) {
			String input=sc.nextLine();
			
			if(input!=null&&!input.equals(""))
				client.out.println(input);		
			if(input.equals("LOGOUT"))
				break;
		}
		
		sc.close();
	}
}
 