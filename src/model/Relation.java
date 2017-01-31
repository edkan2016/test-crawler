package model;

import javax.persistence.*;

import crawler.FBCrawler;

@Entity
public abstract class Relation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long pk;

	private String last_time = FBCrawler.sCRAWL_TIME;
    private int count=1, crawl=1;
    private float avg_count = 1;
    
    private void updateLastTime() { last_time = FBCrawler.sCRAWL_TIME; }
	public void incrementCount() { 
		count++;
		updateAvg();
	}
	private void incrementCrawl() { 
		crawl++;
		updateAvg();
	}
	private void updateAvg() { if (crawl > 0) avg_count = count/(float)crawl; }
	
	@Override
	public String toString() {
		return getClass().getSimpleName()+" PKCntCrwAvg:{"+pk+"/"+last_time+"/"+count+"/"+crawl+"/"+avg_count+"} ";
	}

	static Relation retrieveRelation(EntityManager em, String sQuery, Class<? extends Relation> castClass) {
		Relation relation = (Relation) em.createNativeQuery(sQuery, castClass).getSingleResult();
		relation.incrementCrawl();
		relation.incrementCount();
		relation.updateLastTime();
		em.merge(relation);
		return relation;
	}
}