package PeerMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Cancel {
	/**
	 * cancels a request for the index piece
	 * @param outStream
	 * @param index
	 * @param begin
	 * @param length
	 * @throws IOException
	 */
	public static void write(DataOutputStream outStream, int index, int begin,
			int length) throws IOException {
		outStream.writeInt(13);
		outStream.writeByte(8);
		outStream.writeInt(index);
		outStream.writeInt(begin);
		outStream.writeInt(length);
	}
	
	/**
	 * reads a cancel
	 * @param outStream
	 * @throws IOException
	 */
	public static void read(DataInputStream inStream) throws IOException {
		inStream.readInt();
		inStream.readInt();
		inStream.readInt();
		
	}
}
