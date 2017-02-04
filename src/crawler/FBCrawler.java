package crawler;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.*;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import model.CommentRelation;
import model.ShareRelation;
import model.User;

public class FBCrawler {

	public static final String sCRAWL_TIME = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	
	public static void main(String[] args) throws Exception {
		System.setProperty("webdriver.gecko.driver",
				"C:\\Users\\edkan\\OneDrive - connect.hku.hk\\programming\\geckodriver.exe");
		
		WebDriver driver = new FirefoxDriver();
		
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("ogm-neo4j");
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
		    
//		    crawlTimeline(driver, "yingmanhon", "英文瀚 Travis Ying", emf).forEach(commentor->{
		    crawlTimeline(driver, "nelsonkei", "Nelson Li", emf).forEach(commentor->{
//		    crawlTimeline(driver, "edward.kan.12", "Edward Kan", emf).forEach(commentor->{
//		    crawlTimeline(driver, "jack.yue.58", "Jack Yue", emf).forEach(commentor->{
		    	try {
		    		crawlTimeline(driver, commentor.getHref(), commentor.getName(), emf);
		    	} catch (Exception ex) {
		    		ex.printStackTrace();
		    	}
		    });
		} finally {
			driver.close();
			emf.close();
		}
		System.out.println("Finished!");
	}
	
	private static Set<User> crawlTimeline(WebDriver driver, String sTimeline, String sTLName, EntityManagerFactory emf) 
		throws InterruptedException {
		EntityManager em = emf.createEntityManager();
	    driver.navigate().to("https://www.facebook.com/"+sTimeline);
	    System.out.print("Scrolling "+sTLName+"... ");
		JavascriptExecutor js = ((JavascriptExecutor) driver);
		for (int i=1; i<=10; i++) {
			js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
			System.out.print(i+" ");
			System.out.flush();
		    Thread.sleep(3000l);
		}
		System.out.println("");

		EntityTransaction tx = em.getTransaction(); 
		tx.begin();
		User timelineUser = User.getUser(em, sTimeline, sTLName);
		Map<String,CommentRelation> hrefCommentRelation = new HashMap<>();
		Map<String,ShareRelation> hrefShareRelation = new HashMap<>();
		Set<User> commentors = new HashSet<>();
		
		driver.findElements(
			By.xpath(
				"//a[contains(@href,'www.facebook.com') and (contains(@href,'fref') or @class='profileLink')]"
//					"//div[@id='initial_browse_result']//a[contains(@href,'www.facebook.com') and contains(@href,'ref')]"
			)).forEach(ele->{
				String sURL = ele.getAttribute("href");
				String sHREF = sURL.substring("https://www.facebook.com/".length(), 
											  sURL.contains("?")?sURL.lastIndexOf('?'):sURL.length());
				if (sHREF.endsWith("/")) sHREF = sHREF.substring(0, sHREF.length()-1);
				if (sHREF.equals(sTimeline) || sHREF.equals("profile.php")) return;  // Skip login related links
				String sClass = ele.getAttribute("class");
				
				// Comments
				if (" UFICommentActorName".equals(sClass)) {
					User commentor = User.getUser(em, sHREF, ele.getText());
					
					// Search in memory first
					CommentRelation commentRelation = hrefCommentRelation.get(sHREF);
					// Not in memory -> Add
					if (commentRelation == null) {
						commentRelation = CommentRelation.getCommentRelation(em, timelineUser, commentor);							
						hrefCommentRelation.put(sHREF, commentRelation);
						commentors.add(commentor);
						System.out.println("Commentor:"+commentor+"\n  "+commentRelation);
					}
					// In memory -> Increment count
					else {
						commentRelation.incrementCount();
						System.out.println("Commentor:"+commentor+"\n  "+commentRelation);
						em.merge(commentRelation);
					}
				}
				// Shares
				else if (sClass==null || sClass.isEmpty() || sClass.equals("profileLink")) {
					String sName = ele.getText();
					if (sName==null || sName.isEmpty()) sName = ele.getAttribute("innerHTML");
					
					User originalPoster = User.getUser(em, sHREF, sName);
					
					// Search in memory first
					ShareRelation shareRelation = hrefShareRelation.get(sHREF);
					// Not in memory -> Add
					if (shareRelation == null) {
						shareRelation = ShareRelation.getShareRelation(em, timelineUser, originalPoster);
						hrefShareRelation.put(sHREF, shareRelation);
						System.out.println(shareRelation);
					}
					// In memory -> Increment count
					else {
						shareRelation.incrementCount();
						System.out.println(shareRelation);
						em.merge(shareRelation);
					}
				}
			});
		
		// Update DB
		tx.commit();
		em.close();
		
		return commentors;
	}
}
