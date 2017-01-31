package model;

import java.util.*;
import javax.persistence.*;

@Entity
public class User {

    public User(){}
    public User(String sHREF, String sName) {
    	href = sHREF;
    	name = sName;
    }
        
    @Id
    @Column(name = "href", unique = true)
    private String href;
    
    private String name;
    
    @OneToMany
    private Set<ShareRelation> TL_SHARE = new HashSet<>();
        
	@OneToMany
    private Set<CommentRelation> COMMENT = new HashSet<>();
    
    public String getHref() { return href; }
    public String getName() { return name; }
    
	void addShareRelation(ShareRelation shareRelation) {
		TL_SHARE.add(shareRelation);
	}
	void addCommentRelation(CommentRelation commentRelation) {
		COMMENT.add(commentRelation);
	}
	@Override
	public String toString() {
		return name+"{"+href+"(C"+COMMENT.size()+"S"+TL_SHARE.size()+")}";//+super.toString();
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

	public static User getUser(EntityManager em, String sHREF, String sName) {
		User user;
		String sQuery = "MATCH (u:User{href:'"+sHREF+"'}) RETURN u";
		try {
			user = (User) em.createNativeQuery(sQuery, User.class).getSingleResult();
			if (sName!=null && !sName.isEmpty() &&
				!user.name.equals(sName) && user.name.equals(user.href)) {
				user.name = sName;
				em.merge(user);
			}
		} catch (NoResultException ex) {
			user = new User(sHREF, sName==null||sName.isEmpty()?sHREF:sName);
			em.persist(user);
		}
		
		return user;
	}
}