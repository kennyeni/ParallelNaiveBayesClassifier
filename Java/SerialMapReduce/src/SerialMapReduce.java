import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SerialMapReduce {
	
	/**
	 * Create model of probabilities based on counting
	 */
	private static void createModel() throws InterruptedException, BrokenBarrierException{
		GenesReader<TrainingTask> reader = new GenesReader<TrainingTask>("newgenestrain.tab", Globals.bufferQueue); // The first thread that will be executed is the reader
		reader.run();	
		Map map = new Map();
		map.run();
		Reduce r = new Reduce();
		r.run();
		
		/*Iterator iterator = Globals.modelDict.keySet().iterator();
		while (iterator.hasNext()) {  
		   String key = iterator.next().toString();  
		   String value = Globals.modelDict.get(key).toString();  
		   
		   System.out.println(key + " " + value);  
		}*/
	}
	
	private static void getProbabilities() throws InterruptedException, BrokenBarrierException {
		GenesReader<ClassificationTask> reader = new GenesReader<ClassificationTask>("newgenesblind.tab", Globals.classificationQueue);
		reader.run();
		ClassificationMap map = new ClassificationMap();
		map.run();
	}
	
	public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
		long inicio = System.currentTimeMillis(); // Start counting time
		//Globals.NUM_MAP = Integer.parseInt(args[0]);
		//Globals.NUM_REDUCE = Integer.parseInt(args[1]);
		//System.out.print(Globals.NUM_MAP + " " + Globals.NUM_REDUCE);
		
		// Initialize all global vars
		Globals.bufferQueue = new ArrayBlockingQueue<TrainingTask>(200);
		Globals.classificationQueue = new ArrayBlockingQueue<ClassificationTask>(200);
		Globals.commonDict = new MapReduceDictionary();
		Globals.modelDict = new ModelDictionary();

		createModel();
		
		getProbabilities();
		
		long fin = System.currentTimeMillis();
		
		System.out.print(" " + (fin - inicio) / 1000.0);
		//System.out.println();
	}
}
