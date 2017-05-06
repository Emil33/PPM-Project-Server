/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ppmserver;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Jack_Ultrabook
 */
public class PPMRegistration {
    

  public static void main(String[] args) throws Exception {
    ServerSocket m_ServerSocket = new ServerSocket(9096);
    int id = 0;
    System.out.println("Registration server activated!");
    while (true) {
     Socket clientSocket = m_ServerSocket.accept();
     registrationServerThread regThread = new registrationServerThread(clientSocket, id++);
     regThread.start();
   }
  }
}

class registrationServerThread extends Thread {
  Socket clientSocket;
  int clientID = -1;
  boolean running = true;  
  
  public boolean flagSucceeded = false; // default false
 public String incomingCredentials;
 String username;
 String Class;
 String Name;
 String password;
 String admin;
 boolean succeeded = true;
 

registrationServerThread(Socket s, int i) {
    clientSocket = s;
    clientID = i;
  }

 
  @Override
  public synchronized void run() {
      
    System.out.println("Accepted Client : ID - " + clientID + " : Address - "
    + clientSocket.getInetAddress().getHostName());
   try {
      DataInputStream inFromClient = new DataInputStream(clientSocket.getInputStream());
      DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());     
      incomingCredentials = inFromClient.readUTF();
      BufferedWriter buffer = null;
      File file = new File("users.txt");
      if (incomingCredentials.equals("!REGISTRATION"))
              {
                    outToClient.writeUTF("!active");
                    String userInfo = inFromClient.readUTF();
                    Scanner fileReader = new Scanner(file);
                    String[] splitting = userInfo.split("-");
                     username = splitting[0];
                     Class = splitting[1];
                     Name = splitting[2];                     
                     password = splitting[3];
                     admin = splitting[4];
                    while (fileReader.hasNextLine())
                        {
                    String line = fileReader.nextLine();
                    if (line.contains("<username>" + username + "</username>"))
                        {
                         System.err.println("Already exists!");            
                         outToClient.writeUTF("!FAIL");
                         succeeded = false;
                         break;                       
                        }
                        }
                    if (succeeded)
                    {
                        FileWriter writer = new FileWriter("users.txt", true);
                        buffer = new BufferedWriter(writer);
                        PrintWriter printer = new PrintWriter(buffer);
                        outToClient.writeUTF("!SUCCESS");
                        System.err.println("Started!");
                        printer.println("<user>" + "<username>" + username + "</username> " + "<Class>" + Class + "</Class>" + "<Name>" + Name + "</Name>" + "<password>" + password + "</password>" + "<admin>" + admin + "</admin>" + "</user>");
                        buffer.close();
                    }    
               }
        } catch(IOException e)
        {
            e.printStackTrace();
        }              
    }          
}