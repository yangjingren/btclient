package myTools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;

public class Logger {
	private static long startTime = 0;
	private static Object mutex = new Object();
	public static Integer totalTime = 0;
	
	/**
	 * Gets the total uploaded amount for passing to the tracker
	 * as well as the total runtime
	 */
	public static void getSession(){
		try {
			FileInputStream upTime = new FileInputStream("log.txt");
			Scanner scanner = new Scanner(upTime);
	        FileStorage.up = Integer.parseInt(scanner.nextLine());
	        totalTime = Integer.parseInt(scanner.nextLine());
	        scanner.close();
		} catch (FileNotFoundException e) {
			Writer makenew;
			try {
				makenew = new FileWriter("log.txt");
				Integer number = 0;
				makenew.write(number.toString());
				makenew.write("\r\n");
				makenew.write(number.toString());
				makenew.write("\r\n");
				makenew.close();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Start timer
	 */
	public static void start(){
		synchronized(mutex){
		if (startTime == 0)
			startTime = System.nanoTime();
		}
	}
	/**
	 * get run time since start in seconds
	 * @return
	 */
	public static long getRunTime(){
		return ((System.nanoTime()-startTime)/1000000000);
	}
}
