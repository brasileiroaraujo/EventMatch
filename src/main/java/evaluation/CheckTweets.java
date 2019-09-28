package evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import FootballApi.TeamAPI;
import scala.annotation.elidable;

public class CheckTweets {
	private static String INPUT_PATH = "TwitterEvaluation/TwittersIDS-94662";
	private final static String HOST = "localhost";
	private final static int PORT = 27017;
	private final static String DATABASE = "EventProject";
	private final static String COLLECTION = "Round20";

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		Set<Long> listOfIDs = loadIDs(INPUT_PATH);
		Set<Long> groundtruth = new HashSet<Long>();
		Scanner sc = new Scanner(System.in);
		
		int count = 1;
		for (Long id : listOfIDs) {
			System.out.println(count + "/" + listOfIDs.size());
			System.out.println(queryTwitters(id));
			System.out.println("Digite X caso o tweet não seja relacionado: ");
			sc.hasNext();
			if (sc.next().equalsIgnoreCase("x")) {
//				groundtruth.put(id, false);
				System.out.println("Falso!");
			} else {
				groundtruth.add(id);
				System.out.println("Match!");
			}
			System.out.println("---------------------------------------------");
			System.out.println();
			
		}
		sc.close();
		
		System.out.println("----------------------END--------------------");
		writeGroundtruth(groundtruth);

	}
	
	private static void writeGroundtruth(Set<Long> twitters) {
		try {
			FileOutputStream fos = new FileOutputStream(INPUT_PATH + "-Groundtruth");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(twitters);
			oos.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
	private static String queryTwitters(Long id) {
		MongoClient mongoClient = new MongoClient(HOST, PORT);
		MongoDatabase database = mongoClient.getDatabase(DATABASE);
		MongoCollection<Document> collection = database.getCollection(COLLECTION);
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("id_str", "" + id);
		FindIterable<Document> cursor = collection.find(whereQuery);
		String text = "";
		for (Document document : cursor) {
			text = document.get("text") == null ? document.get("full_text").toString() : document.get("text").toString();
		}
		
		return text;
	}
	
	private static Set<Long> loadIDs(String filePath) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(filePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Set<Long> teams = (HashSet<Long>) ois.readObject();
		ois.close();
		
		return teams;
	}

}
