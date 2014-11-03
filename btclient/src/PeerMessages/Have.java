package PeerMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Have {
	/**
	 * Sends peer ack of valid piece
	 * @param outStream
	 * @param index
	 * @param begin
	 * @param length
	 * @throws IOException
	 */
	public static void write(DataOutputStream outStream, int index) throws IOException {
		outStream.writeInt(5);
		outStream.writeByte(4);
		outStream.writeInt(index);
	}
	
	/**
	 * Useless atm implemented inside of the peerwireprotocol
	 * @param inStream
	 * @throws IOException
	 */
	public static void read(DataInputStream inStream) throws IOException{
		inStream.readInt();
	}
}
