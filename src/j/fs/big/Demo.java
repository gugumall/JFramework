package j.fs.big;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Demo {
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		System.out.println("start");
		long start=System.currentTimeMillis();
		int bufSize = 1024*1024;
		File fin = new File("F:\\temp\\test\\in.log");
		File fout = new File("F:\\temp\\test\\out.log");
		
		BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fin));    
		BufferedReader reader = new BufferedReader(new InputStreamReader(fis,"utf-8"),bufSize);
		
		BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(fout));    
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos,"utf-8"),bufSize);
		
		readThenWrite(reader,writer);
		
		fis.close();
		fos.close();
		long end=System.currentTimeMillis();
		
		System.out.print("OK..."+(start-end));
	}

	/**
	 * 
	 * @param reader
	 * @param writer
	 */
	public static void readThenWrite(BufferedReader reader,
			BufferedWriter writer) {
		try {			  
			//String line = "";
			while(reader.readLine()!= null){
				//writer.write(line+"\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}