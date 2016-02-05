package eoh.mobile.eohapp.models;

/**
 * @author Will Hennessy
 * 
 *  A class to represent an EOH exhibit.
 *  Includes exhibit name, building, time, etc.
 */

public class Exhibit {
	
	private static final String noMajor = "Engineering";
	
	private String name;
	private String description;
	private String major;
	private String building;
	private String room;
	private String buildingAndRoom;
	private String address;
	private String url;
	private boolean isFavorite;
	private int id;
	
	public Exhibit(String name, String description, String major, String building, String room, String address, String url, boolean isFavorite, int id) {
		this.setName(name);
		this.setDescription(description);
		this.setBuilding(building);
		this.setRoom(room);
		this.setAddress(address);
		this.setUrl(url);
		this.setFavorite(isFavorite);
		this.setId(id);
		
		if( !major.equals("") )
			this.setMajor(major);
		else
			this.setMajor(noMajor);
		
		this.buildingAndRoom = building;
		if(room != null && room.length() > 0) // if it has a room, append it with a comma
			this.buildingAndRoom += (", " + room);
	}
	
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getBuilding() {
		return this.building;
	}
	
	public void setBuilding(String building) {
		this.building = building;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String readableLocation() {
		return buildingAndRoom;
	}

}
