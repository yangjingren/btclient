package PeerMessages;

import java.io.*;

public class Choke {

	/**
	 * Chokes the peer if they are not interested
	 * @param outStream
	 * @throws IOException
	 */
	public static void choke(DataOutputStream outStream) throws IOException {
		outStream.writeInt(1);
		outStream.writeByte(0);
	}
	
	/**
	 * Uncokes the peer
	 * @param outStream
	 * @throws IOException
	 */
	public static void unchoke(DataOutputStream outStream) throws IOException{
		outStream.writeInt(1);
		outStream.writeByte(1);
	}
}