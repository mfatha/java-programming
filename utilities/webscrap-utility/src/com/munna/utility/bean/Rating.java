package com.munna.utility.bean;

import java.io.Serializable;

/**
 * The Class Rating.
 * 
 * @author Janardhanan V S
 */
public class Rating implements Serializable {

	private int worth;
	private int campus;
	private int placements;
	private int infra;
	private int faculty;
	private float overall;

	private static final long serialVersionUID = 7153069149744765923L;

	public float getWorth() {
		return worth;
	}

	public void setWorth(int worth) {
		this.worth = worth;
	}

	public int getCampus() {
		return campus;
	}

	public void setCampus(int campus) {
		this.campus = campus;
	}

	public int getPlacements() {
		return placements;
	}

	public void setPlacements(int placements) {
		this.placements = placements;
	}

	public int getInfra() {
		return infra;
	}

	public void setInfra(int infra) {
		this.infra = infra;
	}

	public int getFaculty() {
		return faculty;
	}

	public void setFaculty(int faculty) {
		this.faculty = faculty;
	}

	public float getOverall() {
		return overall;
	}

	public void setOverall(float overall) {
		this.overall = overall;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + worth;
		result = prime * result + campus;
		result = prime * result + placements;
		result = prime * result + infra;
		result = prime * result + faculty;
		result = prime * result + Float.floatToIntBits(overall);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Rating)) {
			return false;
		}
		Rating other = (Rating) obj;
		if (worth != other.worth) {
			return false;
		}
		if (campus != other.campus) {
			return false;
		}
		if (infra != other.infra) {
			return false;
		}
		if (placements != other.placements) {
			return false;
		}
		if (faculty != other.faculty) {
			return false;
		}
		if (Float.floatToIntBits(overall) != Float.floatToIntBits(other.overall)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Rating [worth=" + worth + ", campus=" + campus + ", placements=" + placements + ", infra=" + infra
				+ ", faculty=" + faculty + ", overall=" + overall + "]";
	}

}