package myTools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class validate {
	/**
	 * Check hash of recieved block with the original hash
	 * @param block
	 * @param piece_hash
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static int validator(byte[] block, byte[] piece_hash) throws NoSuchAlgorithmException{
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
