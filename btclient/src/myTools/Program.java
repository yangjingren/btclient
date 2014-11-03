package myTools;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import GivenTools.BencodingException;

public class Program implements Runnable{
	public static boolean running =true;
	private static Object mutex = new Object();
	
	Program(){
	}
	
	/**
	 * starts the thread for shutdown
	 */
	public static void initialize(){
		(new Thread(new Program())).start();
	}
	
	/**
	 * Check if the Application is still alive
	 * @return
	 */
	public static boolean isRunning(){
		boolean alive;
		synchronized(mutex){
			alive = running;
		}
		return alive;
	}
	
	/**
	 * Signals that the application should stop
	 */
	public static void stop(){
		synchronized(mutex){
			running = false;
		}
	}
	
	/**
	 * Runs the local command scanner to test for the input q and quit upon receipt
	 */
	public void run(){
			Scanner scanner = new Scanner(System.in);
			String command = "";
		    Boolean invalid = true;
			while (invalid) {
				//check for input
		        if (scanner.hasNext()) {
		            command = scanner.next();
		            System.out.println(command);
		            if (command.trim().equals("q")) {
		            	//signals to all threads program is exiting
		                Program.stop();
		                try {
		                	//clean up sockets
							PeerWireServer.SSocket.close();
							Integer numPeers =PeerWireProtocol.socketQueue.size();
							Socket temp;
							for (int i = 0; i < numPeers; i++){
								temp = PeerWireProtocol.socketQueue.get(i);
								if (temp.isClosed())
									;
								else
									temp.close();
							}
							//update the log
							Writer update;
							try {
								update = new FileWriter("log.txt");
								Integer totalTime = (int) (Logger.totalTime + Logger.getRunTime());
								update.write(FileStorage.up.toString());
								update.write("\r\n");
								update.write(totalTime.toString());
								update.write("\r\n");
								update.close();
								
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		                break;
		            } else {
		                scanner.next();
		            }
		        }
		    }
		    scanner.close();
		    try {
		    	//clean up the file pointer and signal to tracker we are done
		    	TrackerResponse.trackerStop();
				FileStorage.closeFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BencodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
}
