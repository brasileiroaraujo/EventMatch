package evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class TweetIDPerMatch {
	private final static String PRIME_OUTPUT_PATH = "round20/Sab11/";
	private static String OUTPUT_PATH = "TwitterEvaluation/TwittersIDS-";
	private static int idMatch = 94662;

	public static void main(String[] args) {
		TweetIDPerMatch main = new TweetIDPerMatch();
		File folder = new File(PRIME_OUTPUT_PATH);
		File[] listOfFiles = folder.listFiles();
		
		Set<Long> twitters = new HashSet<Long>();
		for (File file : listOfFiles) {
			twitters.addAll(main.getTwittersMacth(idMatch, file.getAbsolutePath()));
		}
		main.writeTwitters(twitters, idMatch);
	}
	
	private void writeTwitters(Set<Long> twitters, int idMatch) {
		try {
			FileOutputStream fos = new FileOutputStream(OUTPUT_PATH + idMatch);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(twitters);
			oos.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private HashSet<Long> getTwittersMacth(int idMacth, String filePath) {
		HashSet<Long> listOfTwittersIds = readOutput(filePath, idMacth);
		
		return listOfTwittersIds;
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
