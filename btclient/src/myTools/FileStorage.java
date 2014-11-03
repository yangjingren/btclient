package myTools;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class FileStorage {
	/**
	 * Static file pointer to store the file in
	 */
	private static RandomAccessFile file;
	
	private static Object mutex = new Object();
	
	public static ArrayList<Boolean> progress;
	
	public static ArrayList<Boolean> downloading;
	
	public static ArrayList<Boolean> missing;
	
	public static Integer down = 0;

	public static Integer up = 0;
	/**
	 * Initialize the file pointer with write permissions and set length
	 * @param filename
	 * @param file_length
	 * @throws IOException
	 */
	public static void initialize(String filename,long file_length) throws IOException{
		FileStorage.progress = new ArrayList<Boolean>();
		FileStorage.downloading = new ArrayList<Boolean>();
		FileStorage.missing = new ArrayList<Boolean>();
		FileStorage.file = new RandomAccessFile(filename, "rw");
		FileStorage.file.setLength(file_length);
	}
	
	/**
	 * Write bytes to file
	 * @param index
	 * @param block
	 * @param piece_length
	 * @throws IOException
	 */
	public static void store(int index, byte[] block, int piece_length) throws IOException{
		synchronized(mutex){
			long offset = index*piece_length;
			FileStorage.file.seek(offset);
			FileStorage.file.write(block);
		}
	}
	
	/**
	 * Write bytes to file
	 * @param index
	 * @param block
	 * @param piece_length
	 * @throws IOException
	 */
	public static byte[] read(int index, int begin, int piece_length, int length) throws IOException{
		synchronized(mutex){
			long offset = index*piece_length;
			byte [] piece = new byte[length];
			FileStorage.file.seek(offset);
			FileStorage.file.read(piece, begin, length);
			return piece;
		}
	}
	
	
	/**
	 * Close file pointer
	 * @throws IOException
	 */
	public static void closeFile() throws IOException{
		FileStorage.file.close();
	}
	
	/**
	 * Check the file for which pieces are missing and store those values into a boolean array
	 * @param piece_hashes
	 * @param file_length
	 * @param piece_length
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static int validate(ByteBuffer[] piece_hashes ,long file_length, int piece_length) throws IOException, NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		long offset = 0;
		int index = 0;
		byte [] buffer = new byte[piece_length];
		long len = (long)piece_length;
		int completed = 0;
		//Verify Progress of the download
		System.out.println("Verifying Completed Pieces");
		while (offset < file_length){
			if ((offset+piece_length)>file_length){
				len = file_length - offset;
				byte [] endbuffer = new byte[(int)len];
				FileStorage.file.seek(offset);
				FileStorage.file.readFully(endbuffer, 0, (int)len);
				if(validate.validator(endbuffer, piece_hashes[index].array()) == 1){
					FileStorage.progress.add(true);
					completed++;
				}
				else
					FileStorage.progress.add(false);
			}
			else{
				FileStorage.file.seek(offset);
			FileStorage.file.read(buffer, 0, (int)len);
			if(validate.validator(buffer, piece_hashes[index].array()) == 1){
				FileStorage.progress.add(true);
				completed++;
			}
			else
				FileStorage.progress.add(false);
			}
			
			
			offset = offset + piece_length;
			index ++;
		}
		//Return the status of the download as well as save the state of the download to a local array
		FileStorage.down = completed;
		if (index==completed){
			return 1;
		}
		else{
			int i = 0;
			for (i = 0; i< index; i++)
				FileStorage.downloading.add(false);
			for (i = 0; i< index; i++){
				FileStorage.missing.add(FileStorage.downloading.get(i)|FileStorage.progress.get(i));
			}
			return 0;
		}
	}
	
	/**
	 * Verifies the status of the current file
	 * and updates the table accordingly
	 * @param piece_hashes
	 * @param file_length
	 * @param piece_length
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static Integer Update(ByteBuffer[] piece_hashes ,long file_length, int piece_length) throws IOException, NoSuchAlgorithmException{
				synchronized(mutex){
				long offset = 0;
				int index = 0;
				byte [] buffer = new byte[piece_length];
				long len = (long)piece_length;
				int completed = 0;
				//Verify Progress of the download
				while (offset < file_length){
					if ((offset+piece_length)>file_length){
						len = file_length - offset;
						byte [] endbuffer = new byte[(int)len];
						FileStorage.file.seek(offset);
						FileStorage.file.readFully(endbuffer, 0, (int)len);
						if(validate.validator(endbuffer, piece_hashes[index].array()) == 1){
							FileStorage.progress.set(index, true);
							completed++;
						}
						else
							FileStorage.progress.set(index, false);
					}
					else{
						FileStorage.file.seek(offset);
					FileStorage.file.read(buffer, 0, (int)len);
					if(validate.validator(buffer, piece_hashes[index].array()) == 1){
						FileStorage.progress.set(index, true);
						completed++;
					}
					else
						FileStorage.progress.set(index, false);
					}
					
					
					offset = offset + piece_length;
					index ++;
				}
				//Return the status of the download as well as save the state of the download to a local array
				FileStorage.down = completed;
				if (index==completed){
					return 1;
				}
				else{
					return 0;
				}
		}
	}
	
}
