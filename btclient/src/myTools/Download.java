package myTools;

import java.io.IOException;
import java.net.Socket;
import java.nio.*;

public class Download {
	// Start the download
	public static void start(Socket socket){
		//initial state for client
		int choked = 1;
		int interested = 0;
		int peer_choked = 1;
		int peer_interested = 0;
		/*
		try {
			//msgHandler.interested(socket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
}
