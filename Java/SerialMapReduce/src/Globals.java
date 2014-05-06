import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Globals {
	
	public static BlockingQueue<TrainingTask> bufferQueue;
	public static MapReduceDictionary commonDict;
	public static ModelDictionary modelDict;
	public static int NUM_MAP = 5;
	public static int NUM_REDUCE = 3;
	public static final String[] categories = {"CEU", "GIH", "JPT", "ASW", "YRI"};
	public static BlockingQueue<ClassificationTask> classificationQueue;

}
