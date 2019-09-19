package DbpediaApi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import FootballApi.TeamAPI;

public class AddNicknamesToTeams {
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		HashMap<String, String> teamNicknames = loadNicknames("C:/Users/Brasileiro/eclipse/workspace2/EventMatching/inputsFootball/WikiTimesBrasileiros.txt");
		List<TeamAPI> teams = loadTeams("C:/Users/Brasileiro/eclipse/workspace2/EventMatching/inputsFootball/teamsBrasileiroLeague/");
		for (TeamAPI teamAPI : teams) {
			String nicknames = teamNicknames.get(teamAPI.getName());
			if (nicknames != null) {
				teamAPI.setNicknames(nicknames);
			} else {
				System.err.println("Nicknames of " + teamAPI.getName() + " was not found!!!!");
			}
		}
		
		saveTeams("C:/Users/Brasileiro/eclipse/workspace2/EventMatching/inputsFootball/teamsBrasileiroLeagueWithNicknames/", teams);
		
	}
	
	private static List<TeamAPI> loadTeams(String filePath) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(filePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		List<TeamAPI> teams = (ArrayList<TeamAPI>) ois.readObject();
		ois.close();
		
		return teams;
	}

	private static void saveTeams(String filePath, List<TeamAPI> listOfTeamsWithNicknames) throws IOException {
		FileOutputStream fos = new FileOutputStream(filePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(listOfTeamsWithNicknames);
		oos.close();
		
	}
	
	
	private static HashMap<String, String> loadNicknames(String path) {
		HashMap<String, String> teamNicknames =  new HashMap<String, String>();
		BufferedReader br = null;
		FileReader fr = null;
		try {
			//br = new BufferedReader(new FileReader(FILENAME));
			fr = new FileReader(path);
			br = new BufferedReader(fr);
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] teamNicks = sCurrentLine.split(":");
				teamNicknames.put(teamNicks[0], teamNicks[1]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
		return teamNicknames;
	}

}
