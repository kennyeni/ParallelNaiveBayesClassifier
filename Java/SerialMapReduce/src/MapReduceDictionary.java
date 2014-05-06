import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

public class MapReduceDictionary extends ConcurrentSkipListMap<String, ArrayList<Integer>>{
	
	private static final long serialVersionUID = 1L;

	public void addValue(String key, int value) throws InterruptedException {
		ArrayList<Integer> absentArr = new ArrayList<Integer>();
		putIfAbsent(key, absentArr);
		
		synchronized (get(key)) {
			get(key).add(value);
		}
	}
	
	
}