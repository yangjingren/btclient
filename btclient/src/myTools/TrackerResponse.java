package myTools;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Map;

import GivenTools.*;

public class TrackerResponse implements Runnable{
	/**
	 * UTF-8 charset
	 */
	public final static String charset = "UTF-8";
	
	/**
	 * ASCII charset
	 */
	public final static String charset2 = "ASCII";
	
	/**
	 * Default port to listen on range 6881-6889
	 */
	public final static String port = "20000";
	
	/**
	 * Tracker events
	 */
	public final static String start = "started";
	public final static String complete = "completed";
	public final static String stop = "stopped";
	/**
	 * Info needed to Parse Tracker URLs
	 */
	protected static Integer numPieces;
	public static Integer interval = 0;
	protected static TorrentInfo tf;
	protected static String peer_id;
	protected long currentTime;
	
	/**
	 * Key used to retrieve the interval
	 */
	public final static ByteBuffer KEY_INTERVAL = ByteBuffer.wrap(new byte[]
			{'i','n','t','e','r','v','a','l'});
	
	/**
	 * Starts the new Tracker Thread
	 */
	public static void initialize(){
		new Thread (new TrackerResponse()).start();
	}
	
	/**
	 * Updates the interval based on the tracker response
	 * @param tr
	 * @throws BencodingException
	 */
	@SuppressWarnings("unchecked")
	public static void updateInterval(String tr) throws BencodingException{
		byte[] tracker_response = tr.getBytes();
		//Make sure the input is valid
		if(tracker_response == null|| tracker_response.length == 0){
			throw new IllegalArgumentException("Tracker response must be non-null and have at least 1 byte.");
		}
		
		
		//Assign the response map
		Map<ByteBuffer,Object>response_map = (Map<ByteBuffer,Object>)Bencoder2.decode(tracker_response);
		
		Integer interval_buff = (Integer)response_map.get(FindPeer.KEY_INTERVAL);
		if(interval_buff == null)
			throw new BencodingException("Could not retrieve interval.\n");
		if (interval_buff > 180)
			interval_buff = 180;
		TrackerResponse.interval = interval_buff;
		
	}
	
