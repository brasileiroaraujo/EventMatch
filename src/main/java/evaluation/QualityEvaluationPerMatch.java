package evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import FootballApi.MatchAPI;

public class QualityEvaluationPerMatch {
	
//	private final String HOST = "localhost";
//	private final int PORT = 27017;
//	private final String DATABASE = "EventProject";
//	private final String COLLECTION = "TwitterTeste";
	private final static String PRIME_OUTPUT_PATH = "round20/Sab11/";
	private static String GROUNDTRUTH = "TwitterEvaluation/TwittersIDS-94662-Groundtruth";
	private static int idMatch = 94662;

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		QualityEvaluationPerMatch main = new QualityEvaluationPerMatch();
		File folder = new File(PRIME_OUTPUT_PATH);
		File[] listOfFiles = folder.listFiles();
		
		for (File file : listOfFiles) {
			Set<Long> twitters = main.getTwittersMacth(idMatch, file.getAbsolutePath());
			Set<Long> groundtruth = main.loadGroundtruth(GROUNDTRUTH);
			
			System.out.println("Match " + idMatch);
			System.out.println("File " + file.getName());
			int total = twitters.size();
			System.out.println("Number of Tweets detected: " + total);
			twitters.removeAll(groundtruth);
			int wrong = twitters.size();
			System.out.println("Number of Tweets detected wrong: " + wrong);
			System.out.println("Number of Tweets detected correctly: " + (total - wrong));
			System.out.println("------------------------------------");
			System.out.println();
		}
	}
	

	private HashSet<Long> getTwittersMacth(int idMacth, String filePath) {
		HashSet<Long> listOfTwittersIds = readOutput(filePath, idMacth);
		
		return listOfTwittersIds;
	}
	
	public static Set<Long> loadGroundtruth(String filePath) throws ClassNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(filePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Set<Long> matches = (HashSet<Long>) ois.readObject();
		ois.close();
		
		return matches;
	}
	
	private HashSet<Long> readOutput(String path, int idMacth) {
		HashSet<Long> listOfTwittersID = new HashSet<Long>();
		BufferedReader br = null;
		FileReader fr = null;
		try {
			//br = new BufferedReader(new FileReader(FILENAME));
			fr = new FileReader(path);
			br = new BufferedReader(fr);
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] matchAndTwitterIDs = sCurrentLine.split(">");
				int macthID = Integer.parseInt(matchAndTwitterIDs[0]);
				if (macthID == idMacth && matchAndTwitterIDs.length > 1) {
					for (String twId : matchAndTwitterIDs[1].split(",")) {
						listOfTwittersID.add(Long.parseLong(twId));
					}
				}
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
		return listOfTwittersID;
	}

}
