package DataProducer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import DataStructures.Attribute;
import DataStructures.EntityProfile;
import DataStructures.IdDuplicates;
import DataStructures.StatisticalSummarization;
import FootballApi.MatchAPI;
import info.debatty.java.lsh.MinHash;
import tokens.KeywordGenerator;
import tokens.KeywordGeneratorImpl;

//localhost:9092 inputs/round15-pl
public class KafkaDataStreamingFootball {
	private static String INPUT_PATH1;
	private static boolean APPLY_ATT_SELECTION;
//	private static Set<String> allTokens = new HashSet<String>();
	
	public static void main(String[] args) throws Exception {
		INPUT_PATH1 = args[1];
		
		// create execution environment
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		
		
		//TOPIC
        final String topicName = "mytopicAPI";

        FlinkKafkaProducer011<String> producer = new FlinkKafkaProducer011<String>(
        		args[0],            // broker list
                topicName,                  // target topic
                new SimpleStringSchema());

		// add a simple source which is writing some strings
		DataStream<String> messageStream = env.addSource(new SimpleStringGenerator());

		// write stream to Kafka
		messageStream.addSink(producer);

		env.execute();
	}

	public static class SimpleStringGenerator implements SourceFunction<String> {
		private static final long serialVersionUID = 2174904787118597072L;
		boolean running = true;
		long i = 0;
		@Override
		public void run(SourceContext<String> ctx) throws Exception {
			//CHOOSE THE INPUT PATHS
	        int currentIncrement = 0;
	        
//	        FlinkKafkaProducer011<String, String> producer = new FlinkKafkaProducer011<>(props);
	        List<MatchAPI> EntityListSource = null;
	        
			// reading the files
			ObjectInputStream ois1;
			try {
//				ois1 = new ObjectInputStream(new FileInputStream(INPUT_PATH1));
//				ois2 = new ObjectInputStream(new FileInputStream(INPUT_PATH2));
				EntityListSource = loadMatches(INPUT_PATH1);
				System.out.println("Source loaded ... " + EntityListSource.size());
//				ois1.close();
//				ois2.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
//			if (APPLY_ATT_SELECTION) {
//				System.out.println("Attribute selection starting ...");
//				attributeSelection(EntityListSource, EntityListTarget);
//				System.out.println("Attribute selection ending ...");
//			}
			
			System.out.println("sending ...");
			
			int count = 0;
			for (MatchAPI match : EntityListSource) {
				if (match.getId() == 243014) {
					ctx.collect(match.convertToStandardFormat());
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
//	        System.out.println("Number of possible tokens: " + allTokens.size());
		}


		private List<Tuple2<EntityProfile, EntityProfile>> generateSourceOfMatches(List<EntityProfile> entityListSource,
				List<EntityProfile> entityListTarget, HashSet<IdDuplicates> gt) {
			List<Tuple2<EntityProfile, EntityProfile>> output = new ArrayList<Tuple2<EntityProfile, EntityProfile>>();
			List<EntityProfile> matchedSource = new ArrayList<EntityProfile>();
			List<EntityProfile> matchedTarget = new ArrayList<EntityProfile>();
			
			
			//define ids
			for (int i = 0; i < entityListSource.size(); i++) {
				entityListSource.get(i).setKey(i);
				entityListSource.get(i).setSource(true);
			}
			for (int i = 0; i < entityListTarget.size(); i++) {
				entityListTarget.get(i).setKey(i);
				entityListTarget.get(i).setSource(false);
			}
			
			Map<Integer, Integer> mapGT = new HashMap<Integer, Integer>();
			
			for (IdDuplicates pair : gt) {
				mapGT.put(pair.getEntityId1(), pair.getEntityId2());
			}
			
			for (EntityProfile eSource : entityListSource) {
				Integer match = mapGT.get(eSource.getKey());
				if (match != null) {
					EntityProfile eTarget = entityListTarget.get(match);
					matchedTarget.add(eTarget);
					matchedSource.add(eSource);
					output.add(new Tuple2<EntityProfile, EntityProfile>(eSource, eTarget));
				}
			}
			
			entityListTarget.removeAll(matchedTarget);
			entityListSource.removeAll(matchedSource);
			
			return output;
		}


		private List<MatchAPI> loadMatches(String INPUT_PATH1) {
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


		@Override
		public void cancel() {
			running = false;
		}
		
		
		private void attributeSelection(List<EntityProfile> entityListSource,
				List<EntityProfile> entityListTarget) {
			HashMap<String, Integer> tokensIDs = new HashMap<String, Integer>();
	        HashMap<String, Set<Integer>> attributeMapSource = new HashMap<String, Set<Integer>>();
	        HashMap<String, Set<Integer>> attributeMapTarget = new HashMap<String, Set<Integer>>();
	        HashMap<Integer, Integer> itemsFrequencySource = new HashMap<Integer, Integer>();
	        HashMap<Integer, Integer> itemsFrequencyTarget = new HashMap<Integer, Integer>();
	        int currentTokenID = 0;
	        double totalTokensSource = 0;
	        double totalTokensTarget = 0;
	        
	        for (EntityProfile e : entityListSource) {
				for (Attribute att : e.getAttributes()) {
					KeywordGenerator kw = new KeywordGeneratorImpl();
					for (String tk : kw.generateKeyWords(att.getValue())) {
						if (!tokensIDs.containsKey(tk)) {
							tokensIDs.put(tk, currentTokenID++);
						}
						
						itemsFrequencySource.put(tokensIDs.get(tk), (itemsFrequencySource.getOrDefault(tokensIDs.get(tk), 0) + 1));
						
						if (attributeMapSource.containsKey(att.getName())) {
							attributeMapSource.get(att.getName()).add(tokensIDs.get(tk));
						} else {
							attributeMapSource.put(att.getName(), new HashSet<Integer>());
							attributeMapSource.get(att.getName()).add(tokensIDs.get(tk));
						}
						totalTokensSource++;
					}
				}
			}
	        
	        
	        for (EntityProfile e : entityListTarget) {
				for (Attribute att : e.getAttributes()) {
					KeywordGenerator kw = new KeywordGeneratorImpl();
					for (String tk : kw.generateKeyWords(att.getValue())) {
						if (!tokensIDs.containsKey(tk)) {
							tokensIDs.put(tk, currentTokenID++);
						}
						
						itemsFrequencyTarget.put(tokensIDs.get(tk), (itemsFrequencyTarget.getOrDefault(tokensIDs.get(tk), 0) + 1));
						
						if (attributeMapTarget.containsKey(att.getName())) {
							attributeMapTarget.get(att.getName()).add(tokensIDs.get(tk));
						} else {
							attributeMapTarget.put(att.getName(), new HashSet<Integer>());
							attributeMapTarget.get(att.getName()).add(tokensIDs.get(tk));
						}
						totalTokensTarget++;
					}
				}
			}
	        
	        
	        //blacklist based on entropy
			Set<Integer> blackListSource = StatisticalSummarization.getBlackListEntropy(attributeMapSource, itemsFrequencySource, totalTokensSource);
			Set<Integer> blackListTarget = StatisticalSummarization.getBlackListEntropy(attributeMapTarget, itemsFrequencyTarget, totalTokensTarget);
			
			//generate the MinHash
			HashMap<String, int[]> attributeHashesSource = new HashMap<String, int[]>();
			HashMap<String, int[]> attributeHashesTarget = new HashMap<String, int[]>();
			MinHash minhash = new MinHash(120, tokensIDs.size());//Estimate the signature size after
			for (String att : attributeMapSource.keySet()) {
				attributeHashesSource.put(att, minhash.signature(attributeMapSource.get(att)));
			}
			for (String att : attributeMapTarget.keySet()) {
				attributeHashesTarget.put(att, minhash.signature(attributeMapTarget.get(att)));
			}
			
			//generate the similarity matrix
			ArrayList<String> attFromSource = new ArrayList<String>(attributeHashesSource.keySet());
			ArrayList<String> attFromTarget = new ArrayList<String>(attributeHashesTarget.keySet());
			double[][] similarityMatrix = new double[attFromSource.size()][attFromTarget.size()];
			DescriptiveStatistics statistics = new DescriptiveStatistics();
			for (int i = 0; i < attFromSource.size(); i++) {
				for (int j = 0; j < attFromTarget.size(); j++) {
					double similarity = minhash.similarity(attributeHashesSource.get(attFromSource.get(i)), attributeHashesTarget.get(attFromTarget.get(j)));
					statistics.addValue(similarity);
					similarityMatrix[i][j] = similarity;
				}
			}
			
			//attribute selection (based on the matrix evaluation)
			double average = statistics.getMean();//similaritySum/(attFromSource.size() * attFromTarget.size());
//			double median = statistics.getPercentile(50); //the percentile in 50% represents the median
			
//			ArrayList<Integer> blackListSource = new ArrayList<Integer>();
//			ArrayList<Integer> blackListTarget = new ArrayList<Integer>();
			for (int i = 0; i < attFromSource.size(); i++) {
				int count = 0;
				for (int j = 0; j < attFromTarget.size(); j++) {
					if (similarityMatrix[i][j] >= average) { //use average or median
						count++;
					}
				}
				if (count == 0) {
					blackListSource.add(i);
				}
				count = 0;
			}			
			for (int i = 0; i < attFromTarget.size(); i++) {
				int count = 0;
				for (int j = 0; j < attFromSource.size(); j++) {
					if (similarityMatrix[j][i] >= average) {
						count++;
					}
				}
				if (count == 0) {
					blackListTarget.add(i);
				}
				count = 0;
			}
			
			//removing the bad attributes
			for (EntityProfile e : entityListSource) {
				for (Integer index : blackListSource) {
					Attribute att = findAttribute(e.getAttributes(), attFromSource.get(index));
					e.getAttributes().remove(att);
				}
			}
			for (EntityProfile e : entityListTarget) {
				for (Integer index : blackListTarget) {
					Attribute att = findAttribute(e.getAttributes(), attFromTarget.get(index));
					e.getAttributes().remove(att);
				}
			}
			
		}
		
		private static Attribute findAttribute(Set<Attribute> attributes, String string) {
			for (Attribute attribute : attributes) {
				if (attribute.getName().equalsIgnoreCase(string)) {
					return attribute;
				}
			}
			return null;
		}
	}

}
