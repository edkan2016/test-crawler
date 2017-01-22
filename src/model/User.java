package model;

import java.util.*;
import javax.persistence.*;

@Entity
public class User {

    public User(){}
    public User(String href) {
    	this.href = href;
    }
        
    @Id
    @Column(name = "href", unique = true)
    private String href;
    
    @OneToMany(cascade=CascadeType.MERGE)
    private List<ShareRelation> TL_SHARE = new ArrayList<>();
        
    @OneToMany(cascade=CascadeType.MERGE)
    private List<CommentRelation> COMMENT = new ArrayList<>();
    
    String getHref() { return href; }
    public List<ShareRelation> getShareRelations() {
		return TL_SHARE;
	}
	public void addShareRelation(ShareRelation shareRelation) {
		TL_SHARE.add(shareRelation);
	}
	public List<CommentRelation> getCommentRelations() {
		return COMMENT;
	}
	public User addCommentRelation(CommentRelation commentRelation) {
		COMMENT.add(commentRelation);
		return this;
	}
	@Override
	public String toString() {
		return "{"+href+": CommentRelations:"+COMMENT+" ShareRelations:"+TL_SHARE+"}";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((href == null) ? 0 : href.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (href == null) {
			if (other.href != null)
				return false;
		} else if (!href.equals(other.href))
			return false;
		return true;
	}

	public static User getUser(EntityManager em, String sHREF) {
		String sQuery = "MATCH (u:User{href:'"+sHREF+"'}) RETURN u";
		try {
			return (User) em.createNativeQuery( sQuery, User.class ).getSingleResult();
		} catch (NoResultException ex) {
			return new User(sHREF);
		}
	}
}