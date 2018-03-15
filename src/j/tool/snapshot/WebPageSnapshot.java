package j.tool.snapshot;

import j.app.webserver.JHandler;
import j.app.webserver.JSession;
import j.fs.JFile;
import j.log.Logger;
import j.nvwa.Nvwa;
import j.sys.SysUtil;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class WebPageSnapshot extends JHandler{
	private static Logger log=Logger.create(WebPageSnapshot.class);
	private static WebDriver driver =null;
	private static final Object lock=new Object();
	
	
	/**
	 * 
	 *
	 */
	public WebPageSnapshot(){
	}
	
	/**
	 * 
	 *
	 */
	public static void init(){
		synchronized(lock){
			if(driver!=null) return;
			
			System.setProperty(Nvwa.getParameter("/snapshot","DriverName"),Nvwa.getParameter("/snapshot","DriverPath"));
			if(driver==null){
				try{
					driver = new FirefoxDriver();
				}catch(Exception ex){
					log.log(ex,Logger.LEVEL_FATAL);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param jsession
	 * @param session
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void snapshot(JSession jsession,HttpSession session,HttpServletRequest request,HttpServletResponse response) throws Exception{
		init();
		synchronized(lock){
			try{
				long now=SysUtil.getNow();
				String savedPath=Nvwa.getParameter("/snapshot","SavedPath");
				savedPath=JFile.adjustFileSeperator(savedPath);
				
				String url=SysUtil.getHttpParameter(request,"url");
				if(!url.startsWith("http")) url="http://"+url;
				
				log.log("getting snapshot of "+url+"...",-1);
				
				driver.navigate().to(url);
		
				File snapshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				File newFile=new File(savedPath+now+".jpg");
				if(!newFile.getParentFile().exists()) newFile.getParentFile().mkdirs();
				snapshotFile.renameTo(newFile);
				
				jsession.resultString=now+"";
			}catch(Exception ex){
				log.log(ex,Logger.LEVEL_ERROR);
				
				jsession.resultString="";
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	public void finalize(){
		
	}
	
	/**
	 * 测试
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		long now=SysUtil.getNow();
		String savedPath="F:/";
		
		String url="http://www.baidu.com/";
		
		WebDriver driver = new FirefoxDriver();
		driver.navigate().to(url);

		File snapshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		File newFile=new File(savedPath+now+".jpg");
		if(!newFile.getParentFile().exists()) newFile.getParentFile().mkdirs();
		snapshotFile.renameTo(newFile);
		
		driver.quit();
	}
}
