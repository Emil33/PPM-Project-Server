
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppmserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.*;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;
import java.util.*;

/**
 *
 * @author Jack_Ultrabook
 */
public class PPMServer {

    public static String connectedUsernames = "";
    public static void main(String[] args) throws Exception {
    ServerSocket m_ServerSocket = new ServerSocket(9091);
    int id = 0;
    System.out.println("Login/Logout server activated!");
    while (true) {
      Socket clientSocket = m_ServerSocket.accept();
      ClientServiceThread cliThread = new ClientServiceThread(clientSocket, id++);
      cliThread.start();
    }
  }    
}

class ClientServiceThread extends Thread {
   Socket clientSocket;
   int clientID = -1;
   boolean running = true;
   public int usersConnected = 0;  
   public String connectedUsernamesReturn;
   public int friendsConnected;
   public boolean flagSucceeded = false;
   public boolean isDuplicate;
   public static String incomingCredentials;
   public static String incomingRequest;

  ClientServiceThread(Socket s, int i) {
    clientSocket = s;
    clientID = i;
  }
@Override
  public void run() {
      
    System.out.println("Accepted Client : ID - " + clientID + " : Address - "
        + clientSocket.getInetAddress().getHostName());
    try {
      DataInputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());        
    while (running) {
          
         
    incomingRequest = inFromClient.readUTF();
    System.out.println(incomingRequest);
    outToClient.writeUTF("RequestRecieved");   
        
    if(incomingRequest.equals("!LOGIN")) 
    {
        incomingCredentials = inFromClient.readUTF();
        System.out.println(incomingCredentials);        
        
        
            File file = new File("users.txt");             
            Scanner fileReader = new Scanner(file);
            
            System.out.println("SERVER:Recieved logon request...authenticating");
            String[] splitting = incomingCredentials.split("-");
            String username = splitting[0];
            String password = splitting[1]; 
            while (fileReader.hasNextLine())
            {
                String line = fileReader.nextLine();
                if (line.contains("<username>" + username + "</username>") && line.contains("<password>" + password + "</password>"))
                {
                    System.err.println("User found!");
                    outToClient.writeUTF("true");                    
                    PPMServer.connectedUsernames += (username + ":");
                    System.out.println(PPMServer.connectedUsernames);
                    usersConnected++;
                    flagSucceeded = true; 
                    System.err.println("User logged in");
                    break;
                }
            }
            if (!flagSucceeded)
            {
                outToClient.writeUTF("false");               
            }
     
    }
    else if(incomingRequest.equals("!LOGOUT"))
    {
        incomingCredentials = inFromClient.readUTF();
        String[] userSplit = incomingCredentials.split("-");
        String username = userSplit[0];
        PPMServer.connectedUsernames = "";    
        System.out.println(username);         
            
            System.out.println("SERVER:Recieved Logout request...authenticating");
            String[] splitting = PPMServer.connectedUsernames.split(":");
            for (int i = 0; i < splitting.length; i++)
            {
                System.out.println(PPMServer.connectedUsernames); 
                if(splitting[i].equals(username))
                {                   
                   splitting[i] = "";
                }
                System.out.println(splitting[i]); 
                PPMServer.connectedUsernames += splitting[i];
            }            
            usersConnected = usersConnected - 1;
            System.out.println(PPMServer.connectedUsernames);
            System.out.println("User logged out");         
                     
        }    
    } 
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}