package myTools;

import java.io.*;
import java.net.*;

import GivenTools.TorrentInfo;

public class TrackerResponse {
	
	public final static String charset = "UTF-8";
	public final static String charset2 = "ASCII";
	public final static String port = "6881";
	
	
	
	public static String get(TorrentInfo tf, String peer_id) throws IOException{
        
		String up = "0";
        String down = "0";
        String left = "287";
        //String hast = new String (tf.info_hash.array());
        String query = String.format(("info_hash=%s&peer_id=%s&port=%s"
        		+ "&uploaded=%s&downloaded=%s&left=%s"),
        		Util.byteArrayToURLString(tf.info_hash.array()),
        		URLEncoder.encode(peer_id,charset2),URLEncoder.encode(port,charset2),
        		URLEncoder.encode(up,charset2),URLEncoder.encode(down,charset2),
        		URLEncoder.encode(left,charset2));
        //System.out.println(peer_id.toString());
        URL combined = new URL(tf.announce_url + "?"+query);
       
        //System.out.println(combined);
        URLConnection connection = combined.openConnection();
        connection.setRequestProperty("Accept-Charset", charset);
        InputStream response = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(response));
        String line;
        StringBuilder out = new StringBuilder();
        while(((line =reader.readLine()))!=null){
        	out.append(line);
        }
        //System.out.println(out.toString());
        reader.close();
		return out.toString();
	}
}
