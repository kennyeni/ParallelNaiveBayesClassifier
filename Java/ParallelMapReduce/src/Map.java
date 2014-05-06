import java.util.concurrent.atomic.AtomicInteger;

public class Map implements Runnable {
	
	private static AtomicInteger count = new AtomicInteger(); 
	private int id;
	private int countOfOperations;

	public Map(){
		this.id = Map.count.incrementAndGet();
	}
	
	@Override
	public void run() {
		for(;;){
			if (Globals.bufferQueue.isEmpty()){
				if (countOfOperations < 5)
					System.err.println("Map thread #" + this.id + " only did " + countOfOperations);
				Globals.mapLatch.countDown();
				return;
			}
			try {
				TrainingTask task = Globals.bufferQueue.take();
				String label = task.getLabel();
				Globals.commonDict.addValue(label + "_class", 1);
				String[] attr = task.getAttrs();
				for(int i = 0; i < attr.length; i++){
					String featureValue = attr[i];
					if (featureValue.charAt(0) == '?') continue;
					Globals.commonDict.addValue(label + "_" + i, featureValue.charAt(0) - '0');
				}
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
