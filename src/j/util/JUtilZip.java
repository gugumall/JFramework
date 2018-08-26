package j.util;

import j.common.Global;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.commons.io.IOUtils;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.rarfile.FileHeader;


/**
 * 
 * @author 肖炯
 * 
 */
public class JUtilZip {
	/**
	 * 
	 * 
	 */
	public JUtilZip() {
	}

	/**
	 * 将zip文件zipFilePath解压到目录outPath中
	 * @param zipFilePath
	 * @param outPath
	 * @param fileNameCharSet
	 * @param password
	 * @throws Exception
	 */
	public static void extractZipFile(String zipFilePath, String outPath,String fileNameCharSet,String password)throws Exception{
		ZipFile zipFile = new ZipFile(zipFilePath);
		if(fileNameCharSet!=null&&!"".equals(fileNameCharSet)) zipFile.setFileNameCharset(fileNameCharSet);
		if(password!=null&&!"".equals(password)) zipFile.setPassword(password);
		zipFile.extractAll(outPath);
	}

	/**
	 * 将dirPath及其子目录和文件压缩为destFile
	 * @param dirPath
	 * @param destFile
	 * @param fileNameCharSet
	 * @param password
	 * @throws Exception
	 */
	public static void createZipFile(String dirPath, String destFile,String fileNameCharSet,int compressMethod,int compressLevel) throws Exception {
		ZipFile zipFile = new ZipFile(destFile);
		if(fileNameCharSet!=null&&!"".equals(fileNameCharSet)) zipFile.setFileNameCharset(fileNameCharSet);
					
		ZipParameters parameters = new ZipParameters();
		
		//设置压缩方法
		if(compressMethod!=Zip4jConstants.COMP_STORE
				&&compressMethod!=Zip4jConstants.COMP_DEFLATE
				&&compressMethod!=Zip4jConstants.COMP_AES_ENC){
			compressMethod=Zip4jConstants.COMP_DEFLATE;
		}
		parameters.setCompressionMethod(compressMethod);
		
		//设置压缩率
		if(compressLevel!=Zip4jConstants.DEFLATE_LEVEL_FAST
				&&compressLevel!=Zip4jConstants.DEFLATE_LEVEL_FASTEST
				&&compressLevel!=Zip4jConstants.DEFLATE_LEVEL_MAXIMUM
				&&compressLevel!=Zip4jConstants.DEFLATE_LEVEL_NORMAL
				&&compressLevel!=Zip4jConstants.DEFLATE_LEVEL_ULTRA){
			compressLevel=Zip4jConstants.DEFLATE_LEVEL_NORMAL;
		}		
		parameters.setCompressionLevel(compressLevel);

		//read hidden files
		parameters.setReadHiddenFiles(true);
		
		zipFile.createZipFileFromFolder(dirPath,parameters,false,0);
	}

	/**
	 * 将dirPath及其子目录和文件压缩为destFile并设置密码（使用标准加密）
	 * @param dirPath
	 * @param destFile
	 * @param fileNameCharSet
	 * @param password
	 * @throws Exception
	 */
	public static void createZipFileEncryptStandard(String dirPath, String destFile,String fileNameCharSet,int compressMethod,int compressLevel,String password) throws Exception {
		if(password==null||"".equals(password)) throw new Exception("password is empty");
		
		ZipFile zipFile = new ZipFile(destFile);
		if(fileNameCharSet!=null&&!"".equals(fileNameCharSet)) zipFile.setFileNameCharset(fileNameCharSet);
					
		ZipParameters parameters = new ZipParameters();
		
		//设置压缩方法
		if(compressMethod!=Zip4jConstants.COMP_STORE
				&&compressMethod!=Zip4jConstants.COMP_DEFLATE
				&&compressMethod!=Zip4jConstants.COMP_AES_ENC){
			compressMethod=Zip4jConstants.COMP_DEFLATE;
		}
		parameters.setCompressionMethod(compressMethod);
		
		//设置压缩率
		if(compressLevel!=Zip4jConstants.DEFLATE_LEVEL_FAST
				&&compressLevel!=Zip4jConstants.DEFLATE_LEVEL_FASTEST
				&&compressLevel!=Zip4jConstants.DEFLATE_LEVEL_MAXIMUM
				&&compressLevel!=Zip4jConstants.DEFLATE_LEVEL_NORMAL
				&&compressLevel!=Zip4jConstants.DEFLATE_LEVEL_ULTRA){
			compressLevel=Zip4jConstants.DEFLATE_LEVEL_NORMAL;
		}		
		parameters.setCompressionLevel(compressLevel);
		
		parameters.setEncryptFiles(true);
		parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
		parameters.setPassword(password);
		
		//read hidden files
		parameters.setReadHiddenFiles(true);
		
		zipFile.createZipFileFromFolder(dirPath,parameters,false,0);
	}

