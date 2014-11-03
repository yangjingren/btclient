package PeerMessages;

import java.io.*;
import java.security.NoSuchAlgorithmException;

import myTools.*;

public class Piece {
	
	
	/**
	 * Reads the piece of the file and sends back the byte array
	 * @param inStream
	 * @param responseLen
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	@SuppressWarnings("unused")
	public static int read(DataInputStream inStream, int responseLen, byte[] piece_hash,int piece_length, int index ) throws IOException, NoSuchAlgorithmException {
		int block_length, begin, valid;
		block_length = responseLen - 9;
		byte[] block = new byte[block_length];
		
		
		begin = inStream.readInt();
		inStream.readFully(block);
		valid = validate.validator(block, piece_hash);
		if (valid == 1){
			FileStorage.store(index, block, piece_length);
			return 1;
		}
		else{
			return 0;
		}
	}

	/**
	 * Writes to the output stream the piece requested
	 * <len+9><7><index><begin><block>
	 * @param outStream
	 * @param index
	 * @param begin
	 * @param piece_length
	 * @param length
	 * @throws IOException
	 */
	public static void write(DataOutputStream outStream, int index, int begin,
			int piece_length, int length) throws IOException {
		int totallength = length + 9;
		outStream.writeInt(totallength);
		outStream.writeByte(7);
		outStream.writeInt(index);
		outStream.writeInt(begin);
		byte [] piece = new byte[length];
		piece = FileStorage.read(index, begin, piece_length, totallength);
		outStream.write(piece, 0, length);
		FileStorage.up++;
	}
	
	
}
