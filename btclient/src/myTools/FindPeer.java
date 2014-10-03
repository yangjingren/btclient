package myTools;
import java.io.UnsupportedEncodingException;
import java.nio.*;
import java.util.*;

import GivenTools.Bencoder2;
import GivenTools.BencodingException;
import GivenTools.ToolKit;

/**
 * This is a data structure class that extracts the peer list from the bencoded response
 * from the tracker and stores it in an array list.
 * 
 * @author Yang J. Ren 
 * format credited to TorrentInfo.java 
 * @author Robert Moore II
 */

public class FindPeer {
	/**
	 * Key used to retrieve the ip from the tracker response
	 */
	public final static ByteBuffer KEY_IP =ByteBuffer.wrap(new byte[]
			{'i','p'});
	
	/**
	 * Key used to retrieve the peer id
	 */
	public final static ByteBuffer KEY_PEER_ID = ByteBuffer.wrap(new byte[]
			{'p','e','e','r',' ','i','d'});
	
	/**
	 * Key used to retrieve the port
	 */
	public final static ByteBuffer KEY_PORT = ByteBuffer.wrap(new byte[]
			{'p','o','r','t'});
	
	/**
	 * Key used to retrieve the peers dictionary
	 */
	public final static ByteBuffer KEY_PEERS = ByteBuffer.wrap(new byte[]
			{'p','e','e','r','s'});
	
	/**
	 * Key used to retrieve the interval
	 */
	public final static ByteBuffer KEY_INTERVAL = ByteBuffer.wrap(new byte[]
			{'i','n','t','e','r','v','a','l'});
	
	/**
	 * Tracker response
	 */
	public final byte[] tracker_response;
	
	/**
	 * The base dictionary of the tracker response
	 */
	public final Map<ByteBuffer,Object> response_map;
	
	/**
	 * The base dictionary of the tracker response
	 */
	public final ArrayList<Object> peers_list;
	
	public final String peer_id;
	public final Integer port;
	public final String ip;
	public final Integer interval;
	
	/**
	 * Pulls the peers from the list and returns the selected peer
	 * @param tracker_response
	 * @throws BencodingException
	 */
	@SuppressWarnings("unchecked")
	public FindPeer(byte[] tracker_response) throws BencodingException{
		//Peer to check for -AZ5400-ASLV73OxS5Qs
		String peerComparator = "-AZ5400";
		
		//Make sure the input is valid
		if(tracker_response ==null|| tracker_response.length == 0){
			throw new IllegalArgumentException("Tracker response must be non-null and have at least 1 byte.");
		}
		
		//Assign the byte array
		this.tracker_response = tracker_response;
		
		//Assign the response map
		this.response_map = (Map<ByteBuffer,Object>)Bencoder2.decode(tracker_response);
		
		Integer interval_buff = (Integer)this.response_map.get(FindPeer.KEY_INTERVAL);
		if(interval_buff == null)
			throw new BencodingException("Could not retrieve interval.\n");
		
		this.interval= interval_buff;
		
		//Parse the response for the peer_list
		this.peers_list = (ArrayList<Object>)this.response_map.get(FindPeer.KEY_PEERS);
		
		@SuppressWarnings("rawtypes")
		Iterator i = this.peers_list.iterator();
		Object o = null;
		String peerId = null;
		Integer peerPort = null;
		String peerIp = null;
		
		//Loops through the peer list and assigns a valid peer to the variables
		while(i.hasNext()&&(o=i.next())!=null){
			Map<ByteBuffer,Object> peer_list = (Map<ByteBuffer,Object>)o;
			ByteBuffer peer_id = (ByteBuffer)peer_list.get(FindPeer.KEY_PEER_ID);
			String pid;
			String ip1;
			try {
				pid = new String(peer_id.array(),"ASCII");
				if (pid.startsWith(peerComparator)){
					ByteBuffer ip = (ByteBuffer)peer_list.get(FindPeer.KEY_IP);
					Integer port = (Integer)peer_list.get(FindPeer.KEY_PORT);
					ip1 = new String(ip.array(),"ASCII");
					peerIp = ip1;
					peerPort = port;
					peerId = pid;
					break;
				}
			} catch (UnsupportedEncodingException e) {
				throw new BencodingException(e.getLocalizedMessage());
			}
			
		//Validate if any peers are found
		if (peerId == null)
			throw new BencodingException("Could not retrieve a valid peer.\n");
		}
		
		//Assign the peer information
		this.peer_id = peerId;
		this.port = peerPort;
		this.ip =peerIp;
	
	}
}
