package model;

import javax.persistence.*;

import crawler.FBCrawler;

@Entity
public abstract class Relation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    private String last_time = FBCrawler.sCRAWL_TIME;
    private int count=1, crawl=1;
    private float avg_count = 1;
    
    void updateLastTime() { last_time = FBCrawler.sCRAWL_TIME; }
	public void incrementCount() { 
		count++;
		updateAvg();
	}
	void incrementCrawl() { 
		crawl++;
		updateAvg();
	}
	private void updateAvg() { if (crawl > 0) avg_count = count/(float)crawl; }
	
	@Override
	public String toString() {
		return "{"+last_time+"/"+count+"/"+crawl+"/"+avg_count+"}";
	}
}