package com.munna.utility.bean;

import java.io.Serializable;

/**
 * The Class Review.
 * 
 * @author Janardhanan V S
 */
public class Review implements Serializable {

	private String reviewer;
	private int batch;
	private Rating rating;
	private boolean recommended;
	private String courseReviewed;
	private String placement;
	private String infrastructure;
	private String faculty;
	private String other;
	private String genericReview;

	private static final long serialVersionUID = -125283249208902102L;

	public String getReviewer() {
		return reviewer;
	}

	public void setReviewer(String reviewer) {
		this.reviewer = reviewer;
	}

	public int getBatch() {
		return batch;
	}

	public void setBatch(int batch) {
		this.batch = batch;
	}

	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public boolean isRecommended() {
		return recommended;
	}

	public void setRecommended(boolean recommended) {
		this.recommended = recommended;
	}

	public String getCourseReviewed() {
		return courseReviewed;
	}

	public void setCourseReviewed(String courseReviewed) {
		this.courseReviewed = courseReviewed;
	}

	public String getPlacement() {
		return placement;
	}

	public void setPlacement(String placement) {
		this.placement = placement;
	}

	public String getInfrastructure() {
		return infrastructure;
	}

	public void setInfrastructure(String infrastructure) {
		this.infrastructure = infrastructure;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getGenericReview() {
		return genericReview;
	}

	public void setGenericReview(String genericReview) {
		this.genericReview = genericReview;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + batch;
		result = prime * result + ((courseReviewed == null) ? 0 : courseReviewed.hashCode());
		result = prime * result + ((faculty == null) ? 0 : faculty.hashCode());
		result = prime * result + ((genericReview == null) ? 0 : genericReview.hashCode());
		result = prime * result + ((infrastructure == null) ? 0 : infrastructure.hashCode());
		result = prime * result + ((other == null) ? 0 : other.hashCode());
		result = prime * result + ((placement == null) ? 0 : placement.hashCode());
		result = prime * result + ((rating == null) ? 0 : rating.hashCode());
		result = prime * result + (recommended ? 1231 : 1237);
		result = prime * result + ((reviewer == null) ? 0 : reviewer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Review)) {
			return false;
		}

		Review other = (Review) obj;
		if (batch != other.batch) {
			return false;
		}
		if (recommended != other.recommended) {
			return false;
		}
		if (courseReviewed == null) {
			if (other.courseReviewed != null) {
				return false;
			}
		} else if (!courseReviewed.equals(other.courseReviewed)) {
			return false;
		}
		if (faculty == null) {
			if (other.faculty != null) {
				return false;
			}
		} else if (!faculty.equals(other.faculty)) {
			return false;
		}
		if (genericReview == null) {
			if (other.genericReview != null) {
				return false;
			}
		} else if (!genericReview.equals(other.genericReview)) {
			return false;
		}
		if (infrastructure == null) {
			if (other.infrastructure != null) {
				return false;
			}
		} else if (!infrastructure.equals(other.infrastructure)) {
			return false;
		}
		if (this.other == null) {
			if (other.other != null) {
				return false;
			}
		} else if (!this.other.equals(other.other)) {
			return false;
		}
		if (placement == null) {
			if (other.placement != null) {
				return false;
			}
		} else if (!placement.equals(other.placement)) {
			return false;
		}
		if (rating == null) {
			if (other.rating != null) {
				return false;
			}
		} else if (!rating.equals(other.rating)) {
			return false;
		}
		if (reviewer == null) {
			if (other.reviewer != null) {
				return false;
			}
		} else if (!reviewer.equals(other.reviewer)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Review [reviewer=" + reviewer + ", batch=" + batch + ", rating=" + rating + ", recommended="
				+ recommended + ", courseReviewed=" + courseReviewed + ", placement=" + placement + ", infrastructure="
				+ infrastructure + ", faculty=" + faculty + ", other=" + other + ", genericReview=" + genericReview
				+ "]";
	}
}
