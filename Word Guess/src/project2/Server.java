package project2;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;


public class Server {
    public static HashSet<String> names = new HashSet<>();
    public static HashMap<String,PrintWriter> writers = new HashMap<>();
    //public static HashMap<String,BufferedReader> readers = new HashMap<>();
    public static HashMap<Integer,Game> games=new HashMap<>();
    
    static int gameCounter=1;
    
    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(9001);
        System.out.println("Server is Running");
        try {
            while (true) {
                Player player=new Player(listener.accept());
                System.out.println("new player connected");
                player.start();
            }
        } finally {
            listener.close();
        }
    }
}