#include "Classification_task.h"

int Classification::trainingSetCount = 0;

Classification::Classification(int id, string label, int labelCount){
	this->id = id;
	this->label = label;
	this->labelCount = labelCount;
}

void Classification::addFeature(int valFeatureModel){
	featureCount++;
	featureMultiplication *= valFeatureModel + .007;
}

double Classification::end(){
	if (finalized)
		return res;
	if (featureCount < 2)
		return 0;
	unsigned long long featurePower = labelCount;
	featurePower = pow(featurePower, featureCount - 1);

	double resL = featureMultiplication / (featurePower * (trainingSetCount));
	if (!_finite(resL)){
		if (resL > 0) 
			return numeric_limits<double>::max();
		return numeric_limits<double>::max() * -1;
	}

	res = resL;
	finalized = true;
	return resL;
}

string Classification::toString(){
	string probs("Tuple #" + to_string(id) + " prob of being " + label + " is " + to_string(end()) + "\n");
	return probs;
}

///////////////////////////////////////////////////////////////


Classification_task::Classification_task(string& line, int i)
{
	originalLine.assign(line);
	id = i;
	attrs = vector<int>();
	classes = Classifications();
	initialized = false;
}

void Classification_task::initialize()
{
	int i = id;
	if (!initialized){
		split(originalLine, '\t');
		for (int i = 0; i < MAX_CATEGORIES; i++){
			Model_ReduceHashMap::const_accessor a;
			string label(categories[i]);
			modelDictionary->find(a, label + "_class");
			int valLabelOverall = a->second;
			Classification tmp(id, label, valLabelOverall);
			pair<string, Classification> p(label, tmp);
			classes.insert(p);
			Classification::trainingSetCount += valLabelOverall;
		}
	}
}

void Classification_task::classificate()
{
	initialize();
	for (int i = 0; i < 50; i++){
		int featureValue = attrs[i];
		if (featureValue == 1){
			for (int i = 0; i < MAX_CATEGORIES; i++){
				string label(categories[i]);
				Classifications::iterator got = classes.find(label);
				Classification classTmp = got->second;

				Model_ReduceHashMap::const_accessor a;
				string find = label + "_" + to_string(i);
				if (!modelDictionary->find(a, find)){ // This feature was unknown always, skip it
					classTmp.addFeature(0);
				}
				else{
					int valFeatureModel = a->second; // Count of this feature in this label
					classTmp.addFeature(valFeatureModel);
				}
				got->second = classTmp;

			}
		}
	}
}

string Classification_task::toString(){
	string str;
	string winner;
	double winnerVal = -1 * numeric_limits<double>::max();
	for (Classifications::iterator it = classes.begin();
		it != classes.end();
		it++){
		str.append(it->second.toString());
		if (it->second.end() > winnerVal){
			winner = it->first;
			winnerVal = it->second.end();
		}
	}
	string fin = "So, the biggest is: " + winner + " : " + to_string(winnerVal) + "\n";
	return str.append(fin);
}



// Created by: http://stackoverflow.com/users/7980/alec-thomas
// Adapted by: http://github.com/kennyeni
void Classification_task::split(const string &text, const char sep){
	//int start = 0, end = 0;
	char tmp;
	int i = 0;
	int offset = 0;
	const char * line = text.c_str();
	while (sscanf(line + offset, "%c%n", &tmp, &i) == 1){
		if (tmp == '1' || tmp == '0' || tmp == '?'){
			attrs.push_back(tmp - '0');
		}
		offset += i;
	}
}