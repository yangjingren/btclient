package myTools;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import GivenTools.BencodingException;
import PeerMessages.*;

public class PeerWireProtocol {
	/**
	 * Run once variable
	 */
	public static Integer downloadC = 0; 
	
	private static Object mutex = new Object();
	
	/**
	 * Queues to keep track of notices that have to be sent to clients
	 * and the queue that stores all of the sockets that have been open
	 */
	public static ArrayList<Socket> socketQueue = new ArrayList<Socket>();
	public static ArrayList<Integer> haveQueue = new ArrayList<Integer>();
	
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
	 * @throws BencodingException 
	 */
	public static void start(byte[] info_hash, String client_id, String ip, Integer port, String peer_id, 
			ByteBuffer[] piece_hashes, Integer piece_length, Integer file_length) throws IOException, MessagingException, NoSuchAlgorithmException, BencodingException{
		
		ArrayList<Boolean> PeerPieceList = new ArrayList<Boolean>();
		HashMap<Integer, Integer> downloadQueue = new HashMap<Integer,Integer>();
		//Initiate prefix_length, msg_id, file_parameters, peer/client_parameters
		int responseLen, responseID, valid;
		int index, begin, length, numPieces;
		index = begin = 0;
		length = file_length%piece_length;
		numPieces = file_length/piece_length;	
		Integer highestNumPieces = 0;
		if (length > 0){
			numPieces++;	
		}
		
		//Initiate client and peer parameters to defaults
		int peer_choke;
		peer_choke = 1;
		int l = 0;
		int addition = 0;
		while (l < numPieces){
			PeerPieceList.add(false);
			l++;
		}
		
		//Open the data streams and start the connection to peer
		Socket socket = new Socket(ip, port);
		System.out.println(ip+ "  " + port);
		socketQueue.add(socket);
		socket.setSoTimeout(120000);
		System.out.println("Open outputStream");
		DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
		System.out.println("Open inputStream");
		DataInputStream inStream = new DataInputStream(socket.getInputStream());
		System.out.println("Sending handshake");
		Handshake.write(info_hash, client_id, outStream);
		System.out.println("Sent handshake");
		Handshake.read(info_hash, inStream);
		System.out.println("Handshake success");
		
		System.out.println("Sending interested");
		Interested.write(outStream);
		System.out.println("Sent interested");
		
		System.out.println("Download Start");
		while (Program.isRunning()){
			
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
						//Send cancel messages
						Set set = downloadQueue.entrySet();
						Iterator i = set.iterator();
						while (i.hasNext()){
							Map.Entry me = (Map.Entry)i.next();
							if ((Integer)me.getKey() == (numPieces-1)&&length != 0)
								Cancel.write(outStream, ((Integer) me.getKey()).intValue(), 0, length);
							else if ((Integer)me.getKey() == (numPieces-1)&&length == 0)
								Cancel.write(outStream, ((Integer) me.getKey()).intValue(), 0, piece_length);
							FileStorage.downloading.set(((Integer) me.getKey()).intValue(), false);
							i.remove();
						}
						break;
					case 1://Unchoked <len = 0001><id = 1>
						System.out.println("Peer unchoked");
						peer_choke = 0;
						break;
					case 2:
						break;
					case 3:
						break;
					case 4://Have <len = 0005><id = 4><piece_index>
						if(responseLen!= 5){
							throw new MessagingException("Message not properly formatted?Corrupt socket?4");
						}
						addition = inStream.readInt();
						PeerPieceList.set(addition, true);
						break;
					case 5://Bitfield <len = 0001 + X><id = 5><bitfield>
						Bitfield.read(inStream, responseLen, numPieces, PeerPieceList);
						break;
					case 6://Request <len = 0013><id = 6><index><begin><length>
						if(responseLen!= 13){
							throw new MessagingException("Message not properly formatted?Corrupt socket?6");
						}
						break;
					case 7://Piece <len = 0009 + X><id = 7><index><begin><length>
						if(responseLen< 9){
							throw new MessagingException("Message not properly formatted?Corrupt socket?7");
						}
						index = inStream.readInt();
						valid = Piece.read(inStream, responseLen, piece_hashes[index].array(), piece_length, index);
						//Ask for next file piece only if the current file piece is valid
						if (valid == 1){
							Have.write(outStream, index);
							haveQueue.add(index);
							downloadQueue.remove(index);
							FileStorage.progress.set(index, true);
							FileStorage.downloading.set(index, false);
							FileStorage.missing.set(index, true);
							TrackerResponse.trackerCompleted();
						}
						break;
					case 8://Cancel <len = 0013><id = 8><index><begin><length>
						if(responseLen!= 13){
							throw new MessagingException("Message not properly formatted?Corrupt socket?8");
						}
						break;
					default:
						throw new MessagingException("Message not properly formatted?Corrupt socket?");
				}
			}
			else
				throw new MessagingException("Corrupt connection?");
			
