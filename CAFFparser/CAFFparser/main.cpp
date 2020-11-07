#include <iostream>
#include <fstream>
#include <iterator>
#include <vector>

#include "error_handler.h"
#include "caff.h"

using namespace std;
using namespace CAFFparser;

int main(int argc, char* argv[])
{
	cout << "CAFFparser" << endl;
	if (argc < 3)
	{
		ErrorHandler::Handle("Input .caff file must be the first parameter. output directory must be the second.");
		return -1;
	}

	cout << "currently running in: " << argv[0] << endl;
	string caff_file = argv[1];
	cout << ".caff file: " << caff_file << endl;
	string outputpath = string(argv[2]) + "\\";
	cout << "output directory: " << outputpath << endl;

	ifstream input(caff_file, ios::binary);
	vector<unsigned char> buffer(istreambuf_iterator<char>(input), {});
	input.close();

	CAFF caff;
	try {
		caff.Read(buffer.data(), buffer.size());
	}
	catch (const char* c)
	{
		ErrorHandler::Handle(c);
		return -1;
	}

	caff.ExportToJson(outputpath + "output.json");
	for(size_t i = 0; i < caff.GetAnimations().size(); ++i)
	{
		caff.GetAnimations()[i].ciff.ExportToBMP(outputpath + to_string(i+1) + "_img.bmp" );
	}

	return 0;
}