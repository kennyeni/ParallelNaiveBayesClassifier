#include "Reader.h"
#include "Globals.h"

Reader::Reader(string path, void * container)
{
	Buffer_Queue * queue = static_cast<Buffer_Queue*>(container);
	ifstream file(path);
	string line;
	while (!file.eof())
	{
		getline(file, line);
		queue->push_back(line);
	}
}

