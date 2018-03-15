package j.fs.big;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DemoNio {
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		System.out.println("start");
		long start=System.currentTimeMillis();
		int bufSize = 102400;
		File fin = new File("F:\\temp\\test\\in.log");
		File fout = new File("F:\\temp\\test\\out.log");
		
		RandomAccessFile in=new RandomAccessFile(fin, "r");
		FileChannel fileChannelIn = in.getChannel();
		ByteBuffer rBuffer = ByteBuffer.allocate(bufSize);

		RandomAccessFile out=new RandomAccessFile(fout, "rws");
		FileChannel fileChannelOut = out.getChannel();
		ByteBuffer wBuffer = ByteBuffer.allocateDirect(bufSize);

		readThenWrite(bufSize, fileChannelIn, rBuffer, fileChannelOut, wBuffer);

		in.close();
		out.close();

		long end=System.currentTimeMillis();
		
		System.out.print("OK..."+(start-end));
	}

	/**
	 * 
	 * @param bufSize
	 * @param fcin
	 * @param rBuffer
	 * @param fcout
	 * @param wBuffer
	 */
	public static void readThenWrite(int bufSize, 
			FileChannel fcin,
			ByteBuffer rBuffer, 
			FileChannel fcout, 
			ByteBuffer wBuffer) {
		try {
			while (fcin.read(rBuffer) != -1) {	
				byte[] bs=new byte[rBuffer.position()];
				rBuffer.rewind();
				rBuffer.get(bs);
				rBuffer.clear();
				//fcout.write(ByteBuffer.wrap(bs));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}