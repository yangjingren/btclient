package PeerMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import myTools.FileStorage;

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
		int i = inStream.readInt();
		int j = FileStorage.count.get(i);
		FileStorage.count.set(i, j++);
	}
}
