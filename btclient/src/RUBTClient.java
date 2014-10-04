import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.file.*;
import java.security.NoSuchAlgorithmException;

import myTools.*;
import GivenTools.*;

/**
 * BitTorrent Client
 * @author Yang J. Ren
 * *note*
 * needs multithreading or processes download rate is low
 */
public class RUBTClient {
	
	/**
	 * Peer_id_prefix
	 */
	public final static String peer_id_prefix = "YJRBT-RAND-NUM-";
	
	/**
	 * Runs the different protocols and downloads the file
	 * @param args
	 * @throws MessagingException
	 * @throws NoSuchAlgorithmException
	 */
	public static void main(String[] args) throws MessagingException, NoSuchAlgorithmException {
		
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
			
				//Communicate with the tracker and retrieve the peer
				TorrentInfo tf = new TorrentInfo(tfArray);
				String tr = TrackerResponse.get(tf, peer_id);
				FindPeer fp = new FindPeer(tr.getBytes());
				
				String saveFile = args[1];
				FileStorage.initialize(saveFile,tf.file_length);
				
				//Initiate the download
				PeerWireProtocol.start(tf.info_hash.array(), peer_id, fp.ip, fp.port, fp.peer_id, 
				tf.piece_hashes, tf.piece_length, tf.file_length);
				
				FileStorage.closeFile();
				System.out.println("Download complete");
				System.out.print("Torrent_File: " + args[0] + "\n" + "File_Save_Name: " + args[1] + "\n");
		
				
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

