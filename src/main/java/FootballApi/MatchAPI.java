package FootballApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import DataStructures.TokenExtractor;


public class MatchAPI implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8726335055513003717L;
	private String date;
	private String venue;
	private TeamAPI home;
	private TeamAPI away;
	private Integer id;
	private List<PlayerAPI> players;
	
	private final String split1 = "<<>>";
	private final String split2 = "#=#";
	
	public MatchAPI(Integer id, String date, String venue, TeamAPI home, TeamAPI away) {
		super();
		this.id = id;
		this.date = date;
		this.venue = venue;
		this.home = home;
		this.away = away;
	}
	
	public MatchAPI(String stringStandardFormat) {
		String[] splitedString = stringStandardFormat.split(split1);
		this.id = Integer.parseInt(splitedString[0]);
		this.date = splitedString[1];
		this.venue = splitedString[2];
		this.home = new TeamAPI(splitedString[3]);
		this.away = new TeamAPI(splitedString[4]);
		
		players = new ArrayList<PlayerAPI>();
		
		for (int i = 5; i < splitedString.length; i++) {
			players.add(new PlayerAPI(splitedString[i]));
		}
		
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public TeamAPI getHome() {
		return home;
	}

	public void setHome(TeamAPI home) {
		this.home = home;
	}

	public TeamAPI getAway() {
		return away;
	}

	public void setAway(TeamAPI away) {
		this.away = away;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<PlayerAPI> getPlayers() {
		return players;
	}

	public void setPlayers(List<PlayerAPI> players) {
		this.players = players;
	}
	
	
	
	public String convertToStandardFormat() {
		String output = id + split1 + date + split1 + venue + split1 + home.stringStandard() + split1
				+ away.stringStandard();
		
		for (PlayerAPI player : players) {
			output += (split1 + player.stringStandard());
		}
		return output;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getSplit1() {
		return split1;
	}

	public String getSplit2() {
		return split2;
	}
	
	public Set<String> getAllTokensExp1() {
		Set<String> tokens = new HashSet<String>();
		
//		tokens.addAll(TokenExtractor.generateTokens(getVenue()));
		tokens.addAll(TokenExtractor.generateTokens(getHome().getName()));
//		tokens.addAll(TokenExtractor.generateTokens(getHome().getVenue_name()));
//		tokens.addAll(TokenExtractor.generateTokens(getHome().getVenue_city()));
		tokens.addAll(TokenExtractor.generateTokens(getAway().getName()));
//		tokens.addAll(TokenExtractor.generateTokens(getAway().getVenue_name()));
//		tokens.addAll(TokenExtractor.generateTokens(getAway().getVenue_city()));
		
		return tokens;
	}
	
	public Set<String> getAllTokensExp2() {
		Set<String> tokens = new HashSet<String>();
		
//		tokens.addAll(TokenExtractor.generateTokens(getVenue()));
		tokens.addAll(TokenExtractor.generateTokens(getHome().getName()));
		tokens.addAll(TokenExtractor.generateTokens(getAway().getName()));
		
		for (PlayerAPI p : players) {
			tokens.addAll(TokenExtractor.generateTokens(p.getName()));
//			tokens.addAll(TokenExtractor.generateTokens(p.getNumber().toString()));
		}
		
		return tokens;
	}
	
	
	public Set<String> getAllTokensExp3() {
		Set<String> tokens = new HashSet<String>();
		
//		tokens.addAll(TokenExtractor.generateTokens(getVenue()));
		tokens.addAll(TokenExtractor.generateTokens(getHome().getName()));
		tokens.addAll(TokenExtractor.generateTokens(getHome().getNicknames()));
		tokens.addAll(TokenExtractor.generateTokens(getAway().getName()));
		tokens.addAll(TokenExtractor.generateTokens(getAway().getNicknames()));
		
		for (PlayerAPI p : players) {
			tokens.addAll(TokenExtractor.generateTokens(p.getName()));
//			tokens.addAll(TokenExtractor.generateTokens(p.getNumber().toString()));
		}
		
		return tokens;
	}
	
	
	@Override
	public String toString() {
		return id + "\n" + date + "\n" + venue + "\n" + home.getName() + "\n"
				+ away.getName() + "\n" + players.size();
	}
	
}
