package PeerMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import myTools.FileStorage;

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
	 * @param length2 
	 * @throws InterruptedException 
	 */
	public static void read(DataInputStream inStream, int piece_length, DataOutputStream outStream) throws IOException, InterruptedException {
		int index = inStream.readInt();
		int begin = inStream.readInt();
		int length = inStream.readInt();
		if(FileStorage.progress.get(index) == true){
				System.out.println("Sent piece: " +index);
				//Sends the piece of the file
				Piece.write(outStream, index, begin, piece_length, length);
		}
	}
}
