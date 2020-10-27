#include <iostream>
#include <fstream>
#include <iterator>
#include <vector>

#include "caff.h"
#include "error_handler.h"

using namespace std;
using namespace CAFFparser;

int main()
{
	cout << "CAFFparser" << endl;

	ifstream input("c:\\BME\\SzamitogepBiztonsag\\test\\1.caff", ios::binary);
	vector<unsigned char> buffer(istreambuf_iterator<char>(input), {});
	input.close();

	CAFF caff;
	try {
		caff.Read(buffer.data(), buffer.size());
	}
	catch (const char* c)
	{
		ErrorHandler::Handle(c);
	}

	return 0;
}