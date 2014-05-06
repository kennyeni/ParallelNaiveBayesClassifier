import java.util.Arrays;

import org.ietf.jgss.Oid;

// READ-ONLY class that represent a map task
public class TrainingTask extends ClassificationTask{
	private String label;
	
	public TrainingTask(String line, int id) {
		super(line, id);
	}

	public void initialize(){
		if (!initialized){
			char[] tmp = new char[3];
			for (int i = 0; i < 3; i++) {
				tmp[i] = originalLine.charAt(i); // create category
			}
			label = new String(tmp);
			attrs = originalLine.substring(4).split("\t");
			initialized = true;
		}
	}

	public String getLabel() {
		initialize();
		return label;
	}

	public String[] getAttrs() {
		initialize();
		return attrs;
	}

}
