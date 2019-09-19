package evaluation;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ShowEventResults {

	public static void main(String[] args) {
		MongoClient mongoClient = new MongoClient("localhost", 27017);
		MongoDatabase database = mongoClient.getDatabase("EventProject");
		MongoCollection<Document> collection = database.getCollection("Twitter");
		
		
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("id_str", "1165703365441019905");
		FindIterable<Document> cursor = collection.find(whereQuery);
		for (Document document : cursor) {
			System.out.println(document);
		}
		
	}

}
