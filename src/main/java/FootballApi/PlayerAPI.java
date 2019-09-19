package FootballApi;

import java.io.Serializable;

public class PlayerAPI implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -234016229121309120L;
	private Integer id;
	private String name;
	private Integer number;
	private Integer id_team;
	private String name_team;
	
	private final String split2 = "#=#";
	
	
	public PlayerAPI(Integer id, String name, Integer number, Integer id_team, String name_team) {
		super();
		this.id = id;
		this.name = name;
		this.number = number;
		this.id_team = id_team;
		this.name_team = name_team;
	}
	
	public PlayerAPI(String stringStandardFormat) {
		String[] splitedString = stringStandardFormat.split(split2);
		this.id = Integer.parseInt(splitedString[0]);
		this.name = splitedString[1];
		this.number = Integer.parseInt(splitedString[2]);
		this.id_team = Integer.parseInt(splitedString[3]);
		this.name_team = splitedString[4];
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
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public Integer getId_team() {
		return id_team;
	}
	public void setId_team(Integer id_team) {
		this.id_team = id_team;
	}
	public String getName_team() {
		return name_team;
	}
	public void setName_team(String name_team) {
		this.name_team = name_team;
	}
	
	public String stringStandard() {
		return id + split2 + name + split2 + number + split2 + id_team + split2 + name_team;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getSplit2() {
		return split2;
	}
	

}
