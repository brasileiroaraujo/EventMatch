package FootballApi;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class FootballCrawler {
	
	public static void main(String[] args) throws UnirestException, ParseException, IOException, ClassNotFoundException {
		String result = null;
		
		//results of matches per date(AAAA-MM-DD) and league (ID)
//		System.out.println(MatchesPerLeagueAndDate("357", "2019-08-11"));
		
		//results of matches per league (ID) and round (number)
		result = MatchesPerLeagueAndRound("357", "15");
		JSONObject json = JsonManager.parserToJson(result);
		saveMatch(json, "C:\\Users\\Brasileiro\\eclipse\\workspace2\\EventMatching\\inputsFootball\\round15-pl2");
//		for (Match m : loadMatch("C:\\Users\\Brasileiro\\eclipse\\workspace2\\EventMatching\\inputsFootball\\round15-pl2")) {
//			System.out.println(m.getHome().getName() + " - " + m.getAway().getName() + " > " + m.getPlayers().size());
//		}
		
		//teams per league (ID)
//		result = TeamsPerLeague("357");
//		JSONObject json = JsonManager.parserToJson(result);
//		saveTeams(json, "C:\\Users\\Brasileiro\\eclipse\\workspace2\\EventMatching\\inputsFootball\\teamsBrasileiro");
//		for (Team t : loadTeams("C:\\Users\\Brasileiro\\eclipse\\workspace2\\EventMatching\\inputsFootball\\teamsBrasileiro")) {
//			System.out.println(t.getName());
//		}
		
		
		//players per team (ID) and season (ano-ano or ano)
//		System.out.println(PlayesPerTeamAndSeason("131", "2019"));
		
		
		//players per match (ID)
//		System.out.println(PlayesPerMatch("94608")); //94608
		
		
		
		
		
		
		
		
		//converter unicode to string
//		System.out.println(ConverterUnicode.converter("Gustavo G\\u00f3mez"));
		
		
	}
	
	private static List<MatchAPI> loadMatch(String filePath) throws ClassNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(filePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<MatchAPI> matches = (ArrayList<MatchAPI>) ois.readObject();
		ois.close();
		
		return matches;
	}

	private static void saveMatch(JSONObject json, String filePath) throws IOException, JSONException, ClassNotFoundException, UnirestException, ParseException {
		List<MatchAPI> listOfMatches = new ArrayList<MatchAPI>();
		Iterator<Object> teams = json.getJSONObject("api").getJSONArray("fixtures").iterator();
		while (teams.hasNext()) {
			JSONObject match = (JSONObject)teams.next();
			MatchAPI matchObj = new MatchAPI(match.getInt("fixture_id"), match.getString("event_date"), match.getString("venue"), getTeam(match.getJSONObject("homeTeam").getInt("team_id")), getTeam(match.getJSONObject("awayTeam").getInt("team_id")));
			//get players in this game
			matchObj.setPlayers(getPlayers(matchObj.getId()));
			listOfMatches.add(matchObj);			
		}
		
		FileOutputStream fos = new FileOutputStream(filePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(listOfMatches);
		oos.close();
				
	}

	private static List<PlayerAPI> getPlayers(Integer id) throws UnirestException, ParseException {
		String result = PlayesPerMatch(id.toString());
		JSONObject json = JsonManager.parserToJson(result);
		
		List<PlayerAPI> listOfPlayers = new ArrayList<PlayerAPI>();
		Iterator<Object> players = json.getJSONObject("api").getJSONArray("players").iterator();
		while (players.hasNext()) {
			JSONObject player = (JSONObject)players.next();
			listOfPlayers.add(new PlayerAPI(player.getInt("player_id"), player.getString("player_name"), player.getInt("number"), player.getInt("team_id"), player.getString("team_name")));
		}
		
		return listOfPlayers;
	}

	private static TeamAPI getTeam(int id) throws ClassNotFoundException, IOException {
		List<TeamAPI> teams = loadTeams("C:\\Users\\Brasileiro\\eclipse\\workspace2\\EventMatching\\inputsFootball\\teamsBrasileiro");
		for (TeamAPI team : teams) {
			if (team.getId() == id) {
				return team;
			}
		}
		return null;
	}

	private static List<TeamAPI> loadTeams(String filePath) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(filePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<TeamAPI> teams = (ArrayList<TeamAPI>) ois.readObject();
		ois.close();
		
		return teams;
	}

	private static void saveTeams(JSONObject json, String filePath) throws IOException {
		List<TeamAPI> listOfTeams = new ArrayList<TeamAPI>();
		Iterator<Object> teams = json.getJSONObject("api").getJSONArray("teams").iterator();
		while (teams.hasNext()) {
			JSONObject team = (JSONObject)teams.next();
			listOfTeams.add(new TeamAPI(team.getInt("team_id"), team.getString("name"), team.getString("country"), team.getString("venue_name"), team.getString("venue_address"), team.getString("venue_city"), " "));
			
		}
		
		FileOutputStream fos = new FileOutputStream(filePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(listOfTeams);
		oos.close();
		
	}

	private static String MatchesPerLeagueAndDate(String leagueID, String date) throws UnirestException {
		HttpResponse<String> response = Unirest.get("https://api-football-v1.p.rapidapi.com/v2/fixtures/league/"+ leagueID + "/" + date)
				.header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
				.header("x-rapidapi-key", "e9de784972mshb38b9da4a26b039p118e54jsn683a62ea410a")
				.asString();
		
		return response.getBody();

	}
	
	private static String MatchesPerLeagueAndRound(String leagueID, String round) throws UnirestException {
		HttpResponse<String> response = Unirest.get("https://api-football-v1.p.rapidapi.com/v2/fixtures/league/" + leagueID + "/Regular_Season_-_" + round)
				.header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
				.header("x-rapidapi-key", "e9de784972mshb38b9da4a26b039p118e54jsn683a62ea410a")
				.asString();
		
		return response.getBody();

	}
	
	
	private static String TeamsPerLeague(String leagueID) throws UnirestException {
		HttpResponse<String> response =Unirest.get("https://api-football-v1.p.rapidapi.com/v2/teams/league/" + leagueID)
				.header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
				.header("x-rapidapi-key", "e9de784972mshb38b9da4a26b039p118e54jsn683a62ea410a")
				.asString();
		
		return response.getBody();

	}
	
	private static String PlayesPerTeamAndSeason(String teamID, String season) throws UnirestException {
		HttpResponse<String> response = Unirest.get("https://api-football-v1.p.rapidapi.com/v2/players/team/" + teamID + "/" + season)
				.header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
				.header("x-rapidapi-key", "e9de784972mshb38b9da4a26b039p118e54jsn683a62ea410a")
				.asString();
		
		return response.getBody();

	}
	
	private static String PlayesPerMatch(String matchID) throws UnirestException {
		HttpResponse<String> response = Unirest.get("https://api-football-v1.p.rapidapi.com/v2/players/fixture/" + matchID)
				.header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
				.header("x-rapidapi-key", "e9de784972mshb38b9da4a26b039p118e54jsn683a62ea410a")
				.asString();
		
		return response.getBody();

	}

}
