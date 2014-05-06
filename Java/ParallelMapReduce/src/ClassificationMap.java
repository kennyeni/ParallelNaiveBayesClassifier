import java.util.concurrent.atomic.AtomicInteger;

public class ClassificationMap implements Runnable {
	
	private static AtomicInteger count = new AtomicInteger(); 
	private int id;
	private int countOfOperations;

	public ClassificationMap(){
		this.id = ClassificationMap.count.incrementAndGet();
	}
	
	@Override
	public void run() {
		for(;;){
			if (Globals.classificationQueue.isEmpty()){
				if (countOfOperations < 5)
					System.err.println("Classification Map thread #" + this.id + " only did " + countOfOperations);
				Globals.classifcationLatch.countDown();
				return;
			}
			try {
				ClassificationTask task = Globals.classificationQueue.take();
				task.classificate();
				System.out.println(task.toString());
				countOfOperations++;
				
			} catch (InterruptedException e) {
				if (Globals.bufferQueue.isEmpty())
					System.out.println("Map thread #" + this.id + " was killed normally");
				else
					e.printStackTrace();
			}
		}
		
		
	}

}
