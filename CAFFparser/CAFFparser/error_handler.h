#pragma once

#ifndef ERROR_HANDLER_H
#define ERROR_HANDLER_H

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

#endif