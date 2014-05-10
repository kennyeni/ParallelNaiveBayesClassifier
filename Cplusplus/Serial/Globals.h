#include <iostream>
#include <fstream>
#include <vector>
#include <sstream>
#include <unordered_map>
#include <math.h>
#include <time.h>
#define MAX_CATEGORIES 5

using namespace std;

extern string categories[MAX_CATEGORIES];

typedef vector<string> Buffer_Queue;
typedef unordered_map<string, vector<int>> Training_MapHashMap;
typedef unordered_map<string, int> Model_ReduceHashMap;


extern Buffer_Queue * ioQueue;
extern Training_MapHashMap * trainingMap;
extern Model_ReduceHashMap * modelDictionary;
extern Buffer_Queue * classificationQueue;

