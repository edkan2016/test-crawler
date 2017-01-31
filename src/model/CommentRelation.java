package model;

import javax.persistence.*;

@Entity
public class CommentRelation extends Relation {		

	@ManyToOne
	private User TL_COMMENT;
	
	private CommentRelation() {}
	private CommentRelation(User postUser) {
		TL_COMMENT = postUser;
	}
	
	@Override
	public String toString() {
		return super.toString()+"Timeline of "+TL_COMMENT;
	}
	
	public static CommentRelation getCommentRelation(EntityManager em, User timelineUser, User commentor) {
		String sQuery = "MATCH (:User{href:'"+timelineUser.getHref()+"'})"
								+ "<-[:TL_COMMENT]-(r:Relation)<-[:COMMENT]-"
								+ "(:User{href:'"+commentor.getHref()+"'})"
					  + "RETURN r";
		
		CommentRelation relation;
		try {
			relation = (CommentRelation) retrieveRelation(em, sQuery, CommentRelation.class);
//			System.out.println("Existing comment relation "+relation.pk);
		} catch (NoResultException ex) {
			relation = new CommentRelation(timelineUser);
			em.persist(relation);
//			System.out.println("New comment relation "+relation.pk);
			commentor.addCommentRelation(relation);
			em.merge(commentor);
		}
		return relation;
	}
}
