import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class Reduce implements Runnable {
	
	private static AtomicInteger count = new AtomicInteger(); 
	private int id;
	private int countOfOperations;

	public Reduce(){
		this.id = Reduce.count.incrementAndGet();
	}
	
	@Override
	public void run() {
		for(;;){
			try {
				Entry<String, ArrayList<Integer>> array = Globals.commonDict.pollLastEntry();
				if (array == null){
					if (countOfOperations < 5)
						System.err.println("Reduce thread #" + this.id + " only did " + countOfOperations);
					Globals.reduceLatch.countDown();
					return;
				}
				Globals.modelDict.addValue(array.getKey(), array.getValue());
				countOfOperations++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				
		}
		
		
	}

}
