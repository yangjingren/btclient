import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.file.*;

import myTools.*;
import GivenTools.*;

public class RUBTClient {
	
	public final static String peer_id_prefix = "YJRBT-RAND-NUM-";
	
	
	public static void main(String[] args) throws MessagingException {
		
		//Check if the arguments are valid
		if ( args.length < 2 || args.length > 2){
			System.out.println("Usage: RUBTClient torrent_file file_save_name\n");
			return;
		}
		else {
			
			// Check if first argument is a valid host
			Path torrentFile = Paths.get(args[0]);
			byte[] tfArray;
			try {
				//Read torrent file into a byte array
				tfArray = Files.readAllBytes(torrentFile);
				try {
					int peer_id_postfix = Util.randInt(10000, 99999);
					StringBuilder peer_id_buff = new StringBuilder();
			        peer_id_buff.append(peer_id_prefix);
			        peer_id_buff.append(peer_id_postfix);
			        String peer_id = new String (peer_id_buff.toString());
			        
					TorrentInfo tf = new TorrentInfo(tfArray);
			        String tr = TrackerResponse.get(tf, peer_id);
			        System.out.print(tr);
					FindPeer fp = new FindPeer(tr.getBytes());
					//System.out.println(fp.interval + "\n"+fp.peer_id+"\n"+fp.port+"\n"+fp.ip+"\n");
					//findPeer.peer((HashMap)decod);
			       
					Socket socket = Handshake.write(tf.info_hash.array(),peer_id, fp.ip, fp.port);
					socket = Handshake.read(tf.info_hash.array(), fp.peer_id, socket);
			       
					String has = new String(Util.toHex(tf.piece_hashes[0].array()));
					//System.out.println(has);
			        
				} catch (BencodingException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// Check if second argument is a valid port number
			Path saveFile = Paths.get(args[0]);
			
			
		System.out.print("Torrent_File: " + args[0] + "\n" + "File_Save_Name: " + args[1] + "\n");
		
		}
		
		//End argument check
	}
}

