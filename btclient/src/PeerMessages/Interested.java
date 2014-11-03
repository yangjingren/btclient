package PeerMessages;

import java.io.*;

public class Interested {

	/**
	 * Tells the peer interested in downloading
	 * @param outStream
	 * @throws IOException
	 */
	public static void write(DataOutputStream outStream) throws IOException {
		outStream.writeInt(1);
		outStream.writeByte(2);
	}
	
	/**
	 * Useless atm the peer wire protocol takes care of this
	 * @param inStream
	 */
	public static void read(DataInputStream inStream) {
		
	}
}
