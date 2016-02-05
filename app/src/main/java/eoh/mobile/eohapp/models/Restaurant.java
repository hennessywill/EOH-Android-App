package eoh.mobile.eohapp.models;

/**
 * @author Will Hennessy
 * 
 *  A class to represent a restaurant pulled from the Google Places API.
 */

public class Restaurant {
	
	private String name;		// restaurant name
	private String vicinity;	// readable address
	private String reference;	// a reference to more details about the restaurant. Can be appended to Places URL.
	private double rating;		// rating out of 5.0
	private int openNow;	    // true if the restaurant is open at the time of the query
		
	public Restaurant(String name, String vicinity, String reference, double rating, int openNow) {
		this.setName(name);
		this.setVicinity(vicinity);
		this.setReference(reference);
		this.setRating(rating);
		this.setOpenNow(openNow);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVicinity() {
		return vicinity;
	}

	public void setVicinity(String vicinity) {
		this.vicinity = vicinity;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
	
	@Override
	public String toString() {
		return
			this.getName() + ", "
			+ this.getVicinity() + ", "
			+ this.getReference();
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}
	
	/** return a string verson of the floating point rating **/
	public String getRatingString() {
		if(rating == -1.0)
			return "?";
		return Double.toString(rating);
	}

	public boolean isOpenNow() {
		return openNow == 1;
	}

	public void setOpenNow(int openNow) {
		this.openNow = openNow;
	}
	
	/** return a string stating if the restaurant is currently open or closed **/
	public String getOpenNowString() {
		switch(openNow) {
		case 0:
			return "closed";
		case 1:
			return "open";
		default:
			return "";
		}
	}
}
