package j.tool.selenium;

import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * 
 * @author ceo
 * 
 */
public class Sample{
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception{
		String username="......";// 你的手机号
		String password="......";// 你的密码
		

		//System.setProperty("webdriver.firefox.bin","C:/Program Files/Mozilla Firefox/firefox.exe");
		System.setProperty("webdriver.chrome.driver","F:/work/JFramework_v2.0/doc/selenium/chromedriver.exe");
		
		//System.setProperty("webdriver.gecko.driver","F:/work/JFramework_v2.0/doc/selenium/geckodriver.exe");
		//FirefoxOptions options=new FirefoxOptions();
		//FirefoxProfile profile=new FirefoxProfile(new File("C:/Users/ceo/AppData/Roaming/Mozilla/Firefox/Profiles/1nnskemt.default"));
		//options.setProfile(profile);
		
		ChromeDriver driver=new ChromeDriver();
		driver.get("https://www.taobao.com/");

		Thread.sleep(3000);
		
		System.out.println("find login link and click");
		WebElement element=driver.findElement(By.id("J_SiteNavLogin"));
		element=element.findElement(By.className("site-nav-menu-hd"));
		element=element.findElement(By.tagName("a"));
		element.click();
		
		Thread.sleep(3000);
		
		element=driver.findElement(By.id("J_Quick2Static"));
		element.click();
		
		Thread.sleep(1000);
		
		element=driver.findElement(By.id("TPL_username_1"));
		//element.click();
		
		Thread.sleep(3000);

		Random rand=new Random();
		for(int i=0;i<username.length();i++){
			Thread.sleep(rand.nextInt(1000));
			element.sendKeys(""+username.charAt(i));
		}
		
		Thread.sleep(3000);
		
		element=driver.findElement(By.id("TPL_password_1"));
		element.click();
			
		Thread.sleep(1000);
		for(int i=0;i<password.length();i++){
			Thread.sleep(rand.nextInt(1000));
			element.sendKeys(""+password.charAt(i));
		}
		
		Thread.sleep(2000);
		
		element=driver.findElement(By.id("J_SubmitStatic"));
		element.click();
		
		Thread.sleep(5000);
		
		driver.get("https://upload.taobao.com/auction/json/reload_cats.htm?customId=&fenxiaoProduct=&path=next&pv=&sid=50011972");
	
		System.out.println(driver.getPageSource());
		
		driver.quit();
	}
}