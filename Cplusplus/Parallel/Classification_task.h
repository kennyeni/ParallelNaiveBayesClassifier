#include "Globals.h"

class Classification
{
	int id;
	string label; // this classifciation belongs to label
	double featureMultiplication = 1; // Multiplication of this features
	int featureCount = 0; // How many features we counted
	int labelCount; // How many times this label repeats in the set
	bool finalized = false;
	double res;

public:
	double end();
	static int trainingSetCount;
	Classification(int, string, int);
	void addFeature(int);
	string toString();
};

typedef std::unordered_map<string, Classification> Classifications;

#pragma once
class Classification_task
{
	vector<int> attrs;
	string originalLine;
	bool initialized;
	Classifications classes;
	

	void initialize();
	void split(const string&, const char);

public:
	int id;
	Classification_task(string &, int);
	void classificate();
	string toString();
};

