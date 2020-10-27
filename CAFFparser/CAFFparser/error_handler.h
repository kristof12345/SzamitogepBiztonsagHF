#pragma once

#include <string>
#include <iostream>

using namespace std;

namespace CAFFparser
{
	class ErrorHandler
	{
	public:
		static void Handle(string error) { cout << "ERROR: " << error << endl; }
	};
}