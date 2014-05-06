import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;

/**
 * This thread will read each line of the given file and insert it into the mapping queue to be processed.
 * If the queue is full, the thread will wait for a space to free up to two seconds, if no space is available line will be skipped.
 */

public class GenesReader<T extends ClassificationTask> implements Runnable{
		private final String fileName;
		private final BlockingQueue<T> bufferQueue;
		
		public GenesReader(String fileName, BlockingQueue<T> bufferQueue){
			this.fileName = fileName;
			this.bufferQueue = bufferQueue;
		}

		@Override
		public void run() {
			try (BufferedReader reader = new BufferedReader(new FileReader(fileName)))
			{
				String tmpStr;
				int i = 1;
				while ((tmpStr = reader.readLine()) != null) {
					bufferQueue.offer((T) new TrainingTask(tmpStr, i), 2, TimeUnit.SECONDS);
					i++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

}
