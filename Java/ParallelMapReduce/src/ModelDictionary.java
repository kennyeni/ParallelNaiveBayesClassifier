import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ModelDictionary extends ConcurrentSkipListMap<String, AtomicInteger>{
	
	private static final long serialVersionUID = 1L;

	public void addValue(String key, ArrayList<Integer> list) throws InterruptedException {
		AtomicInteger absentInt = new AtomicInteger();
		putIfAbsent(key, absentInt);
		
		synchronized (get(key)) {
			int value = 0;
			for(int i : list){
				value += i;
			}
			if (value == 0)
				remove(key);
			else
				get(key).addAndGet(value);
		}
	}
	
	
}