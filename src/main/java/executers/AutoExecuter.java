package executers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import FootballApi.FootballAPImain;
import FootballApi.MatchAPI;
import PRIMEbigdata.PRIMEBigdataEvent;

public class AutoExecuter {

	public static void main(String[] args) {
		FootballAPImain api = new FootballAPImain();
		ArrayList<Integer> idsToUpdate = new ArrayList<Integer>();
		for (String input : args) {
			idsToUpdate.add(Integer.parseInt(input));
		}
//	 Integer[] listIds = {94643, 94650}; //sabado 11hrs
//	    Integer[] listIds = {94641, 94646}; //sabado 17hrs
//		Integer[] listIds = {94648}; //sabado 19hrs
//	Integer[] listIds = {94647}; //sabado 21hrs		
//		Integer[] listIds = {94644}; //domingo 11hrs
//		Integer[] listIds = {94642, 94649}; //domingo 16hrs
		
		//window 5 min 
		String[] args1 = "localhost:9092 localhost:2181 100 200 20 round20/ 1 1 5".split(" ");
		String[] args2 = "localhost:9092 localhost:2181 100 200 20 round20/ 1 2 5".split(" ");
		String[] args3 = "localhost:9092 localhost:2181 100 200 20 round20/ 1 3 5".split(" ");
		
		//window 10 min 
		String[] args4 = "localhost:9092 localhost:2181 100 200 20 round20/ 1 1 10".split(" ");
		String[] args5 = "localhost:9092 localhost:2181 100 200 20 round20/ 1 2 10".split(" ");
		String[] args6 = "localhost:9092 localhost:2181 100 200 20 round20/ 1 3 10".split(" ");
				
		String[] argsKafka = ("localhost:9092 C:/Users/Brasileiro/eclipse/workspace2/EventMatching/inputsFootball/round20" + " " + Arrays.toString(args)).split(" ");
		try {
			//update games
			api.updateMatch("C:\\Users\\Brasileiro\\eclipse\\workspace2\\EventMatching\\inputsFootball\\round20", idsToUpdate);
			System.out.println("Updated ... " + Arrays.toString(idsToUpdate.toArray()));
			for (MatchAPI m : api.loadMatch("C:\\Users\\Brasileiro\\eclipse\\workspace2\\EventMatching\\inputsFootball\\round20")) {//final_brazilcup-players
				System.out.println(m.getId() + ": " + m.getHome().getName() + " - " + m.getAway().getName() + " > " + m.getPlayers().size() + " - " + m.getHome().getNicknames().split(",").length + " - " + m.getAway().getNicknames().split(",").length);
			}
			
			//Run primes (6 versions)
			ThreadCreator t1 = new ThreadCreator(args1, true);
			t1.run();
			System.out.println("Running Thread 1");
			ThreadCreator t2 = new ThreadCreator(args2, true);
			t2.run();
			System.out.println("Running Thread 2");
			ThreadCreator t3 = new ThreadCreator(args3, true);
			t3.run();
			System.out.println("Running Thread 3");
			
			ThreadCreator t4 = new ThreadCreator(args4, true);
			t4.run();
			System.out.println("Running Thread 4");
			ThreadCreator t5 = new ThreadCreator(args5, true);
			t5.run();
			System.out.println("Running Thread 5");
			ThreadCreator t6 = new ThreadCreator(args6, true);
			t6.run();
			System.out.println("Running Thread 6");
			
			Thread.sleep(8000);
			//Run Kafka
			ThreadCreator tkafka = new ThreadCreator(argsKafka, false);
			tkafka.run();
			System.out.println("Running Kafka Matches");
			
			
			Thread.sleep(115 * 60 * 1000);
			//stop the threads
			System.out.println("Stopping ... at " + new Date());
			t1.stop();
			System.out.println("Stopped Thread 1");
			t2.stop();
			System.out.println("Stopped Thread 2");
			t3.stop();
			System.out.println("Stopped Thread 3");
			t4.stop();
			System.out.println("Stopped Thread 4");
			t5.stop();
			System.out.println("Stopped Thread 5");
			t6.stop();
			System.out.println("Stopped Thread 6");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
