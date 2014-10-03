package myTools;

import java.math.BigInteger;
import java.security.*;
import java.util.Formatter;
import java.util.Random;

public class Util {

	//convert info_hash to hex
		public static String toHex(byte[] bytes) {
		    BigInteger bi = new BigInteger(1, bytes);
		    return String.format("%0" + (bytes.length << 1) + "X", bi);
		}
	
		
		/**
		 * Returns a pseudo-random number between min and max, inclusive.
		 * The difference between min and max can be at most
		 * <code>Integer.MAX_VALUE - 1</code>.
		 *
		 * @param min Minimum value
		 * @param max Maximum value.  Must be greater than min.
		 * @return Integer between min and max, inclusive.
		 * @see java.util.Random#nextInt(int)
		 */
		public static int randInt(int min, int max) {

		    // NOTE: Usually this should be a field rather than a method
		    // variable so that it is not re-seeded every call.
		    Random rand = new Random();

		    // nextInt is normally exclusive of the top value,
		    // so add 1 to make it inclusive
		    int randomNum = rand.nextInt((max - min) + 1) + min;

		    return randomNum;
		}	
		
	/**
	 * Converts the hash to a format readable by the tracker
	 * Taken from somewhere not sure exactly who the author is too many online
	 * @param in
	 * @return
	 */
	public static String byteArrayToURLString(byte in[]) {
	    byte ch = 0x00;
	    int i = 0;
	    if (in == null || in.length <= 0)
	      return null;

	    String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
	        "A", "B", "C", "D", "E", "F" };
	    StringBuffer out = new StringBuffer(in.length * 2);

	    while (i < in.length) {
	      // First check to see if we need ASCII or HEX
	      if ((in[i] >= '0' && in[i] <= '9')
	          || (in[i] >= 'a' && in[i] <= 'z')
	          || (in[i] >= 'A' && in[i] <= 'Z') || in[i] == '$'
	          || in[i] == '-' || in[i] == '_' || in[i] == '.'
	          || in[i] == '!') {
	        out.append((char) in[i]);
	        i++;
	      } else {
	        out.append('%');
	        ch = (byte) (in[i] & 0xF0); // Strip off high nibble
	        ch = (byte) (ch >>> 4); // shift the bits down
	        ch = (byte) (ch & 0x0F); // must do this is high order bit is
	        // on!
	        out.append(pseudo[(int) ch]); // convert the nibble to a
	        // String Character
	        ch = (byte) (in[i] & 0x0F); // Strip off low nibble
	        out.append(pseudo[(int) ch]); // convert the nibble to a
	        // String Character
	        i++;
	      }
	    }

	    String rslt = new String(out);

	    return rslt;

	  }
	
	public static String SHAsum(byte[] convertme) throws NoSuchAlgorithmException{
	    MessageDigest md = MessageDigest.getInstance("SHA-1"); 
	    return byteArray2Hex(md.digest(convertme));
	}

	private static String byteArray2Hex(final byte[] hash) {
	    Formatter formatter = new Formatter();
	    for (byte b : hash) {
	        formatter.format("%02x", b);
	    }
	    return formatter.toString();
	}
}
