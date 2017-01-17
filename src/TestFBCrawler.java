import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestFBCrawler {

	private static int i = 0;
	
	public static void main(String[] args) throws InterruptedException {
		System.setProperty("webdriver.gecko.driver",
				"C:\\Users\\edkan\\Documents\\facebook_collect\\geckodriver.exe");
		
		WebDriver driver = new FirefoxDriver();
		
		try {
			// Puts an Implicit wait, Will wait for 10 seconds before throwing
			// exception
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	
			// Launch website
			driver.navigate().to("http://www.facebook.com/");
		    driver.findElement(By.id("email")).clear();
		    driver.findElement(By.id("email")).sendKeys("edwardkan@astri.org");
		    driver.findElement(By.id("pass")).clear();
		    driver.findElement(By.id("pass")).sendKeys("Astri123");
		    driver.findElement(By.xpath("//input[@value='Log In']")).click();
		    Thread.sleep(3000l);
		    
		    driver.navigate().to("http://www.facebook.com/nelsonkei");	    
			JavascriptExecutor js = ((JavascriptExecutor) driver);
			
			for (int i=1; i<=10; i++) {
				js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
				System.out.println("Scroll "+i+"...");
			    Thread.sleep(3000l);
			}
	
			HashSet<String> set = new HashSet<>();
			try {
				driver.findElements(
					By.xpath(
//						"//a[contains(@href,'www.facebook.com') and contains(@href,'fref')]"
						"//div[@id='initial_browse_result']//a[contains(@href,'www.facebook.com') and contains(@href,'ref')]"
					)).forEach(ele->
						set.add(ele.getAttribute("href"))
				);
			} catch (Exception ex) {
				System.err.println("Error: "+ex);
			}

			System.out.println(set);

		} finally {
			// Close the Browser.
			driver.close();
		}
	}
}
