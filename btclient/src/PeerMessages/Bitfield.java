package PeerMessages;

import java.io.DataInputStream;
import java.io.IOException;

public class Bitfield {
	/**
	 * Parse the bitfield
	 * @param inStream
	 * @param responseLen
	 * @throws IOException
	 */
	public static void read(DataInputStream inStream, int responseLen) throws IOException{
		int bitfieldSize = responseLen - 1;
		byte[] bitfield = new byte[bitfieldSize];
		inStream.readFully(bitfield);
	}
	
	public static void write(){
		
	}
}
