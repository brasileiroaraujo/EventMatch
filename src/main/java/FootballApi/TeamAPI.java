package FootballApi;

import java.io.Serializable;

public class TeamAPI implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2612272231603643974L;
	private Integer id;
	private String name;
	private String country;
	private String venue_name;
	private String venue_address;
	private String venue_city;
	private String nicknames;
	
	private final String split2 = "#=#";
	
	
	public TeamAPI(Integer id, String name, String country, String venue_name, String venue_address, String venue_city, String nicknames) {
		super();
		this.id = id;
		this.name = name;
		this.country = country;
		this.venue_name = venue_name;
		this.venue_address = venue_address;
		this.venue_city = venue_city;
		this.nicknames = nicknames;
	}
	
	
	public TeamAPI(String stringStandardFormat) {
		String[] splitedString = stringStandardFormat.split(split2);
		this.id = Integer.parseInt(splitedString[0]);
		this.name = splitedString[1];
		this.country = splitedString[2];
		this.venue_name = splitedString[3];
		this.venue_address = splitedString[4];
		this.venue_city = splitedString[5];
		this.nicknames = splitedString[6];
	}


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getVenue_name() {
		return venue_name;
	}


	public void setVenue_name(String venue_name) {
		this.venue_name = venue_name;
	}


	public String getVenue_address() {
		return venue_address;
	}


	public void setVenue_address(String venue_address) {
		this.venue_address = venue_address;
	}


	public String getVenue_city() {
		return venue_city;
	}


	public void setVenue_city(String venue_city) {
		this.venue_city = venue_city;
	}
	
	public String stringStandard() {
		return id + split2 + name + split2 + country + split2 + venue_name + split2 + venue_address + split2 + venue_city + split2 + nicknames;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public String getSplit2() {
		return split2;
	}


	public String getNicknames() {
		return nicknames;
	}


	public void setNicknames(String nicknames) {
		this.nicknames = nicknames;
	}

}
