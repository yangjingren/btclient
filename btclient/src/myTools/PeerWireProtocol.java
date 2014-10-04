package myTools;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import PeerMessages.Bitfield;
import PeerMessages.Have;
import PeerMessages.Interested;
import PeerMessages.Piece;
import PeerMessages.Request;
import PeerMessages.Response;

public class PeerWireProtocol {
	
	/**
	 * Opens the socket to peer and initiates the messaging protocol
	 * @param info_hash
	 * @param client_id
	 * @param ip
	 * @param port
	 * @param peer_id
	 * @param piece_hashes
	 * @param piece_length
	 * @param file_length
	 * @throws IOException 
	 * @throws MessagingException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static void start(byte[] info_hash, String client_id, String ip, Integer port, String peer_id, 
			ByteBuffer[] piece_hashes, Integer piece_length, Integer file_length) throws IOException, MessagingException, NoSuchAlgorithmException{
		
		
		//Initiate prefix_length, msg_id, file_parameters, peer/client_parameters
		int responseLen, responseID, completed, valid;
		int client_choke, client_interested, peer_choke, peer_interested;
		int index, begin, length, numPieces;
		index = begin = 0;
		length = file_length%piece_length;
		numPieces = file_length/piece_length;	
		if (length > 0){
			numPieces++;	
		}
		
		//Initiate client and peer parameters to defaults
		client_choke = peer_choke = 1;
		client_interested = peer_interested = 0;
		completed = 0;
		
		//Open the data streams and start the connection to peer
		Socket socket = new Socket(ip, port);
		System.out.println("Open outputStream");
		DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
		System.out.println("Open inputStream");
		DataInputStream inStream = new DataInputStream(socket.getInputStream());
		System.out.println("Sending handshake");
		Handshake.write(info_hash, client_id, ip, port, outStream);
		System.out.println("Sent handshake");
		Handshake.read(info_hash, peer_id, inStream);
		System.out.println("Handshake success");
		
		System.out.println("Sending interested");
		Interested.write(outStream);
		System.out.println("Sent interested");
		
		System.out.println("Download Start");
		while (completed == 0){
			
			responseLen = Response.readLen(inStream);
			if (responseLen == 0){
				//keep connection open
			}
			else if (responseLen >= 1){
				responseID = Response.readID(inStream);
				switch (responseID){
					case 0://Choke <len = 0001><id = 0>
						System.out.println("Peer choked");
						peer_choke = 1;
						break;
					case 1://Unchoke <len = 0001><id = 1>
						System.out.println("Peer unchoked");
						peer_choke = 0;
						break;
					case 2://Interested <len = 0001><id = 2>
						peer_interested = 1;
						break;
					case 3://Uninterested <len = 0001><id = 3>
						peer_interested = 0; 
						break;
					case 4://Have <len = 0005><id = 4><piece_index>
						if(responseLen!= 5){
							throw new MessagingException("Message not properly formatted?Corrupt socket?");
						}
						break;
					case 5://Bitfield <len = 0001 + X><id = 5><bitfield>
						Bitfield.read(inStream, responseLen);
						break;
					case 6://Request <len = 0013><id = 6><index><begin><length>
						if(responseLen!= 13){
							throw new MessagingException("Message not properly formatted?Corrupt socket?");
						}
						break;
					case 7://Piece <len = 0009 + X><id = 7><index><begin><length>
						if(responseLen< 9){
							throw new MessagingException("Message not properly formatted?Corrupt socket?");
						}
						valid = Piece.read(inStream, responseLen, piece_hashes[index].array(), piece_length);
						//Ask for next file piece only if the current file piece is valid
						if (valid == 1){
							Have.write(outStream, index);
							index++;
						}
						if (index == numPieces ){
							completed = 1;
						}
						break;
					case 8://Cancel <len = 0013><id = 8><index><begin><length>
						if(responseLen!= 13){
							throw new MessagingException("Message not properly formatted?Corrupt socket?");
						}
						break;
					default:
						throw new MessagingException("Message not properly formatted?Corrupt socket?");
				}
			}
			else
				throw new MessagingException("Corrupt connection?");
			
			//Exit loop after complete download
			if (completed == 1){
				break;
			}
			
			//Request the next piece of the file if not peer_choked
			if (peer_choke == 0){
				
				if (index == (numPieces-1) && length != 0){
					Request.write(outStream, index, begin, length);
					System.out.println("Request Sent for index: " + index + "   Size: " + length);
				}
				else{
					Request.write(outStream, index, begin, piece_length);
					System.out.println("Request Sent for index: " + index + "   Size: " + piece_length);
				}
			}
			else
				;
		}
		
		outStream.close();
		inStream.close();
		socket.close();	
	}
}