	/**
	 * 将dirPath及其子目录和文件压缩为destFile并设置密码（使用AES加密）
	 * @param dirPath
	 * @param destFile
	 * @param fileNameCharSet
	 * @param compressMethod
	 * @param compressLevel
	 * @param password
	 * @param aesStrengthLength
	 * @throws Exception
	 */
	public static void createZipFileEncryptAES(String dirPath, String destFile,String fileNameCharSet,int compressMethod,int compressLevel,String password,int aesStrengthLength) throws Exception {
		if(password==null||"".equals(password)) throw new Exception("password is empty");
		
		ZipFile zipFile = new ZipFile(destFile);
		if(fileNameCharSet!=null&&!"".equals(fileNameCharSet)) zipFile.setFileNameCharset(fileNameCharSet);
					
		ZipParameters parameters = new ZipParameters();
		
		//设置压缩方法
		if(compressMethod!=Zip4jConstants.COMP_STORE
				&&compressMethod!=Zip4jConstants.COMP_DEFLATE
				&&compressMethod!=Zip4jConstants.COMP_AES_ENC){
			compressMethod=Zip4jConstants.COMP_DEFLATE;
		}
		parameters.setCompressionMethod(compressMethod);
		
		//设置压缩率
		if(compressLevel!=Zip4jConstants.DEFLATE_LEVEL_FAST
				&&compressLevel!=Zip4jConstants.DEFLATE_LEVEL_FASTEST
				&&compressLevel!=Zip4jConstants.DEFLATE_LEVEL_MAXIMUM
				&&compressLevel!=Zip4jConstants.DEFLATE_LEVEL_NORMAL
				&&compressLevel!=Zip4jConstants.DEFLATE_LEVEL_ULTRA){
			compressLevel=Zip4jConstants.DEFLATE_LEVEL_NORMAL;
		}		
		parameters.setCompressionLevel(compressLevel);
		
		parameters.setEncryptFiles(true);
		parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);

		if(aesStrengthLength!=Zip4jConstants.AES_STRENGTH_128
				&&compressLevel!=Zip4jConstants.AES_STRENGTH_192
				&&compressLevel!=Zip4jConstants.AES_STRENGTH_256){
			aesStrengthLength=Zip4jConstants.AES_STRENGTH_256;
		}	
		parameters.setAesKeyStrength(aesStrengthLength);
		
		parameters.setPassword(password);
		
		//read hidden files
		parameters.setReadHiddenFiles(true);
		
