import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.ietf.jgss.Oid;

class Classification{
	static int trainingSetCount;
	int id;
	String label; // this classifciation belongs to label
	BigDecimal featureMultiplication = BigDecimal.ONE; // Multiplication of this features
	int featureCount = 0; // How many features we counted
	final int labelCount; // How many times this label repeats in the set
	boolean finalized = false;
	double res;
	
	public Classification(int id, String label, int labelCount) {
		this.id = id;
		this.label = label;
		this.labelCount = labelCount;
	}

	public void addFeature(int valFeatureModel) {
		featureCount++;
		featureMultiplication = featureMultiplication.multiply(new BigDecimal((valFeatureModel + .007) + ""));
		
	}
	
	public double end(){
		if (finalized)
			return res;
		if (featureCount < 2)
			return 0;
		BigDecimal featurePower = new BigDecimal(labelCount + "");
		featurePower.pow(featureCount - 1);
		double resL =  featureMultiplication.divide(featurePower.multiply(new BigDecimal(trainingSetCount + "")), MathContext.DECIMAL128).doubleValue();
		res = resL;
		finalized = true;
		return resL;
	}
	
	@Override
	public String toString() {
		String probs = "Tuple #" + id + " prob of being " + label + " is " + end() + "\n";
		return probs;
	}
	
}

// READ-ONLY class that represent a map task
public class ClassificationTask{
	int id = 0;
	String[] attrs = null;
	String originalLine = null;
	boolean initialized = false;
	HashMap<String, Classification> classification = new HashMap<String, Classification>();
	
	public ClassificationTask(String line, int id) {
		this.id = id;
		this.originalLine = line;
	}

	public void initializeMe(){
		if (!initialized){
			attrs = originalLine.split("\t");
			initialized = true;
			for(String label : Globals.categories){
				int valLabelOverall = Globals.modelDict.get(label + "_class").intValue(); // Count of this label overall
				classification.put(label, new Classification(id, label, valLabelOverall));
				Classification.trainingSetCount += valLabelOverall;
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		String winner = "";
		double winnerVal = Double.NEGATIVE_INFINITY;
		for (String label : classification.keySet()){
			str.append(classification.get(label).toString());
			if (classification.get(label).end() > winnerVal){
				winner = label;
				winnerVal = classification.get(label).end();
			}
		}
		String fin = "So, the biggest is: " + winner + " : " + winnerVal + "\n";
		return str.toString() + fin;
	}
	
	public String[] getAttrs() {
		initializeMe();
		return attrs;
	}

	public void classificate() {
		initializeMe();
		String[] attr = this.getAttrs();
		features: for(int i = 0; i < 600; i++){
			String featureValue = attr[i];
			if (featureValue.charAt(0) == '1'){
				for(String label : Globals.categories){
					Classification classTmp = classification.get(label);
					
					AtomicInteger valFeatureModel = Globals.modelDict.get(label + "_" + i); // Count of this feature in this label
					if (valFeatureModel == null) // This feature was unknown always, skip it
						classTmp.addFeature(0);
					else
						classTmp.addFeature(valFeatureModel.intValue());
					
				}
			}
		}
	}

}
