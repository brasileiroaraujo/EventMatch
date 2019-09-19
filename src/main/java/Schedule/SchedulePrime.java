package Schedule;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.jena.sparql.function.library.date;

import DataProducer.KafkaDataStreamingFootball;
import FootballApi.FootballAPImain;
import PRIMEbigdata.PRIMEBigdataEvent;
import executers.AutoExecuter;

public class SchedulePrime {

	static Timer timer;

    public SchedulePrime() {
        //Get the Date corresponding to 11:01:00 pm today.
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 39);
        calendar.set(Calendar.SECOND, 0);
        Date timeStart = calendar.getTime();
        timer = new Timer();
        
        // 94656: Flamengo - Santos 17hrs
//        String listIds1 = "94656";
//        timer.schedule(new RemindTask(false, listIds1), timeStart);
        
        
        // 94655: Chapecoense-sc - Vasco DA Gama
        // 94659: Palmeiras - Cruzeiro
//        String listIds2 = "94655 94659";
//        calendar.set(Calendar.HOUR_OF_DAY, 19);
//        calendar.set(Calendar.MINUTE, 5);
//        calendar.set(Calendar.SECOND, 0);
//        timeStart = calendar.getTime();
//        timer.schedule(new RemindTask(false, listIds2), timeStart);
        
        
        // 94654: Ceara - Botafogo
//        String listIds3 = "94654";
//        calendar.set(Calendar.HOUR_OF_DAY, 21);
//        calendar.set(Calendar.MINUTE, 5);
//        calendar.set(Calendar.SECOND, 0);
//        timeStart = calendar.getTime();
//        timer.schedule(new RemindTask(false, listIds3), timeStart);
        
        
        //Domingo
//        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        
        
        // 94651: Atletico Paranaense - Avai
        // 94652: Atletico-MG - Internacional
//        String listIds4 = "94651 94652";
//        calendar.set(Calendar.HOUR_OF_DAY, 11);
//        calendar.set(Calendar.MINUTE, 5);
//        calendar.set(Calendar.SECOND, 0);
//        timeStart = calendar.getTime();
//        timer.schedule(new RemindTask(false, listIds4), timeStart);
        
        // 94657: Fluminense - Corinthians
        // 94658: Gremio - Goias
        // 94653: Bahia - Fortaleza EC
//        String listIds5 = "94657 94658 94653";
//        calendar.set(Calendar.HOUR_OF_DAY, 16);
//        calendar.set(Calendar.MINUTE, 32);
//        calendar.set(Calendar.SECOND, 0);
//        timeStart = calendar.getTime();
//        timer.schedule(new RemindTask(false, listIds5), timeStart);
        
        
        // 94660: Sao Paulo - CSA
        String listIds6 = "243014";
//        calendar.set(Calendar.HOUR_OF_DAY, 19);
//        calendar.set(Calendar.MINUTE, 5);
//        calendar.set(Calendar.SECOND, 0);
        timeStart = calendar.getTime();
        timer.schedule(new RemindTask(true, listIds6), timeStart);
    }

    class RemindTask extends TimerTask {
    	private boolean isLastExecution = true;
    	private String listIds;
        public RemindTask(boolean isLastExecution, String listIds) {
        	this.isLastExecution = isLastExecution;
        	this.listIds = listIds;
		}

		public void run() {
			AutoExecuter exec = new AutoExecuter();
			exec.main(listIds.split(" "));
			if (isLastExecution) {
				timer.cancel();
			}
        }
    }
    
    public static void main(String args[]) {
        new SchedulePrime();
        System.out.println("Task scheduled.");
    }
}
