package j.tool.selenium;

import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class AlibabaImagesGetter {
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
		driver.get("https://detail.1688.com/offer/589773343065.html");

		Thread.sleep(3000);
		
		String src=driver.getPageSource();
		
		driver.get("https://img.alicdn.com/tfscom/TB1kY7NyaL7gK0jSZFBXXXZZpXa?t=1586971729512");

		
		driver.quit();
	}
}
