import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParallelMapReduce {
	
	static ExecutorService mapPool;
	
	/**
	 * Create model of probabilities based on counting
	 */
	private static void createModel() throws InterruptedException, BrokenBarrierException{
		mapPool.execute(new GenesReader<TrainingTask>("newgenestrain.tab", Globals.bufferQueue)); // The first thread that will be executed is the reader
		Globals.readLatch.await(); // Wait for reading processes to finish
		for (int i = 0; i < Globals.NUM_MAP; i++) { // Create Mapping processes
			mapPool.execute(new Map());
		}
		Globals.mapLatch.await(); // Wait for all Map processes to finish
		
		for (int i = 0; i < Globals.NUM_REDUCE; i++) { // Create Reducing processes
			mapPool.execute(new Reduce());
		}
		
		Globals.reduceLatch.await();// Wait for all Map processes to finish
		/*Iterator iterator = Globals.modelDict.keySet().iterator();
		while (iterator.hasNext()) {  
		   String key = iterator.next().toString();  
		   String value = Globals.modelDict.get(key).toString();  
		   
		   System.out.println(key + " " + value);  
		}*/
	}
	
	private static void getProbabilities() throws InterruptedException, BrokenBarrierException {
		Globals.readLatch.reset();
		mapPool.execute(new GenesReader<ClassificationTask>("newgenesblind.tab", Globals.classificationQueue));
		Globals.readLatch.await();
		for (int i = 0; i < Globals.NUM_MAP; i++) { // Create Mapping processes
			mapPool.execute(new ClassificationMap());
		}
		Globals.classifcationLatch.await();
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
		Globals.reduceLatch = new CountDownLatch(Globals.NUM_REDUCE);
		Globals.mapLatch = new CountDownLatch(Globals.NUM_MAP);
		Globals.classifcationLatch = new CountDownLatch(Globals.NUM_MAP);
		Globals.readLatch = new CyclicBarrier(2);
		
		// Dynamic size thread pool
		mapPool = Executors.newCachedThreadPool(); // Create thread pool
		
		createModel();
		
		getProbabilities();
		
		long fin = System.currentTimeMillis();
		
		System.out.print(" " + (fin - inicio) / 1000.0);
		
		
		mapPool.shutdown(); // Shutdown allocated threads
		//System.out.println();
	}
}
