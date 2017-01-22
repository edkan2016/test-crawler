package model;

import javax.persistence.*;
import crawler.FBCrawler;

@Entity
public class CommentRelation extends Relation {		

	@ManyToOne(cascade=CascadeType.MERGE)
	private User TL_COMMENT;
	
	private CommentRelation() {}
	private CommentRelation(User postUser) {
		TL_COMMENT = postUser;
	}
	
	@Override
	public String toString() {
		return super.toString()+"->"+TL_COMMENT;
	}
	
	public static CommentRelation getCommentRelation(EntityManager em, 
			User timelineUser, String sCommentorHref) {
		String sQuery = "MATCH (:User{href:'"+timelineUser.getHref()+"'})"
								+ "<-[:TL_COMMENT]-(r:Relation)<-[:COMMENT]-"
								+ "(c:User{href:'"+sCommentorHref+"'})"
					  + "RETURN r";
		
		CommentRelation relation;
		try {
			relation = (CommentRelation) em.createNativeQuery(sQuery, CommentRelation.class).getSingleResult();
			relation.incrementCrawl();
			relation.incrementCount();
			relation.updateLastTime();
		} catch (NoResultException ex) {
			relation = new CommentRelation(timelineUser);
		}
		
		return relation;
	}
}
