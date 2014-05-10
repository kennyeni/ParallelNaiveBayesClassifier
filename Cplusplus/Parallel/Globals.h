#include <iostream>
#include <tbb\task_scheduler_init.h>
#include <tbb\concurrent_hash_map.h>
#include <tbb\concurrent_vector.h>
#include <tbb\parallel_do.h>
#include <fstream>
#include <vector>
#include <sstream>
#include <unordered_map>
#include <math.h>
#include <time.h>
#define MAX_CATEGORIES 5

using namespace std;
using namespace tbb;

extern string categories[MAX_CATEGORIES];

typedef concurrent_vector<string> Buffer_Queue;
typedef concurrent_hash_map<string, vector<int>> Training_MapHashMap;
typedef concurrent_hash_map<string, int> Model_ReduceHashMap;


extern Buffer_Queue * ioQueue;
extern Training_MapHashMap * trainingMap;
extern Model_ReduceHashMap * modelDictionary;
extern Buffer_Queue * classificationQueue;

