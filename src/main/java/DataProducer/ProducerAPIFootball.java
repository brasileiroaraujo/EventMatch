package DataProducer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import FootballApi.MatchAPI;

//localhost:9092 inputs/round15-pl
public class ProducerAPIFootball {
	private static String INPUT_PATH1;
	private static boolean APPLY_ATT_SELECTION;
//	private static Set<String> allTokens = new HashSet<String>();
	
	public static void main(String[] args) throws Exception {
		// Set properties used to configure the producer
        Properties properties = new Properties();
        // Set the brokers (bootstrap servers)
        properties.setProperty("bootstrap.servers", args[0]);
        // Set how to serialize key/value pairs
        properties.setProperty("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.setProperty("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        // specify the protocol for Domain Joined clusters
        //properties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        
        
		INPUT_PATH1 = args[1];
		//TOPIC
        final String topicName = "mytopicAPI";
		
        List<MatchAPI> EntityListSource = null;
        
        List<Integer> listIdsMatches = new ArrayList<Integer>();
        for (String id : args[2].replace("[", "").replace("]", "").replace(" ", "").split(",")) {
			listIdsMatches.add(Integer.parseInt(id));
		}
        
		// reading the files
		ObjectInputStream ois1;
		try {
			EntityListSource = loadMatches(INPUT_PATH1);
			System.out.println("Source loaded ... " + EntityListSource.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("sending ...");
		
		int count = 0;
		for (MatchAPI match : EntityListSource) {
			if (listIdsMatches.contains(match.getId())) {
				ProducerRecord<String, String> text = new ProducerRecord<String, String>(topicName, match.convertToStandardFormat());
	        	System.out.println(text.value());
	        	producer.send(text);
				count++;
			}
			
		}

		
		
        System.out.println("----------------------------END------------------------------");
        Calendar data = Calendar.getInstance();
        int horas = data.get(Calendar.HOUR_OF_DAY);
        int minutos = data.get(Calendar.MINUTE);
        int segundos = data.get(Calendar.SECOND);
        System.out.println(horas + ":" + minutos + ":" + segundos);
        System.out.println("Data sent: " + count);
//        System.out.println("Number of possible tokens: " + allTokens.size());
	}
	
	private static List<MatchAPI> loadMatches(String INPUT_PATH1) {
		FileInputStream fis;
		ObjectInputStream ois;
		List<MatchAPI> matches = new ArrayList<MatchAPI>();
		try {
			fis = new FileInputStream(INPUT_PATH1);
			ois = new ObjectInputStream(fis);
			matches = (ArrayList<MatchAPI>) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return matches;
	}

}
