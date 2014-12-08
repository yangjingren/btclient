import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import myTools.*;
import GivenTools.*;

/**
 * BitTorrent Client
 * @author Yang J. Ren ****
 * *note*
 * ******* I don't have a partner      *****
 * ******* This project was done alone *****
 * 
 */
public class RUBTClient implements Runnable{
	
	/**
	 * Peer_id_prefix
	 */
	public final static String peer_id_prefix = "YJRBT-RAND-NUM-";
	
	protected byte[] info_hash;
	protected String client_id;
	protected String ip;
	protected Integer port;
	protected String peer_id;
	protected ByteBuffer[] piece_hashes;
	protected Integer piece_length;
	protected Integer file_length;
	private static Object mutex = new Object();
	/**
	 * initializes the local variables to push into the peerwireclient
	 * @param info_hash
	 * @param client_id
	 * @param ip
	 * @param port
	 * @param peer_id
	 * @param piece_hashes
	 * @param piece_length
	 * @param file_length
	 */
	public RUBTClient(byte[] info_hash, String client_id, String ip, Integer port, String peer_id, 
			ByteBuffer[] piece_hashes, Integer piece_length, Integer file_length){
		this.info_hash = info_hash;
		this.client_id = client_id;
		this.ip = ip;
		this.port = port;
		this.peer_id = peer_id;
		this.piece_hashes = piece_hashes;
		this.piece_length = piece_length;
		this.file_length = file_length;
	}
	
	/**
	 * Starts the downloader
	 */
	public void run(){
		try {
			
			PeerWireProtocol.start(this.info_hash, this.client_id, this.ip, this.port, this.peer_id, 
					this.piece_hashes, this.piece_length, this.file_length);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BencodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Runs the different protocols and downloads the file
	 * @param args
	 * @throws MessagingException
	 * @throws NoSuchAlgorithmException
	 */
	public static void main(String[] args) throws MessagingException, NoSuchAlgorithmException {
		Integer servPort = ((Integer)Util.randInt(20000, 30000));//Integer.parseInt(TrackerResponse.port);
		TrackerResponse.port = servPort.toString();
		System.out.println("Server will be opened on port: "+ servPort);
		//Check if the arguments are valid
		if ( args.length < 2 || args.length > 2){
			System.out.println("Usage: RUBTClient torrent_file file_save_name\n");
			return;
		}
		else {
			
			// Check if first argument is a valid host
			Path torrentFile = Paths.get(args[0]);
			byte[] tfArray;
			
			//Read torrent file into a byte array
			try {
				tfArray = Files.readAllBytes(torrentFile);
				//Generate client_id
				int peer_id_postfix = Util.randInt(10000, 99999);
				StringBuilder peer_id_buff = new StringBuilder();
				peer_id_buff.append(peer_id_prefix);
				peer_id_buff.append(peer_id_postfix);
				String peer_id = new String (peer_id_buff.toString());
				System.out.println(peer_id);
				//Communicate with the tracker and retrieve the peer
				TorrentInfo tf = new TorrentInfo(tfArray);

				String saveFile = args[1];
				FileStorage.initialize(saveFile,tf.file_length);
			
				int completed = FileStorage.validate(tf.piece_hashes, tf.file_length, tf.piece_length);
				String tr = TrackerResponse.get(tf, peer_id);
				FindPeer fp = new FindPeer(tr.getBytes());
				TrackerResponse.initialize();
				//Initiate the download
				int i = 0;
				if (completed == 0){
					System.out.println("Download not Complete.");
				while (i<fp.PeerIPList.size()){
					synchronized(mutex){
						
						RUBTClient downloader = new RUBTClient(tf.info_hash.array(), peer_id, fp.PeerIPList.get(i), fp.PeerPortList.get(i), fp.PeerIDList.get(i), 
								tf.piece_hashes, tf.piece_length, tf.file_length);
						(new Thread(downloader)).start();
						i++;
					}
				}
				
				//This portion is for downloading from the localhost for testing purposes
				//The ip should be the ip of the localhost port of the server
				/* 
				synchronized(mutex){
					RUBTClient downloader = new RUBTClient(tf.info_hash.array(), peer_id, "127.0.0.1", 21235, "-AZ5400-uEs8yTsF3Hti", 
							tf.piece_hashes, tf.piece_length, tf.file_length);
					(new Thread(downloader)).start();
				}*/
				
				}
				else{
				System.out.println("Download complete");
				System.out.print("Torrent_File: " + args[0] + "\n" + "File_Save_Name: " + args[1] + "\n");
				}
				
				//This starts the shutdown option part of the application
				Program.initialize();
				
				try {
					//Starts the local PeerServer
					PeerWireServer.initialize(tf.file_length, tf.piece_length, tf.info_hash.array(), peer_id, servPort);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BencodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		                                                                  
	}
	
	
}

