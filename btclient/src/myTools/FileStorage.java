package myTools;

import java.io.*;
import java.nio.file.Path;

public class FileStorage {
	/**
	 * Static file pointer to store the file in
	 */
	private static RandomAccessFile file;
	
	/**
	 * Initialize the file pointer with write permissions and set length
	 * @param filename
	 * @param file_length
	 * @throws IOException
	 */
	public static void initialize(String filename,long file_length) throws IOException{
		FileStorage.file= new RandomAccessFile(filename, "rw");
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
		long offset = index*piece_length;
		FileStorage.file.seek(offset);
		FileStorage.file.write(block);
	}
	
	/**
	 * Close file pointer
	 * @throws IOException
	 */
	public static void closeFile() throws IOException{
		FileStorage.file.close();
	}

}
