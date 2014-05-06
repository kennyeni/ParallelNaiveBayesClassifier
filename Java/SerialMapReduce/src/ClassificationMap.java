import java.util.concurrent.atomic.AtomicInteger;

public class ClassificationMap{
	
	private static Integer count = 0; 
	private int id;
	private int countOfOperations;

	public ClassificationMap(){
		this.id = ClassificationMap.count++;
	}
	
	public void run() {
		for(;;){
			if (Globals.classificationQueue.isEmpty()){
				return;
			}
			try {
				ClassificationTask task = Globals.classificationQueue.take();
				task.classificate();
				System.out.println(task.toString());
				
			} catch (InterruptedException e) {
				if (Globals.bufferQueue.isEmpty())
					System.out.println("Map thread #" + this.id + " was killed normally");
				else
					e.printStackTrace();
			}
		}
		
		
	}

}
