import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class Reduce implements Runnable {
	
	private static Integer count = 0;
	private int id;
	private int countOfOperations;

	public Reduce(){
		this.id = Reduce.count++;
	}
	
	@Override
	public void run() {
		for(;;){
			try {
				Entry<String, ArrayList<Integer>> array = Globals.commonDict.pollLastEntry();
				if (array == null){
					return;
				}
				Globals.modelDict.addValue(array.getKey(), array.getValue());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				
		}
		
		
	}

}
