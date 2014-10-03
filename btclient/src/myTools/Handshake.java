package myTools;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.*;
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
	public static Socket write(byte[] info_hash, String peer_id, String ip, Integer port){
	// handshake <protoLen><protocol><reserved><info_hash><peer_id>
		Socket socket;
		//System.out.println(port + ip);
		try {
			socket = new Socket(ip, port);
			//System.out.println("Socket created.\n");
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeByte(protoLen);
			out.write(protocol.getBytes());
			out.write(new byte[reserved]);
			out.write(info_hash);
			out.write(peer_id.getBytes());
			return socket;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
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
	public static Socket read(byte[] info_hash, String peer_id, Socket socket) throws IOException, MessagingException{
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		
		//Read the handshake response and store the result
		int protocol_length;
		byte[] peer_protocol = new byte[protoLen];
		byte[] peer_reserved = new byte[reserved];
		byte[] peer_hash = new byte[20];
		byte[] p_id = new byte[20];
		protocol_length = in.readByte();
		in.readFully(peer_protocol);
		in.readFully(peer_reserved);
		in.readFully(peer_hash);
		in.readFully(p_id);
		
		//Check the handshake response for the correct values
		if (protocol_length != protoLen)
			throw new MessagingException("Different protocol? Corrupt Socket Stream.");
		if (!Arrays.equals(peer_protocol,protocol.getBytes()))
			throw new MessagingException("Different protocol? Corrupt Socket Stream.");
		if (!Arrays.equals(peer_hash, info_hash))
			throw new MessagingException("Different hash? Corrupt Socket Stream ? Wrong file.");
		if (!Arrays.equals(p_id, peer_id.getBytes()))
			throw new MessagingException("Different peer_id? Corrupt Socket Stream ? Wrong peer.");
		
		return socket;
	}
	
}