	/**
	 * Sends the http GET request to tracker to obtain the peer_list
	 * @param tf
	 * @param peer_id
	 * @return
	 * @throws IOException
	 */
	public static String get(TorrentInfo tf, String peer_id) throws IOException{
        //Concatenate the get request

		TrackerResponse.numPieces = tf.piece_hashes.length;
		TrackerResponse.peer_id = peer_id;
		TrackerResponse.tf = tf;
		
        URL combined = formreply(0);
       
        //Open the connection and send the request
        URLConnection connection = combined.openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        
        //Read the tracker reply convert to string and return
        InputStream response = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response));
        String line;
        StringBuilder out = new StringBuilder();
        while(((line =reader.readLine()))!=null){
        	out.append(line);
        }
        reader.close();
		return out.toString();
	}
	
	/**
	 * Creates the replys to send to the tracker
	 * @param event
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 */
	private static URL formreply(int event) throws UnsupportedEncodingException, MalformedURLException{
		Integer remaining = (TrackerResponse.numPieces-FileStorage.down);
		String up = FileStorage.up.toString();
        String down = FileStorage.down.toString();
        String left = remaining.toString();
        URL combined;
        String query;
        switch (event){
        case 0: 
        	query = String.format(("info_hash=%s&peer_id=%s&port=%s"
        		+ "&uploaded=%s&downloaded=%s&left=%s&event=%s"),
        		Util.byteArrayToURLString(tf.info_hash.array()),
        		URLEncoder.encode(peer_id,charset2),URLEncoder.encode(port,charset2),
        		URLEncoder.encode(up,charset2),URLEncoder.encode(down,charset2),
        		URLEncoder.encode(left,charset2), URLEncoder.encode(start, charset2));
        	combined = new URL(tf.announce_url + "?"+query);
        	break;
        case 1:
        	query = String.format(("info_hash=%s&peer_id=%s&port=%s"
            		+ "&uploaded=%s&downloaded=%s&left=%s&event=%s"),
            		Util.byteArrayToURLString(tf.info_hash.array()),
            		URLEncoder.encode(peer_id,charset2),URLEncoder.encode(port,charset2),
            		URLEncoder.encode(up,charset2),URLEncoder.encode(down,charset2),
            		URLEncoder.encode(left,charset2), URLEncoder.encode(complete, charset2));
            	combined = new URL(tf.announce_url + "?"+query);
            	break;
        case 2:
        	query = String.format(("info_hash=%s&peer_id=%s&port=%s"
            		+ "&uploaded=%s&downloaded=%s&left=%s&event=%s"),
            		Util.byteArrayToURLString(tf.info_hash.array()),
            		URLEncoder.encode(peer_id,charset2),URLEncoder.encode(port,charset2),
            		URLEncoder.encode(up,charset2),URLEncoder.encode(down,charset2),
            		URLEncoder.encode(left,charset2), URLEncoder.encode(stop, charset2));
            	combined = new URL(tf.announce_url + "?"+query);
            	break;
        default:
        	query = String.format(("info_hash=%s&peer_id=%s&port=%s"
            		+ "&uploaded=%s&downloaded=%s&left=%s"),
            		Util.byteArrayToURLString(tf.info_hash.array()),
            		URLEncoder.encode(peer_id,charset2),URLEncoder.encode(port,charset2),
            		URLEncoder.encode(up,charset2),URLEncoder.encode(down,charset2),
            		URLEncoder.encode(left,charset2));
            	combined = new URL(tf.announce_url + "?"+query);
            	break;
        }
        
        return combined;
	}
	
	/**
	 * Creates a new thread that runs according to the currentTime
	 * and sends periodic updates to the tracker
	 */
	public void run(){
		System.out.println("Starting Session");
		Logger.start();
		Logger.getSession();
		this.currentTime = Logger.getRunTime();
		while(Program.isRunning()){
			
			if ((Logger.getRunTime()-this.currentTime)>=TrackerResponse.interval){
				this.currentTime = Logger.getRunTime();
				try {
					trackerUpdate();
				} catch (IOException | BencodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			trackerStop();
		} catch (IOException | BencodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Updates the tracker of our presence occassionally dependant on the intervals
	 * @throws IOException
	 * @throws BencodingException
	 */
	public static void trackerUpdate() throws IOException, BencodingException{
		
        URL combined = formreply(4);
        //Open the connection and send the request
        URLConnection connection = combined.openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        
        //Read the tracker reply convert to string and return
        InputStream response = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response));
        String line;
        StringBuilder out = new StringBuilder();
        while(((line =reader.readLine()))!=null){
        	out.append(line);
        }
        if (out.length()!=0)
        	updateInterval(out.toString());
        reader.close();
        
	}
	
	/**
	 * Signals to the tracker that the download is complete
	 * @throws IOException
	 * @throws BencodingException
	 */
	public static void trackerCompleted() throws IOException, BencodingException{
		 URL combined = formreply(2);
	       
	        
	        //Open the connection and send the request
	        URLConnection connection = combined.openConnection();
	        connection.setRequestProperty("Accept-Charset", charset);
	        
	        //Read the tracker reply convert to string and return
	        InputStream response = connection.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(response));
	        String line;
	        StringBuilder out = new StringBuilder();
	        while(((line =reader.readLine()))!=null){
	        	out.append(line);
	        }
	        if (out.length()!=0)
	        	updateInterval(out.toString());
	        reader.close();
	}
	
	/**
	 * Tells the tracker that the application is shutting down
	 * @throws IOException
	 * @throws BencodingException
	 */
	public static void trackerStop() throws IOException, BencodingException{
		 URL combined = formreply(3);
	       
	        
	        //Open the connection and send the request
	        URLConnection connection = combined.openConnection();
	        connection.setRequestProperty("Accept-Charset", charset);
	        
	        //Read the tracker reply convert to string and return
	        InputStream response = connection.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(response));
	        String line;
	        StringBuilder out = new StringBuilder();
	        while(((line =reader.readLine()))!=null){
	        	out.append(line);
	        }
	        reader.close();
	}
	
	
}
