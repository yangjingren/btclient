package myTools;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;


public class msgHandler {
	
	
	//keep alive
	public int keep_alive(Socket socket){
		ByteBuffer len =ByteBuffer.allocate(4);
		len.putInt(0);
		return 0;
	}
		
	//choke doesnt need to be sent
	public int choke(Socket socket){
		ByteBuffer len =ByteBuffer.allocate(4);
		ByteBuffer msg_id = ByteBuffer.allocate(1);
		len.putInt(1);
		msg_id.putInt(0);
		return 0;
	}
	//unchoke doesnt need to be sent
	public int unchoke(Socket socket){
		ByteBuffer len =ByteBuffer.allocate(4);
		ByteBuffer msg_id = ByteBuffer.allocate(1);
		len.putInt(1);
		msg_id.putInt(1);
		return 0;
	}
	//interested needs to be sent
	public static int interested(Socket socket) throws IOException{
		
		int i,j,k;
		
		i=0;
		
		
		DataOutputStream handOut = new DataOutputStream(socket.getOutputStream());
		handOut.writeInt(1);
		handOut.writeByte(2);
		
		DataInputStream in = new DataInputStream(socket.getInputStream());
		i=in.readByte();
		j= in.readByte();

		System.out.println("len: "+i+"id: "+j+"payload: ");
		while((i=in.readByte())!=1)
			;
		j= in.readByte();
		System.out.println("len: "+i+"id: "+j+"payload: ");
		
		//msgHandler.request(socket);
		
		
		/*
		byte[] file_part = new byte [32768];
		in.readFully(file_part);
		try {
			String info_hash = Util.SHAsum(file_part);
			Util.toHex(info_hash.getBytes());
			System.out.println("len: "+i+"id: "+j+"payload: "+Util.toHex(info_hash.getBytes()));
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		return 0;
	}
	//uninterested
	public int uninterested(Socket socket){
		ByteBuffer len =ByteBuffer.allocate(4);
		ByteBuffer msg_id = ByteBuffer.allocate(1);
		len.putInt(1);
		msg_id.putInt(3);
		return 0;
	}
	//have
	public void have(Socket socket){
		ByteBuffer len =ByteBuffer.allocate(4);
		ByteBuffer msg_id = ByteBuffer.allocate(1);
		len.putInt(5);
		msg_id.putInt(4);
	}
	
	//request
	public static void request(Socket socket){
		ByteBuffer len =ByteBuffer.allocate(4);
		len.putInt(0013);
		int id = 6;
		byte msg_id = (byte) id;
		Integer index =0;
		Integer begin =0;
		Integer length = 32768;
		try {
			DataOutputStream handOut = new DataOutputStream(socket.getOutputStream());
			handOut.writeInt(13);
			handOut.writeByte(6);
			handOut.writeInt(index);
			handOut.writeInt(begin);
			handOut.writeInt(length);
			
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//piece
		public void piece(Socket socket){
			ByteBuffer len =ByteBuffer.allocate(4);
			ByteBuffer msg_id = ByteBuffer.allocate(1);
			len.putInt(5);
			msg_id.putInt(0);
		}
}
