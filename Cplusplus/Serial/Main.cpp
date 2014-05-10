#include "Globals.h"
#include "Reader.h"
#include "Classification_task.h"

class TrainingMap{

public:
	TrainingMap(){};

	void insert(string str, int i) const {
		vector<int> res = (*trainingMap)[str];
		res.push_back(i);
		(*trainingMap)[str] = res;
	}

	void addFeature(string class_, string substr, int n) const
	{
		
		if (substr[0] == '1' || substr[0] == '0')
		{
			string tmp = class_ + "_" + to_string(n);
			insert(tmp, substr[0] - '0');
		}
	}

	// Created by: http://stackoverflow.com/users/7980/alec-thomas
	// Adapted by: http://github.com/kennyeni
	void split(const string &text, const char sep) const {
		int start = 0, end = 0;
		string class_;
		string buffer;
		string tmp;
		int i = 0;
		while ((end = text.find(sep, start)) != string::npos) {
			string substr = text.substr(start, end - start);
			if (substr[0] >= 'A')
			{
				tmp.assign(substr + "_class");
				insert(tmp, 1);
				class_.assign(substr);
			}
			else
			{
				addFeature(class_, substr, i);
				i++;
			}
			start = end + 1;
		}
		string substr = text.substr(start);
		addFeature(class_, substr, i);
		i++;
	}

	void apply(string item) const {
		split(item, '\t'); // Insert each token to trainingMap
	}
};

class TrainingReduce
{
public:
	int reduce(vector<int> v) const {
		int val = 0;
		for (vector<int>::iterator it = v.begin();
			it != v.end();
			it++)
		{
			val += *it;
		}
		return val;
	}

	void apply(pair<const string, vector<int>> item) const {
		int val = reduce(item.second); // agregate values
		pair<string, int> p(item.first, val);
		modelDictionary->insert(p); // insert to model
	}
};

class ClassificationMap
{
public:
	void apply(string item) const {
		Classification_task task(item, 1);
		task.classificate();
		string str = task.toString();
	}
};




void createModel(){
	string file("");
	Reader read(file, ioQueue); // read each line to ioQueue
	for (Buffer_Queue::iterator it = ioQueue->begin();
		it != ioQueue->end();
		it++){
		string str = *it;
		TrainingMap a;
		a.apply(str);// Apply TrainingMap to each instance
	}

	for (Training_MapHashMap::iterator it = trainingMap->begin();
		it != trainingMap->end();
		it++){
		pair<string, vector<int>> p = *it;
		TrainingReduce a;
		a.apply(p);// Apply TrainingMap to each instance
	}
}

void applyModel()
{
	string file("");
	Reader read(file, classificationQueue);
	for (Buffer_Queue::iterator it = classificationQueue->begin(); it != classificationQueue->end(); it++){
		string str = *it;
		ClassificationMap a;
		a.apply(str); // Apply TrainingMap to each individual
	}

}

int main() {
	
	time_t start, end;
	time(&start);

	createModel(); // get counts
	applyModel(); // use model

	time(&end);
	double dif = difftime(end, start);
	printf("Elasped time is %.2lf seconds.", dif);
	trainingMap->clear();
	delete trainingMap;
	//delete trainingMap;
	//delete modelDictionary;
	return 0;
}