			//Exit loop after complete download
			
			//Request the next piece of the file if not peer_choked
			if (peer_choke == 0){
				
				//Find the next piece to download
				synchronized(mutex){
					index = 0;
					//////////////////////////RFA
					/**
					 * Make list of Pieces Left to be Downloaded
					 * that this peer has
					 */
					Queue<Integer> list = new LinkedList<Integer>();
					while (index < numPieces){
						if (FileStorage.missing.get(index) == false&&PeerPieceList.get(index)==true&&
								FileStorage.downloading.get(index)==false){
							////////////////////////
							list.add(index);
						}
						index++;
					}
					
					
					/**
					 * Cross reference the list with the FileStorage
					 * make list of NumPieces
					 */
					highestNumPieces = 100;
					Queue<Integer> rand = new LinkedList<Integer>();
					while(list.size()!= 0){
						if (FileStorage.count.get(list.peek())<highestNumPieces){
							while(rand.size()!=0){
								rand.remove();
							}
							highestNumPieces = FileStorage.count.get(list.peek());
							rand.add(list.poll());
						}
						else if (FileStorage.count.get(list.peek())==highestNumPieces){
							rand.add(list.poll());
						}
					}
					/**
					 * Select highest key 
					 * Generate random number
					 * Select index = value
					 */
					if (rand.size()>0){
						int selector = Util.randInt(0, rand.size()-1);
						for (int k = 0; k< selector; k++){
							rand.remove();
						}
						index = rand.peek();
						downloadQueue.put(index, 1);
					}
					/*
					index = 0;
					while (index < numPieces){
						if (FileStorage.missing.get(index) == false&&PeerPieceList.get(index)==true&&
								FileStorage.downloading.get(index)==false){
							FileStorage.downloading.set(index, true);
							////////////////////////
							downloadQueue.put(index, 1);
							break;
						}
						index++;
					}
					*/
					/////////////////////////
					if (downloadC == 1){
						break;
					}
					else if (downloadC == 0){
						if (FileStorage.Update(piece_hashes, file_length, piece_length)==1){
							System.out.println("Time of download in seconds(This Session): " + Logger.getRunTime());
							downloadC = 1;
							//FileStorage.closeFile();
							break;
						}
					}
				}
				
				if (index == (numPieces-1) && length != 0){
					Request.write(outStream, index, begin, length);
					System.out.println("Request Sent for index: " + index + "   Size: " + length + "   Ip: "+ ip+ " Tid: " + Thread.currentThread().getId());
				}
				else{
					Request.write(outStream, index, begin, piece_length);
					System.out.println("Request Sent for index: " + index + "   Size: " + piece_length + "   Ip: "+ ip+ " Tid: " + Thread.currentThread().getId());
				}
				
			}
			else
				;
		}
		
		outStream.close();
		inStream.close();
		socket.close();	
		Set set = downloadQueue.entrySet();
		Iterator i = set.iterator();
		while (i.hasNext()){
			Map.Entry me = (Map.Entry)i.next();
			FileStorage.downloading.set(((Integer) me.getKey()).intValue(), false);
			i.remove();
		}
	}
}
