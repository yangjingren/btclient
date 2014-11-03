package PeerMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

import myTools.FileStorage;

public class Bitfield {
	/**
	 * Parse the bitfield
	 * @param inStream
	 * @param responseLen
	 * @param peerPieceList 
	 * @throws IOException
	 */
	public static void read(DataInputStream inStream, int responseLen, int pieces, ArrayList<Boolean> peerPieceList) throws IOException{
		int bitfieldSize = responseLen - 1;
		byte[] bitfield = new byte[bitfieldSize];
		inStream.readFully(bitfield);
		int i = 0;
		//Record the pieces the other peer has
		while (i<pieces){
			if(getBit(bitfield, i)==1)
				peerPieceList.set(i,true);
			else
				peerPieceList.set(i,false);
			i++;
		}
	}
	
	/**
	 * Writes current parts into a bitfield and sends it
	 * @param outStream
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static void write(DataOutputStream outStream) throws IOException, InterruptedException{
		int remainder = FileStorage.progress.size()%8;
		if (remainder!=0)
			remainder = 8-remainder;
		int size = remainder + FileStorage.progress.size();
		BitSet bitReturn = new BitSet(size);
		int i =0;
		while (i<(FileStorage.progress.size())){
			bitReturn.set(i, FileStorage.progress.get(i));
			i++;
		}
		byte [] test = new byte [size/8];
		byte [] copy = new byte [size/8];
		test = bitReturn.toByteArray().clone();
		int k =0;
		int j = 0;
		for (j =0;j<test.length; j++){
			copy[j] = test[j];
		}
		for (k =0; k< size/8;k++){
			copy[k] = reverseBitsByte(copy[k]);
		}
		
		outStream.writeInt(size/8+1);
		outStream.writeByte(5);
		outStream.write(copy);
	}
	
	/**
	 * get the Bits from the bitfield
	 * @param data
	 * @param pos
	 * @return
	 */
	private static int getBit(byte[] data, int pos) {
	      int posByte = pos/8; 
	      int posBit = pos%8;
	      byte valByte = data[posByte];
	      int valInt = valByte>>(8-(posBit+1)) & 0x0001;
	      return valInt;
	}
	
	/**************************************************************************
	 * Author: Isai Damier
	 * Title: Reverse Bits of Byte (8-bit)
	 * Project: geekviewpoint
	 * Package: algorithms
	 *
	 * Statement:
	 *   Given an integer, reverse its bit sequence.
	 *
	 * Sample Input:  00100110
	 * Sample Output: 01100100
	 *
	 * Technical Details:
	 *   It is necessary to know whether the decimal number being passed as
	 *   input is of type byte (8-bit) or short (16-bit) or int (32-bit) or
	 *   long (64-bit): because Java will discard leading zeroes. For instance,
	 *   if x = 0011010, Java will trim it to 11010 and then cause the reverse
	 *   to look like 1011. Under such circumstances the reverseBits operation
	 *   would not be reversible.
	 *
	 *   To keep things simple, the presented algorithm treats short (8-bit)
	 *   inputs.
	 *   
	 *   NOTES Revised for usage, the concept was right but > should have been
	 *   >= Yang.
	 **************************************************************************/
	 private static byte reverseBitsByte(byte x) {
	  int intSize = 8;
	  byte y = 0;
	  for(int position=intSize-1; position>=0; position--){
	    y+=((x&1)<<position);
	    x >>= 1;
	  }
	  return y;
	}
}
