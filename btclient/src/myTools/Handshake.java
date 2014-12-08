package myTools;

import java.io.*;
import java.net.UnknownHostException;
import java.util.Arrays;
		
public class Handshake{
	
	public final static String protocol = "BitTorrent protocol";
	public final static Integer reserved = 8;
	public final static Integer protoLen = protocol.length();

	/**
	 * Send handshake to peer function
	 * @param info_hash
	 * @param peer_id
	 * @param ip
	 * @param port
	 * @return
	 */
	public static void write(byte[] info_hash, String peer_id , DataOutputStream outStream){
	// handshake <protoLen><protocol><reserved><info_hash><peer_id>
		try {
			outStream.writeByte(protoLen);
			outStream.write(protocol.getBytes());
			outStream.write(new byte[reserved]);
			outStream.write(info_hash);
			outStream.write(peer_id.getBytes());
			//System.out.println(protoLen + "\n" + protocol + "\n" + info_hash+ "\n" + peer_id);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Read the return handshake from the peer and validate it
	 * @param info_hash
	 * @param peer_id
	 * @return
	 * @throws IOException 
	 * @throws MessagingException 
	 */
	public static void read(byte[] info_hash, DataInputStream inStream) throws IOException, MessagingException{
		//Read the handshake response and store the result
		int protocol_length;
		byte[] peer_protocol = new byte[protoLen];
		byte[] peer_reserved = new byte[reserved];
		byte[] peer_hash = new byte[20];
		byte[] p_id = new byte[20];
		protocol_length = inStream.readByte();
		inStream.readFully(peer_protocol);
		inStream.readFully(peer_reserved);
		inStream.readFully(peer_hash);
		inStream.readFully(p_id);
		//System.out.println("Protocol length: " + protocol_length + "\nPeer_protocol: " + peer_protocol +
		//		"\nPeer reserved: " +peer_reserved + "\nPeer_hash: " + peer_hash + "\nPeer_id: " + p_id);
		//Check the handshake response for the correct values
		if (protocol_length != protoLen)
			throw new MessagingException("Different protocol? Corrupt Socket Stream.");
		if (!Arrays.equals(peer_protocol,protocol.getBytes()))
			throw new MessagingException("Different protocol? Corrupt Socket Stream.");
		if (!Arrays.equals(peer_hash, info_hash))
			throw new MessagingException("Different hash? Corrupt Socket Stream ? Wrong file.");
		/*
		if (!Arrays.equals(p_id, peer_id.getBytes()))
			throw new MessagingException("Different peer_id? Corrupt Socket Stream ? Wrong peer.");
			*/
	}
	
}
