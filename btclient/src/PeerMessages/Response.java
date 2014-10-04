package PeerMessages;

import java.io.DataInputStream;
import java.io.IOException;

public class Response {
	/**
	 * Returns the length prefix
	 * @param inStream
	 * @return
	 * @throws IOException
	 */
	public static int readLen(DataInputStream inStream) throws IOException {
		int len;
		len = inStream.readInt();
		
		return len;
	}
	
	/**
	 * Returns the message ID
	 * @param inStream
	 * @return
	 * @throws IOException
	 */
	public static int readID(DataInputStream inStream) throws IOException {
		int ID;
		ID = inStream.readByte();
		
		return ID;
	}

}