		zipFile.createZipFileFromFolder(dirPath,parameters,false,0);
	}
	
	
	/**
	 * 解压指定RAR文件到指定的路径
	 * 
	 * @param srcRarFile  需要解压RAR文件
	 * @param destPath 指定解压路径
	 * @param password 压缩文件时设定的密码
	 * @throws IOException
	 */
	public static void unrar(File srcRarFile,String destPath,String password) throws IOException{
		if(null==srcRarFile||!srcRarFile.exists()){
			throw new IOException("source file is not exists.");
		}
		
		if(!destPath.endsWith(Global.filePathSeparator)){
			destPath+=Global.filePathSeparator;
		}
		
		Archive archive=null;
		OutputStream unOut=null;
		try{
			archive=new Archive(srcRarFile,password,false);
			FileHeader fileHeader=archive.nextFileHeader();
			while(null!=fileHeader){
				if(!fileHeader.isDirectory()){
					//根据不同的操作系统拿到相应的 destDirName 和 destFileName
					String destFileName="";
					String destDirName="";
					if(Global.filePathSeparator.equals("/")){ // 非windows系统
						destFileName=(destPath+fileHeader.getFileNameW()).replaceAll("\\\\","/");
						destDirName=destFileName.substring(0,destFileName.lastIndexOf("/"));
					}else{ // windows系统
						destFileName=(destPath+fileHeader.getFileNameW()).replaceAll("/","\\\\");
						destDirName=destFileName.substring(0,destFileName.lastIndexOf("\\"));
					}
					
					//创建文件夹
					File dir=new File(destDirName);
					if(!dir.exists()||!dir.isDirectory()){
						dir.mkdirs();
					}
					
					//抽取压缩文件
					unOut=new FileOutputStream(new File(destFileName));
					archive.extractFile(fileHeader,unOut);
					unOut.flush();
					unOut.close();
				}
				fileHeader=archive.nextFileHeader();
			}
			archive.close();
		}catch(RarException e){
			e.printStackTrace();
		}finally{
			IOUtils.closeQuietly(unOut);
		}
	}
	
	
	/**
	 * 解压gzip流为byte[]
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static byte[] readGZipStream2Bytes(InputStream in) throws Exception {
		GZIPInputStream gzin = new GZIPInputStream(in);
		return JUtilInputStream.bytes(gzin);
	}

	/**
	 * 解压gzip流为String
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static String readGZipStream2String(InputStream in) throws Exception {
		return new String(readGZipStream2Bytes(in));
	}

	/**
	 * 解压gzip流为指定编码的String
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static String readGZipStream2String(InputStream in, String encoding)
			throws Exception {
		return new String(readGZipStream2Bytes(in), encoding);
	}

	/**
	 * 解压gzip流并写入文件
	 * 
	 * @param in
	 * @param file
	 * @throws Exception
	 */
	public static void gzipIs2File(InputStream in, String file)
			throws Exception {
		FileOutputStream fo = new FileOutputStream(file);
		GZIPOutputStream os = new GZIPOutputStream(fo);
		byte[] buffer = new byte[1024];
		int readed = in.read(buffer);
		while (readed > -1) {
			os.write(buffer, 0, readed);
			readed = in.read(buffer);
		}
		os.flush();
		os.close();
		fo.close();
		in.close();
	}

	/**
	 * 压缩字符串
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String gzipString(String str,String encoding) throws Exception {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		if(encoding==null) gzip.write(str.getBytes());
		else gzip.write(str.getBytes(encoding));
		gzip.close();
		return out.toString("ISO-8859-1");
	}
	public static String gzipBytes(byte[] bytes) throws Exception {
		if (bytes==null) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(bytes);
		gzip.close();
		return out.toString("ISO-8859-1");
	}

	/**
	 * 解压字符串
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String readGzipString(String str,String encoding) throws Exception {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		if(encoding==null) return out.toString();
		else  return out.toString(encoding);
	}

	/**
	 * test
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("====");
		JUtilZip.unrar(new File("F:\\temp\\cars\\shop1.rar"),"F:\\temp\\carsx\\","111111");
		//JUtilZip.createZipFileEncryptAES("F:\\temp\\cars\\shop1","F:\\temp\\cars\\shop5.zip","GBK",-1,-1,"我们",Zip4jConstants.AES_STRENGTH_256);
		System.out.println("==== end");
	}
}
