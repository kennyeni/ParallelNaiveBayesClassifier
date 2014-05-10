#include "Globals.h"

string categories[MAX_CATEGORIES] = { "CEU", "GIH", "JPT", "ASW", "YRI" };

Buffer_Queue * ioQueue = new Buffer_Queue;
Buffer_Queue * classificationQueue = new Buffer_Queue;
Training_MapHashMap * trainingMap = new Training_MapHashMap;
Model_ReduceHashMap * modelDictionary = new Model_ReduceHashMap;