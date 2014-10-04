package PeerMessages;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import myTools.FileStorage;
import myTools.Util;

public class Piece {
	
	
	/**
	 * Reads the piece of the file and sends back the byte array
	 * @param inStream
	 * @param responseLen
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	public static int read(DataInputStream inStream, int responseLen, byte[] piece_hash,int piece_length ) throws IOException, NoSuchAlgorithmException {
		int block_length, index, begin,valid;
		block_length = responseLen - 9;
		byte[] block = new byte[block_length];
		
		index = inStream.readInt();
		begin = inStream.readInt();
		inStream.readFully(block);
		int offset = index*piece_length;
		valid = validate(block, piece_hash);
		if (valid == 1){
			FileStorage.store(index, block, piece_length);
			return 1;
		}
		else{
			return 0;
		}
	}
	
	/**
	 * Check hash of recieved block with the original hash
	 * @param block
	 * @param piece_hash
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static int validate(byte[] block, byte[] piece_hash) throws NoSuchAlgorithmException{
		int valid;
		byte[] block_hash = new byte[20];
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		block_hash = md.digest(block);
		
		if(Arrays.equals(block_hash, piece_hash))
			valid = 1;
		else
			valid = 0;
		return valid;
	}
}
