package model;

import javax.persistence.*;

@Entity
public class ShareRelation extends Relation {
	
	@ManyToOne
	private User SHARE;
	
	private ShareRelation() {}
	private ShareRelation(User poster) {
		SHARE = poster;
	}

	@Override
	public String toString() {
		return super.toString()+"Sharing "+SHARE;
	}
	
	public static ShareRelation getShareRelation(EntityManager em, User timelineUser, User poster) {
		String sQuery = "MATCH (:User{href:'"+timelineUser.getHref()+"'})"
								+ "-[:TL_SHARE]->(r:Relation)-[:SHARE]->"
								+ "(:User{href:'"+poster.getHref()+"'})"
					  + "RETURN r";
		
		ShareRelation relation;
		try {
			relation = (ShareRelation) retrieveRelation(em, sQuery, ShareRelation.class);
//			System.out.println("Existing share relation "+relation.pk);
		} catch (NoResultException ex) {
			relation = new ShareRelation(poster);
			em.persist(relation);
//			System.out.println("New share relation "+relation.pk);			
			timelineUser.addShareRelation(relation);
			em.merge(timelineUser);
		}
		
		return relation;
	}
}