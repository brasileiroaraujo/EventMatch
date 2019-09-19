package Quartz;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.jena.sparql.function.library.date;

public class MyTimerTask {

	static Timer timer;

    public MyTimerTask(int seconds) {
        //Get the Date corresponding to 11:01:00 pm today.
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        calendar.set(Calendar.MINUTE, 7);
        calendar.set(Calendar.SECOND, 0);
        Date timeStart = calendar.getTime();
        calendar.set(Calendar.SECOND, 10);
        Date timeEnd = calendar.getTime();

        timer = new Timer();
//        timer.schedule(new RemindTask(), timeStart);
        timer.schedule(new Stop(), timeEnd);
        
//        timer.schedule(new RemindTask(), );
    }

    class RemindTask extends TimerTask {
        public void run() {
        	while (true) {
        		System.out.println("Time's up!" + new Date());
        		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
            
//            timer.cancel(); //Terminate the timer thread
        }
    }
    
    class Stop extends TimerTask {
        public void run() {
        	System.out.println("Stop!");
            timer.cancel(); //Terminate the timer thread
        }
    }

    public static void main(String args[]) {
        
        new MyTimerTask(5);
        System.out.println("Task scheduled.");
    }
}
