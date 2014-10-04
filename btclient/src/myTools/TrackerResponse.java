package myTools;

import java.io.*;
import java.net.*;

import GivenTools.TorrentInfo;

public class TrackerResponse {
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
	public final static String port = "6881";
	
	/**
	 * Sends the http GET request to tracker to obtain the peer_list
	 * @param tf
	 * @param peer_id
	 * @return
	 * @throws IOException
	 */
	public static String get(TorrentInfo tf, String peer_id) throws IOException{
        //Concatenate the get request
		String up = "0";
        String down = "0";
        String left = "287";
        String query = String.format(("info_hash=%s&peer_id=%s&port=%s"
        		+ "&uploaded=%s&downloaded=%s&left=%s"),
        		Util.byteArrayToURLString(tf.info_hash.array()),
        		URLEncoder.encode(peer_id,charset2),URLEncoder.encode(port,charset2),
        		URLEncoder.encode(up,charset2),URLEncoder.encode(down,charset2),
        		URLEncoder.encode(left,charset2));
        URL combined = new URL(tf.announce_url + "?"+query);
       
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
}
