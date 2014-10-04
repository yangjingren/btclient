package PeerMessages;

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
	
}
