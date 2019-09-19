//package PRIMEbigdata;
//
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Properties;
//import java.util.Set;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.apache.flink.api.common.accumulators.IntCounter;
//import org.apache.flink.api.common.functions.AggregateFunction;
//import org.apache.flink.api.common.functions.FlatMapFunction;
//import org.apache.flink.api.common.functions.MapFunction;
//import org.apache.flink.api.java.tuple.Tuple;
//import org.apache.flink.api.java.tuple.Tuple2;
//import org.apache.flink.streaming.api.TimeCharacteristic;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.datastream.DataStreamSink;
//import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
//import org.apache.flink.streaming.api.datastream.WindowedStream;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.windowing.time.Time;
//import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
//import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
//import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
//import org.apache.flink.util.Collector;
//
//
//import DataStructures.Attribute;
//import DataStructures.BlockString;
//import DataStructures.BlockStructure;
//import DataStructures.EntityNode;
//import DataStructures.EntityProfile;
//import DataStructures.MetablockinNodeAggregation;
//import DataStructures.MetablockingNode;
//import DataStructures.TupleSimilarity;
//import tokens.KeywordGenerator;
//import tokens.KeywordGeneratorImpl;
//
////localhost:9092 localhost:2181 20 200 20 outputs/
//public class Metablocking {
//	
//	private IntCounter numLines = new IntCounter();
//	
//	public static void main(String[] args) throws Exception {
//		
//		
//		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//		env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
//		
////		StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment("flink-master", 8081, "/home/user/udfs.jar");
//		env.setParallelism(Integer.parseInt(args[6]));
////		ExecutionEnvironment env = ExecutionEnvironment
////		        .createRemoteEnvironment("flink-master", 8081, "/home/user/udfs.jar");
//
//		Properties properties = new Properties();
//		properties.setProperty("bootstrap.servers", args[0]);
//		// only required for Kafka 0.8
//		properties.setProperty("zookeeper.connect", args[1]);
//		properties.setProperty("group.id", "test");
//		properties.setProperty("enable.auto.commit", "true");
//		properties.setProperty("auto.offset.reset", "earliest");
//		//fast session timeout makes it more fun to play with failover
////		properties.setProperty("session.timeout.ms", "10000");
//		//These buffer sizes seem to be needed to avoid consumer switching to
//		//a mode where it processes one bufferful every 5 seconds with multiple
//		//timeouts along the way.  No idea why this happens.
//		//some properties was based in this flink project https://github.com/big-data-europe/pilot-sc4-flink-kafka-consumer/blob/master/src/main/resources/consumer.props
//		properties.setProperty("fetch.min.bytes", "50000");
//		properties.setProperty("receive.buffer.bytes", "1000000");//1000000
//		properties.setProperty("max.partition.fetch.bytes", "5000000");//5000000
//		
//		
//		DataStream<String> lines = env.addSource(new FlinkKafkaConsumer011<String>("mytopic", new SimpleStringSchema(), properties));
//		
//		DataStream<EntityProfile> entities = lines.rebalance().map(s -> new EntityProfile(s));
//		
//		//Receive the entities and extract the token from the attribute values.
//		DataStream<Tuple2<Integer, EntityNode>> entitiesTokens = entities.rebalance().flatMap(new FlatMapFunction<EntityProfile, Tuple2<Integer, EntityNode>>() {
//			
//			@Override
//			public void flatMap(EntityProfile e, Collector<Tuple2<Integer, EntityNode>> output) throws Exception {
//				Set<Integer> cleanTokens = new HashSet<Integer>();
//
//				for (Attribute att : e.getAttributes()) {
//					Set<String> tokens = generateTokens(att.getValue());
//					for (String string : tokens) {
//						cleanTokens.add(string.hashCode());
//					}
//				}
//				
////				if ((se.isSource() && se.getKey() == 514) || (!se.isSource() && se.getKey() == 970)) {
////					System.out.println();
////				}
//				
//				
//				for (Integer tk : cleanTokens) {
//					EntityNode node = new EntityNode(tk, e.getKey(), cleanTokens, e.isSource(), Integer.parseInt(args[2]), e.getIncrementID());
//					output.collect(new Tuple2<Integer, EntityNode>(tk, node));
//				}
//				
//			}
//			
//			private Set<String> generateTokens(String string) {
//				if (string.length() > 19 && string.substring(0, 19).equals("http://dbpedia.org/")) {
//					String[] uriPath = string.split("/");
//					string = uriPath[uriPath.length-1];
//				}
//				
//				//To improve quality, use the following code
//				Pattern p = Pattern.compile("[^a-zA-Z\\s0-9]");
//				Matcher m = p.matcher("");
//				m.reset(string);
//				String standardString = m.replaceAll("");
//				
//				KeywordGenerator kw = new KeywordGeneratorImpl();
//				return kw.generateKeyWords(standardString);
//			}
////			
////			private String[] generateTokensForBigData(String string) {
////				if (string.length() > 19 && string.substring(0, 19).equals("http://dbpedia.org/")) {
////					String[] uriPath = string.split("/");
////					string = uriPath[uriPath.length-1];
////				}
////				
////				return string.split("[\\W_]");
////			}
//			
//			
//		});
//		
//		
//		//Applies the token as a key.
//		WindowedStream<Tuple2<Integer, EntityNode>, Tuple, TimeWindow> tokenKeys = 
//				entitiesTokens.rebalance().keyBy(0).timeWindow(Time.seconds(Integer.parseInt(args[3])), Time.seconds(Integer.parseInt(args[4])));//define the window
//		
//		
//		//Group the entities with the same token (blocking using the token as a key).
//		//define the blocks based on the tokens <tk, [n1,n2,n3]>, where n is a node.
//		SingleOutputStreamOperator<BlockStructure> tokenBlocks = tokenKeys.aggregate(new AggregateFunction<Tuple2<Integer,EntityNode>, BlockStructure, BlockStructure>() {
//
//			@Override
//			public BlockStructure add(Tuple2<Integer, EntityNode> entity, BlockStructure acc) {
//				BlockStructure block = new BlockStructure();
//				block.setId(entity.f0);
//				block.merge(acc);
//				block.setCurrentIncrement(entity.f1.getIncrementId());
//				if (entity.f1.isSource()) {
//					block.addInSource(entity.f1);
//				} else {
//					block.addInTarget(entity.f1);
//				}
//				return block;
//			}
//
//			@Override
//			public BlockStructure createAccumulator() {
//				return new BlockStructure();
//			}
//
//			@Override
//			public BlockStructure getResult(BlockStructure acc) {
//				return acc;
//			}
//
//			@Override
//			public BlockStructure merge(BlockStructure block1, BlockStructure block2) {
//				BlockStructure block = new BlockStructure();
//				block.merge(block1);
//				block.merge(block2);
//				
//				return block;
//			}
//
//		});
//		
//		
//		//put the rows in the format <e1, b1>
//		SingleOutputStreamOperator<Tuple2<String, Integer>> pairEntityBlock = tokenBlocks.rebalance().flatMap(new FlatMapFunction<BlockStructure, Tuple2<String, Integer>>() {
//
//			@Override
//			public void flatMap(BlockStructure block, Collector<Tuple2<String, Integer>> output) throws Exception {
////				List<Tuple2<Integer, MetablockingNode>> values = Lists.newArrayList(arg1);
////				ArrayList<Tuple2<String, Integer>> output = new ArrayList<Tuple2<String, Integer>>();
//				
//				for (EntityNode eSource : block.getFromSource()) {
//					output.collect(new Tuple2<String, Integer>("S" + eSource.getId(), block.getId()));
//				}
//				
//				for (EntityNode eTarget : block.getFromTarget()) {
//					output.collect(new Tuple2<String, Integer>("T" + eTarget.getId(), block.getId()));
//				}
//				
//			}
//			
//		});
//		
//		
//		WindowedStream<Tuple2<String, Integer>, Tuple, TimeWindow> entitySetBlocks = 
//				pairEntityBlock.rebalance().keyBy(0).timeWindow(Time.seconds(Integer.parseInt(args[3])));//define the window
//		
//		
//		//coloca as tuplas no formato <e1, [b1,b2]>
//		SingleOutputStreamOperator<BlockString> entitySetBlocksAgg = entitySetBlocks.aggregate(new AggregateFunction<Tuple2<String, Integer>, BlockString, BlockString>() {
//
//			@Override
//			public BlockString add(Tuple2<String, Integer> entity, BlockString acc) {
//				BlockString block = new BlockString();
//				if (entity.f0.charAt(0) == 'S') {
//					block.setSource(true);
//				} else {
//					block.setSource(false);
//				}
//				block.setEntityId(Integer.parseInt(entity.f0.substring(1, entity.f0.length())));
//				block.merge(acc);
//				block.addBlock(entity.f1);
//				return block;
//			}
//
//			@Override
//			public BlockString createAccumulator() {
//				return new BlockString();
//			}
//
//			@Override
//			public BlockString getResult(BlockString acc) {
//				return acc;
//			}
//
//			@Override
//			public BlockString merge(BlockString block1, BlockString block2) {
//				BlockString block = new BlockString();
//				block.merge(block1);
//				block.merge(block2);
//				
//				return block;
//			}
//
//		});
//		
//		
//		//coloca as tuplas no formato <b1, (e1, b1, b2)>
//		SingleOutputStreamOperator<Tuple2<Integer, MetablockingNode>> blockEntityAndAllBlocks = entitySetBlocksAgg.rebalance().flatMap(new FlatMapFunction<BlockString, Tuple2<Integer, MetablockingNode>>() {
//
//			@Override
//			public void flatMap(BlockString block, Collector<Tuple2<Integer, MetablockingNode>> output) throws Exception {
//				MetablockingNode metaNode;
//				if (block.isSource()) {
//					metaNode = new MetablockingNode(block.getEntityId(), true);
//				} else {
//					metaNode = new MetablockingNode(block.getEntityId(), false);
//				}
//				for (Integer idBlocks : block.getBlocks()) {
//					metaNode.addInBlocks(idBlocks);
//				}
//				
//				for (Integer idBlocks : block.getBlocks()) {
//					output.collect(new Tuple2<Integer, MetablockingNode>(idBlocks, metaNode));
//				}
//				
//			}
//			
//		});
//		
//		
//		//coloca as tuplas no formato <b1, [(e1, b1, b2), (e2, b1), (e3, b1, b2)]>
//		WindowedStream<Tuple2<Integer, MetablockingNode>, Tuple, TimeWindow> blockPreprocessed = 
//				blockEntityAndAllBlocks.rebalance().keyBy(0).timeWindow(Time.seconds(Integer.parseInt(args[3])));//define the window
//		
//		
//		
//		//coloca as tuplas no formato <b1, [(e1, b1, b2), (e2, b1), (e3, b1, b2)]>
//		SingleOutputStreamOperator<MetablockinNodeAggregation> blockPreprocessedAgg = blockPreprocessed.aggregate(new AggregateFunction<Tuple2<Integer, MetablockingNode>, MetablockinNodeAggregation, MetablockinNodeAggregation>() {
//
//			@Override
//			public MetablockinNodeAggregation add(Tuple2<Integer, MetablockingNode> blockTuple, MetablockinNodeAggregation acc) {
//				MetablockinNodeAggregation block = new MetablockinNodeAggregation();
//				block.setBlockId(blockTuple.f0);
//				block.merge(acc);
//				block.addBlock(blockTuple.f1);
//				return block;
//			}
//
//			@Override
//			public MetablockinNodeAggregation createAccumulator() {
//				return new MetablockinNodeAggregation();
//			}
//
//			@Override
//			public MetablockinNodeAggregation getResult(MetablockinNodeAggregation acc) {
//				return acc;
//			}
//
//			@Override
//			public MetablockinNodeAggregation merge(MetablockinNodeAggregation block1, MetablockinNodeAggregation block2) {
//				MetablockinNodeAggregation block = new MetablockinNodeAggregation();
//				block.merge(block1);
//				block.merge(block2);
//				
//				return block;
//			}
//
//		});
//		
//		
//		SingleOutputStreamOperator<Tuple2<Integer, EntityNode>> entitySimilarities = blockPreprocessedAgg.rebalance().flatMap(new FlatMapFunction<MetablockinNodeAggregation, Tuple2<Integer, EntityNode>>() {
//
//			@Override
//			public void flatMap(MetablockinNodeAggregation block, Collector<Tuple2<Integer, EntityNode>> output) throws Exception {
//				for (MetablockingNode eSource : block.getSource()) {
//					EntityNode e = new EntityNode(-1, eSource.getEntityId(), null, true, Integer.MAX_VALUE, -1);
//					for (MetablockingNode eTarget : block.getTarget()) {
////						if (eSource.getIncrementId() == block.getCurrentIncrement() || eTarget.getIncrementId() == block.getCurrentIncrement()) {
//							double sim = calculateSimilarity(block.getBlockId(), eSource.getBlocks(), eTarget.getBlocks());
//							if (sim >= 0) {
//								e.addNeighbor(new TupleSimilarity(eTarget.getEntityId(), sim));
//							}
////						} 
////						else {
////							System.out.println();
////						}
//					}
//					
//					output.collect(new Tuple2<Integer, EntityNode>(eSource.getEntityId(), e));
//				}
//				
//			}
//			
//			private double calculateSimilarity(Integer blockKey, Set<Integer> ent1, Set<Integer> ent2) {
//				int maxSize = Math.max(ent1.size() - 1, ent2.size() - 1);
//				Set<Integer> intersect = new HashSet<Integer>(ent1);
//				intersect.retainAll(ent2);
//
//				// MACOBI strategy
//				if (!Collections.min(intersect).equals(blockKey)) {
//					return -1;
//				}
//
//				if (maxSize > 0) {
//					double x = (double) intersect.size() / maxSize;
//					return x;
//				} else {
//					return 0;
//				}
//			}
//		});
//		
//		
//		
//		WindowedStream<Tuple2<Integer, EntityNode>, Tuple, TimeWindow> entityNeighborhood = 
//				entitySimilarities.keyBy(0).timeWindow(Time.seconds(Integer.parseInt(args[3])));
//		
//		
//		SingleOutputStreamOperator<EntityNode> graph = entityNeighborhood.aggregate(new AggregateFunction<Tuple2<Integer,EntityNode>, EntityNode, EntityNode>() {
//
//			@Override
//			public EntityNode add(Tuple2<Integer, EntityNode> entity, EntityNode acc) {
//				EntityNode newEntity = new EntityNode();
//				newEntity.setId(entity.f1.getId());
//				newEntity.setMaxNumberOfNeighbors(Integer.parseInt(args[2]));
//				newEntity.addAllNeighbors(entity.f1.getNeighbors());
//				newEntity.addAllNeighbors(acc.getNeighbors());
//				return newEntity;
//			}
//
//			@Override
//			public EntityNode createAccumulator() {
//				return new EntityNode();
//			}
//
//			@Override
//			public EntityNode getResult(EntityNode acc) {
//				return acc;
//			}
//
//			@Override
//			public EntityNode merge(EntityNode e1, EntityNode e2) {
//				EntityNode newEntity = new EntityNode();
//				newEntity.setId(e1.getId());
//				newEntity.setMaxNumberOfNeighbors(Integer.parseInt(args[2]));
//				newEntity.addAllNeighbors(e1.getNeighbors());
//				newEntity.addAllNeighbors(e2.getNeighbors());
//				return newEntity;
//			}
//		});
//		
//		
//		
//		//Execute a pruning of the neighbors
//		DataStreamSink<String> output = graph.rebalance().map(new MapFunction<EntityNode, String>() {
//			@Override
//			public String map(EntityNode node) throws Exception {
////				if (node.getId() == 30) {
////					System.out.println();
////				}
//				node.pruningWNP();
//				return node.getId() + ">" + node.toString();
//			}
//		}).writeAsText(args[5]);
//		
//		
//		
//		env.execute();
//	}
//}
