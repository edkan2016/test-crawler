package model;

import javax.persistence.*;

@Entity
class ShareRelation extends Relation {
	
	@ManyToOne(cascade=CascadeType.MERGE)
	private User SHARE;
	
	public User getOriginalPoster() {
		return SHARE;
	}

	public void setOriginalPoster(User poster) {
		SHARE = poster;
	}

	private ShareRelation() {}
	public ShareRelation(User poster) {
		setOriginalPoster(poster);
	}

	@Override
	public String toString() {
		return super.toString()+"->"+SHARE;
	}
}