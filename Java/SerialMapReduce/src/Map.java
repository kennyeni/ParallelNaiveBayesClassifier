import java.util.concurrent.atomic.AtomicInteger;

public class Map implements Runnable {
	
	private static Integer count = 0;
	private int id;

	public Map(){
		this.id = Map.count++;
	}
	
	@Override
	public void run() {
		for(;;){
			if (Globals.bufferQueue.isEmpty()){
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
				
			} catch (InterruptedException e) {
				if (Globals.bufferQueue.isEmpty())
					System.out.println("Map thread #" + this.id + " was killed normally");
				else
					e.printStackTrace();
			}
		}
		
		
	}

}
