package myTools;

import java.io.*;
import java.net.*;

import PeerMessages.*;

public class PeerWireServer implements Runnable{
		protected Socket csocket;
		protected int file_length;
		protected int piece_length;
		protected byte[] info_hash;
		protected String peer_id;
		public static ServerSocket SSocket;
		
		/**
		 * Initializes the important local variables for the server
		 * @param csocket
		 * @param file_length
		 * @param piece_length
		 * @param info_hash
		 * @param peer_id
		 */
	   PeerWireServer(Socket csocket, int file_length, int piece_length, byte[] info_hash, String peer_id) {
	      this.csocket = csocket;
	      this.file_length = file_length;
	      this.piece_length = piece_length;
	      this.info_hash = info_hash;
	      this.peer_id = peer_id;
	   }
	   
	   
	   /**
	    * Starts the server
	    * @param file_length
	    * @param piece_length
	    * @param info_hash
	    * @param peer_id
	    * @param servPort
	    * @throws Exception
	    */
	   public static void initialize(int file_length, int piece_length, byte[] info_hash, String peer_id, int servPort) 
	   throws Exception {
	      ServerSocket ssock = new ServerSocket(servPort);
	      SSocket = ssock;
	      System.out.println("Listening");
	      while (Program.isRunning()) {
	         Socket sock = ssock.accept();
	         System.out.println("Connected");
	         new Thread(new PeerWireServer(sock, file_length, piece_length, info_hash, peer_id)).start();
	      }
	   }
	   
	   /**
	    * The parser for each connecting client
	    */
	   public void run() {
	    	//Initiate prefix_length, msg_id, file_parameters, peer/client_parameters
	  		int responseLen, responseID;
	  		try {
	  		csocket.setSoTimeout(120000);
	         DataOutputStream outStream = new DataOutputStream(csocket.getOutputStream());
			
	         DataInputStream inStream = new DataInputStream(csocket.getInputStream());
	         Handshake.read(this.info_hash, inStream);
	         Handshake.write(this.info_hash, this.peer_id, outStream);
	         
	         Bitfield.write(outStream);
	         Integer initialPos = PeerWireProtocol.haveQueue.size();
	         // read if client is interested
	         //if interested unchoke
	         while (Program.isRunning()){
	 			
	 			responseLen = Response.readLen(inStream);
	 			if (responseLen == 0){
	 				//keep connection open
	 				//Start Connection
	 				//Close if 2 minutes pass
	 			}
	 			else if (responseLen >= 1){
	 				responseID = Response.readID(inStream);
	 				switch (responseID){
	 					case 0://Choke <len = 0001><id = 0>
	 						System.out.println("Peer choked");
						break;
	 					case 1://Unchoke <len = 0001><id = 1>
	 						System.out.println("Peer unchoked");
						break;
	 					case 2://Interested <len = 0001><id = 2>
	 						System.out.println("Peer is interested");
						System.out.println("Unchoking peer");
	 						Choke.unchoke(outStream);
	 						//Unchoke here
	 						break;
	 					case 3://Uninterested <len = 0001><id = 3>
						System.out.println("Peer not interested, Choking");
	 						Choke.choke(outStream);
	 						//Choke here
	 						break;
	 					case 4://Have <len = 0005><id = 4><piece_index>
	 						if(responseLen!= 5){
	 							throw new MessagingException("Message not properly formatted?Corrupt socket?");
	 						}
	 						Have.read(inStream);
	 						break;
	 					case 5://Bitfield <len = 0001 + X><id = 5><bitfield>
	 						/*Not useful in this scenario of uploading only
	 						Bitfield.read(inStream, responseLen, numPieces, PeerPieceList);
	 						*/
	 						break;
	 					case 6://Request <len = 0013><id = 6><index><begin><length>
	 						if(responseLen!= 13){
	 							throw new MessagingException("Message not properly formatted?Corrupt socket?");
	 						}
	 						else{
	 							Request.read(inStream, this.piece_length, outStream);
	 						}
	 						Thread.sleep(500);
	 						break;
	 					case 7://Piece <len = 0009 + X><id = 7><index><begin><length>
	 						/*Will not be requesting pieces from the client
	 						 * if(responseLen< 9){
	 							throw new MessagingException("Message not properly formatted?Corrupt socket?");
	 						}
	 						valid = Piece.read(inStream, responseLen, piece_hashes[index].array(), piece_length);
	 						//Ask for next file piece only if the current file piece is valid
	 						if (valid == 1){
	 							Have.write(outStream, index);
	 							FileStorage.progress.set(index, true);
	 							FileStorage.downloading.set(index, false);
	 						}*/
	 						break;
	 					case 8://Cancel <len = 0013><id = 8><index><begin><length>
	 						if(responseLen!= 13){
	 							throw new MessagingException("Cancel message not properly formatted?Corrupt socket?");
	 						}
	 						break;
	 					default:
	 						throw new MessagingException("Message not properly formatted?Corrupt socket?");
	 				}
	 			}
	 			else
	 				throw new MessagingException("Corrupt connection?");
	 			
	 			if (initialPos < PeerWireProtocol.haveQueue.size()){
	 				int tempPos = PeerWireProtocol.haveQueue.size();
	 				while(initialPos < tempPos){
	 					Have.write(outStream, PeerWireProtocol.haveQueue.get(initialPos));
	 					initialPos++;
	 				}
	 				
	 			}
	 		}
	         
	         inStream.close();
	         outStream.close();
	         csocket.close();
	  		} catch (IOException | MessagingException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
	   }
	
