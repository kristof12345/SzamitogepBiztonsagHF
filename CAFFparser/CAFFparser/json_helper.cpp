#include "json_helper.h"

namespace CAFFparser
{
	string WriteWithTabs(int tab, string line)
	{
		string tabs;
		for (int i = 0; i < tab; ++i)
			tabs += '\t';

		return tabs + line + "\n";
	}
}