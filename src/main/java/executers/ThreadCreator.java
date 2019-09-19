package executers;

import DataProducer.KafkaDataStreamingFootball;
import PRIMEbigdata.PRIMEBigdataEvent;

public class ThreadCreator {
	private PRIMEBigdataEvent prime = new PRIMEBigdataEvent();
	KafkaDataStreamingFootball kafkaMatches = new KafkaDataStreamingFootball();
	private Thread thread;

	public ThreadCreator(String[] args, boolean flag) {
		thread = new Thread() {

			@Override
			public void run() {
				try {
					if (flag) {
						prime.main(args);
					} else {
						kafkaMatches.main(args);
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	}
	
	public void run() {
		thread.start();
	}
	
	public void stop() {
		try {
			thread.interrupt();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
