package PeerMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Request {
	
	/**
	 * Sends a request for the index piece
	 * @param outStream
	 * @param index
	 * @param begin
	 * @param length
	 * @throws IOException
	 */
	public static void write(DataOutputStream outStream, int index, int begin,
			int length) throws IOException {
		outStream.writeInt(13);
		outStream.writeByte(6);
		outStream.writeInt(index);
		outStream.writeInt(begin);
		outStream.writeInt(length);
	}
	
	/**
	 * Reads the request for a file
	 * @param inStream
	 * @param index
	 * @param begin
	 * @param length
	 */
	public static void read(DataInputStream inStream, int index, int begin,
			int length){
		
	}
}
