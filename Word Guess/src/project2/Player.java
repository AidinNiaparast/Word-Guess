package project2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import static project2.Server.*;


public class Player extends Thread{
    private String name;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private HashSet <Integer> playerGames;
    
    public Player(Socket socket) {
        this.socket = socket;
        try{
            in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (IOException e) {
            System.out.println(e);
        }
        playerGames=new HashSet<>();
    }
        
    public void quitGame(int gameID){
        String firstPlayer=games.get(gameID).firstPlayerName;
        String secondPlayer=games.get(gameID).secondPlayerName;
        synchronized (writers) {
        	writers.get(firstPlayer).println("Game "+ gameID +" has been finished");
            writers.get(secondPlayer).println("Game "+ gameID +" has been finished");
        }
        synchronized (games) {
            Server.games.remove(gameID);
        }
    }
    
    public void run(){
        try {
            while (true) {
                String name = in.readLine();
                
                if (name == null) {
                    return;
                }
                synchronized (names) {
                    if (!name.equals("")&&(names==null||!names.contains(name))) {
                        this.name=name;
                        names.add(name);
                        synchronized (writers) {
                        	writers.put(name, out);
						}
                        out.println("NAME_Valid");
                        break;
                    }
                    else{
                        out.println("write another name");
                    }
                }
            }

            
            while (true) {
                String input = in.readLine();
                if(input==null||input.equals(""))
                	continue;

                else if(input.startsWith("NEWGAME")){
                	
                    /*
                    for(String name:names){
                        if(!opponents.contains(name)&&name!=this.name){
                            out.println(name);
                        }
                    }*/
                    
                    String opponentName,numberOfRounds;
                    opponentName=input.split(" ")[1];
                    numberOfRounds=input.split(" ")[2];
                    
                    
                    if(opponentName.equals(this.name)) {
                    	synchronized (writers) {
                    		writers.get(name).println("MESSAGE this is your name!");
						}
                    	continue;
                    }
                    
                    synchronized (names) {	
						if(!names.contains(opponentName)){
	                        synchronized (writers) {
	                        	writers.get(name).println("MESSAGE This name ("+opponentName+") doesnt exist");  	
							}
	                        continue;
	                    }
                    }
                    
                    synchronized (writers) {
	                    writers.get(opponentName).println("MESSAGE Player "+name+" wants to play "
	                    		+ numberOfRounds+ " rounds with you!"
	                    		+ " ,The game ID is "+Server.gameCounter 
	                    		+ " ,Do you want to play this game?  (your answer must be YES or NO + gameID)");
	                }
                    Game game=new Game(name,opponentName,Server.gameCounter,Integer.parseInt(numberOfRounds));
                    synchronized (games) {
                    	games.put( Server.gameCounter,game);
                    }
					Server.gameCounter++;
                }
                
                else if(input.startsWith("YES")) {
                	out.println("MESSAGE you want to be first player or second player in game " + input.split(" ")[1] + " ( your answer must be FIRST or SECOND + gameID) ?");
                }
                else if(input.startsWith("NO")) {
                	String opponentsName="";
                	synchronized (games) {
                		opponentsName=games.get(Integer.parseInt(input.split(" ")[1])).getOpponent(this.name);
                		games.remove(Integer.parseInt(input.split(" ")[1]));
                	}
                	synchronized (writers) {
                		Server.writers.get(opponentsName).println("MESSAGE player "+opponentsName+" refused your request");
                	}
                }
                
                else if(input.startsWith("FIRST")) {
                	out.println("MESSAGE please write your word (it should start with WORD + your word + gameID) :");
                	synchronized (games) {
                		games.get(Integer.parseInt(input.split(" ")[1])).turn=name;
					}
                }
                
                else if(input.startsWith("SECOND")) {
                	String opponent="";
                	synchronized (games) {
                		opponent=games.get(Integer.parseInt(input.split(" ")[1])).getOpponent(name);
                    	games.get(Integer.parseInt(input.split(" ")[1])).turn=opponent;
					}
                	synchronized (writers) {
                		writers.get(opponent).println("MESSAGE please write your word (it should start with WORD + your word + gameID) :");
					}	
                }
                
                else if(input.startsWith("WORD")) {
                	int id=Integer.parseInt(input.split(" ")[2]);
                	String opponent="";
                	synchronized (games) {
                		games.get(id).setFixedWord(input.split(" ")[1]);
                		opponent=games.get(id).getOpponent(this.name);
                	}
                	synchronized (writers) {
                		writers.get(opponent).println("MESSAGE guess a letter:  ,The game ID is "+id+ ", it should start with GUESS + a letter + game ID");
					}
                }
                
                else if(input.startsWith("GUESSWORD")) {
                	synchronized (games) {
	                	int id=Integer.parseInt(input.split(" ")[2]);
	                	if(input.split(" ")[1].compareTo(Server.games.get(id).getFixedWord())==0){
	                        out.println("MESSAGE You won game "+ id);
	                        String opponent=Server.games.get(id).getOpponent(name);
	                        if(!opponent.equals("SERVER")) {
	                        	synchronized (writers) {
	                        		writers.get(opponent).println("MESSAGE You lost game "+id);
	                        	}
	                        }
	                    }
	                    else{
	                        out.println("MESSAGE You lost game "+ id);
	                        String opponent=Server.games.get(id).getOpponent(name);
	                        if(!opponent.equals("SERVER")) {
	                        	synchronized (writers) {
	                        		writers.get(opponent).println("MESSAGE You won game "+id);
	                        	}
	                        }
	                    }
	                    Server.games.get(id).round++;
	                    if(Server.games.get(id).round<Server.games.get(id).numberOfRounds){
	                        Server.games.get(id).swapPlayers();
	                        Server.games.get(id).initialize();
	                        String firstPlayer=Server.games.get(id).turn;
	                        synchronized (writers) {
	                        	Server.writers.get(firstPlayer).println("MESSAGE please write your word (it should start with WORD + your word + gameID) :");
	                        }
	                    }
	                    else{
	                    	if(!Server.games.get(id).firstPlayerName.equals("SERVER") && !Server.games.get(id).secondPlayerName.equals("SERVER"))
	                    		quitGame(id);
	                    }
                	}
                }
                
                else if(input.startsWith("GUESS")) {
                	synchronized (games) {
	                	int id=Integer.parseInt(input.split(" ")[2]);
	                	
	                	if(input.split(" ")[1].length()!=1){
	                		out.println("MESSAGE You should guess a letter! , gameID: " + id);
	                	}
	                    else if(Server.games.get(id).hasThisLetter(input.split(" ")[1].charAt(0))){
	                        out.println("MESSAGE This letter is chosen before");
	                    }
	                    else {
		                	 
		                    	games.get(id).setLetter(input.split(" ")[1]);
			                	String opponent=games.get(id).getOpponent(this.name);
			                	if(!opponent.equals("SERVER")) {
				                	synchronized (writers) {
				                		Server.writers.get(opponent).println("MESSAGE current word in game "+id+" is: "+games.get(id).getCurrentWord());
				                	}
			                	}
			                	out.println("MESSAGE current word in game "+id+" is: "+games.get(id).getCurrentWord());
			                	
			                	if(games.get(id).isOver()) {
			                		out.println("MESSAGE guess your opponents word: "
			                		+ ", the game ID is "+id
			                		+ ", your message should start with GUESSWORD + your word + game ID");	
			                	}
			                	else
			                		out.println("MESSAGE guess a letter:  ,The game ID is "+id+ ", it should start with GUESS + a letter + game ID");  
	                    }
                	}
                }
                
                else if(input.startsWith("SERVER")) {
                	if(input.split(" ")[1].equals("NEWGAME")) {
	                	File f=new File("C:\\Users\\Aidin\\eclipse-workspace\\project2\\src\\project2\\words.txt");
	                	Scanner sc=new Scanner(f);
	                	
	                	Random rand=new Random();
	                    int randomNum = rand.nextInt(137);
	                    
	                	int i=0;
	                    String word="";
	                    while(sc.hasNextLine()&&i<randomNum) {
	                    	word=sc.nextLine();
	                    	i++;
	                    }
	                    sc.close();
	                    
	                    System.out.println("The guessed word is " + word);
	                    
	                    Game game=new Game(name,"SERVER",Server.gameCounter,1);
	                    game.setFixedWord(word);
	                    synchronized (games) {
		                    Server.games.put( Server.gameCounter,game);
		                    Server.gameCounter++;
	                    }
	                    
	                    out.println("MESSAGE The chosen word has "+word.length()+" letters "
	                    		+ "," + "guess a letter: "
	                    		+ "," + "The game ID is " + game.ID 
	                    		+ "," + "it should start with GUESS + a letter + game ID");
                	}
                }
                
                else if(input.startsWith("QUITGAME")){
                    int gameID=Integer.parseInt(input.split(" ")[1]);
                    quitGame(gameID);
                }
                else if(input.startsWith("LOGOUT")){
                    for(int gameID:playerGames){
                        quitGame(gameID);
                    }
                    synchronized (names) {
                    	names.remove(name);
					}
                    synchronized (writers) {
                    	writers.remove(name);
                    }
                   /* synchronized (readers) {
                    	readers.remove(name);
					}*/
                    break;
                }
                else {
                	out.println("MESSAGE Your message format is incorrect!");
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }
}