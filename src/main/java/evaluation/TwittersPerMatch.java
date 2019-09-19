package evaluation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class TwittersPerMatch {
	
	private final String HOST = "localhost";
	private final int PORT = 27017;
	private final String DATABASE = "EventProject";
	private final String COLLECTION = "TwitterTeste";
	private final String PRIME_OUTPUT_PATH = "outputsCup/3-10;19;56.txt";
	private final String OUTPUT_PATH = "outputs/Twitters2-3-";

	public static void main(String[] args) {
		TwittersPerMatch main = new TwittersPerMatch();
		int idMatch = 243014;
		Set<String> twitters = main.getTwittersMacth(idMatch);
		main.writeTwitters(twitters, idMatch);
	}
	
	private void writeTwitters(Set<String> twitters, int idMatch) {
		try (FileWriter writer = new FileWriter(OUTPUT_PATH+idMatch+".txt"); 
			BufferedWriter bw = new BufferedWriter(writer)) {
			bw.write("Match " + idMatch + " total twitters: " + twitters.size() + "\n");
			for (String tw : twitters) {
				bw.write("[" + tw + "]\n");
			}

		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
		
	}

	private Set<String> getTwittersMacth(int idMacth) {
		HashSet<Long> listOfTwittersIds = readOutput(PRIME_OUTPUT_PATH, idMacth);
		HashSet<String> listOfTwitters = queryTwitters(listOfTwittersIds);
		
		return listOfTwitters;
	}
	
	private HashSet<String> queryTwitters(HashSet<Long> listOfTwittersIds) {
		HashSet<String> listOfTwitters = new HashSet<String>();
		MongoClient mongoClient = new MongoClient(HOST, PORT);
		MongoDatabase database = mongoClient.getDatabase(DATABASE);
		MongoCollection<Document> collection = database.getCollection(COLLECTION);
		
		for (Long twId : listOfTwittersIds) {
			BasicDBObject whereQuery = new BasicDBObject();
			whereQuery.put("id_str", "" + twId);
			FindIterable<Document> cursor = collection.find(whereQuery);
			for (Document document : cursor) {
				String text = document.get("text") == null ? document.get("full_text").toString() : document.get("text").toString();
				listOfTwitters.add(text);
			}
		}
		
		return listOfTwitters;
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
