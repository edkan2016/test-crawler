package crawler;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import model.CommentRelation;
import model.User;

public class FBCrawler {

	public static final String sCRAWL_TIME = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	
	public static void main(String[] args) throws Exception {
		System.setProperty("webdriver.gecko.driver",
				"C:\\Users\\edkan\\Documents\\facebook_collect\\geckodriver.exe");
		
		WebDriver driver = new FirefoxDriver();
		
        EntityManagerFactory emf = null;
		try {
		    String sTimeline = "https://www.facebook.com/nelsonkei?fref=nf";
		    
			emf = Persistence.createEntityManagerFactory("ogm-neo4j");
			final EntityManager em = emf.createEntityManager();
			EntityTransaction tx = em.getTransaction();
			tx.begin();
//			System.exit(-1);

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
		    
		    //driver.navigate().to("http://www.facebook.com/nelsonkei");
		    driver.navigate().to(sTimeline);
		    
//			JavascriptExecutor js = ((JavascriptExecutor) driver);
//			for (int i=1; i<=10; i++) {
//				js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
//				System.out.print(i+" ");
//				System.out.flush();
//			    Thread.sleep(3000l);
//			}
//			System.out.println(" Finished scrolling");
	
			User timelineUser = User.getUser(em, sTimeline);
			Map<String,Object[]> hrefCommentorRelation = new HashMap<>();
			
			driver.findElements(
				By.xpath(
					"//a[contains(@href,'www.facebook.com') and contains(@href,'fref')]"
//						"//div[@id='initial_browse_result']//a[contains(@href,'www.facebook.com') and contains(@href,'ref')]"
				)).forEach(ele->{
					String sClass = ele.getAttribute("class");
					if (" UFICommentActorName".equals(sClass)) {
						String sCommentorHref = ele.getAttribute("href");
						
						// Search in memory first
						Object[] commentorRelation = hrefCommentorRelation.get(sCommentorHref);
						// Not in memory -> Add
						if (commentorRelation == null) {
							CommentRelation relation = 
								CommentRelation.getCommentRelation(em, timelineUser, sCommentorHref);
							
							User commentor = User.getUser(em, sCommentorHref);
							commentor.addCommentRelation(relation);
							
							hrefCommentorRelation.put(sCommentorHref, new Object[] {commentor, relation});
						}
						// In memory -> Increment count
						else ((CommentRelation)commentorRelation[1]).incrementCount();

						System.out.println("commentorRelation:"+Arrays.deepToString(commentorRelation));
					}
					else {}
				});
			
			// Update DB
			hrefCommentorRelation.values().forEach(commentorRelation->
				em.merge(commentorRelation[0]));
			tx.commit();
			em.close();
		} finally {
			driver.close();
			if (emf != null) emf.close();
		}
		System.out.println("Finished!");
	}
}
