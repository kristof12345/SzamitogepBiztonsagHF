#include <fstream>
#include "error_handler.h"
#include "json_helper.h"
#include "caff.h"

namespace CAFFparser
{
	CAFF::CAFF()
		: valid(false)
		, header()
	{

	}

	const bool CAFF::GetValid() const
	{
		return valid;
	}

	const CAFFheader& CAFF::GetHeader() const
	{
		return header;
	}

	const CAFFcredits& CAFF::GetCredits() const
	{
		return credits;
	}

	const vector<Animation>& CAFF::GetAnimations() const
	{
		return animations;
	}

	bool CAFF::Read(byte* data, size_t length)
	{
		valid = false;
		if (data == nullptr || length == 0)
			return false;

		size_t cursor = 0;

		if (!ReadBlocks(data, length, cursor))
			return false;

		if (cursor != length)
		{
			ErrorHandler::Handle("CAFF is not as long as specified");
			return false;
		}

		if (header.num_anim != animations.size())
		{
			ErrorHandler::Handle("CAFF header num_anim is not equal to the number of animation blocks");
			return false;
		}

		valid = true;
		return true;
	}

	bool CAFF::ReadBlocks(byte* data, size_t length, size_t& cursor)
	{
		bool firstblock = true;
		while (cursor < length)
		{
			byte ID = ReadBinary<byte>(data, length, cursor);
			if (firstblock && ID != 0x1)
			{
				ErrorHandler::Handle("Header block mandatory 0x1 validated");
				return false;
			}

			long long int signed_blocklength = ReadBinary<long long int>(data, length, cursor);
			if (signed_blocklength <= 0 || (size_t)signed_blocklength > length)
			{
				ErrorHandler::Handle("Block length invalid");
				return false;
			}

			size_t blocklength = (size_t)signed_blocklength;
			size_t blockcursor = 0;
			if (!ReadBlock(data + cursor, blocklength, blockcursor, ID))
				return false;

			if (blocklength != blockcursor)
			{
				ErrorHandler::Handle("Block length specified is not equal to actual block length");
				return false;
			}

			cursor += blocklength;
			firstblock = false;
		}

		return true;
	}

	bool CAFF::ReadBlock(byte* data, size_t length, size_t& cursor, byte ID)
	{
		if (ID == 0x1)
		{
			return ReadHeader(data, length, cursor);
		}
		else if (ID == 0x2)
		{
			return ReadCredits(data, length, cursor);
		}
		else if (ID == 0x3)
		{
			return ReadAnimation(data, length, cursor);
		}
		else
		{
			ErrorHandler::Handle("Unknown block type");
			return false;
		}
	}

	bool CAFF::ReadHeader(byte* data, size_t length, size_t& cursor)
	{
		ReadBinaryToBuffer<byte>(data, length, cursor, header.magic, sizeof(header.magic));

		header.header_size = ReadBinary<long long int>(data, length, cursor);
		header.num_anim = ReadBinary<long long int>(data, length, cursor);

		return true;
	}

	bool CAFF::ReadCredits(byte* data, size_t length, size_t& cursor)
	{
		credits.Y = ReadBinary<short int>(data, length, cursor);
		credits.M = ReadBinary<byte>(data, length, cursor);
		credits.D = ReadBinary<byte>(data, length, cursor);
		credits.h = ReadBinary<byte>(data, length, cursor);
		credits.m = ReadBinary<byte>(data, length, cursor);
		credits.creator_len = ReadBinary<long long int>(data, length, cursor);

		if (credits.creator_len < 0)
		{
			ErrorHandler::Handle("Credits block has invalid creator_len");
			return false;
		}
		else if (credits.creator_len > 0)
		{
			char* buffercreator = new char[(size_t)credits.creator_len + 1];
			try {
				ReadBinaryToBuffer<char>(data, length, cursor, buffercreator, (size_t)credits.creator_len);
			}
			catch (...)
			{
				delete[] buffercreator;
				throw;
			}

			buffercreator[(size_t)credits.creator_len] = '\0';
			credits.creator = string(buffercreator);

			delete[] buffercreator;
		}
		return true;
	}

	bool CAFF::ReadAnimation(byte* data, size_t length, size_t& cursor)
	{
		Animation animation;
		animation.duration = ReadBinary<long long int>(data, length, cursor);

		size_t remaining_length = length - sizeof(animation.duration);
		bool success = animation.ciff.Read(data + cursor, remaining_length);
		cursor += remaining_length;

		if (success)
		{
			animations.push_back(animation);
			return true;
		}
		else
		{
			return false;
		}
	}

	void CAFF::ExportToJson(string file)
	{
		int tab = 0;
		string json;
		json += WriteWithTabs(tab++, "{");

			json += WriteWithTabs(tab++, "\"Header\": {");
				json += WriteWithTabs(tab, "\"header_size\": " + to_string(header.header_size) + ",");
				json += WriteWithTabs(tab, "\"num_anim\": " + to_string(header.header_size));
			json += WriteWithTabs(--tab, "},");

			json += WriteWithTabs(tab++, "\"Credits\": {");
				json += WriteWithTabs(tab, "\"year\": " + to_string(credits.Y) + ",");
				json += WriteWithTabs(tab, "\"month\": " + to_string(credits.M) + ",");
				json += WriteWithTabs(tab, "\"day\": " + to_string(credits.D) + ",");
				json += WriteWithTabs(tab, "\"hour\": " + to_string(credits.h) + ",");
				json += WriteWithTabs(tab, "\"minute\": " + to_string(credits.m) + ",");
				json += WriteWithTabs(tab, "\"creator_len\": " + to_string(credits.creator_len) + ",");
				json += WriteWithTabs(tab, "\"creator\": \"" + credits.creator + "\"");
			json += WriteWithTabs(--tab, "},");

			json += WriteWithTabs(tab++, "\"Animations\": [");
			for (size_t i = 0; i < animations.size(); ++i)
			{
				Animation& animation = animations[i];
				json += WriteWithTabs(tab++, "{");
					json += WriteWithTabs(tab, "\"duration\": " + to_string(animation.duration) + ",");
					json += animation.ciff.GetJson(tab);
				json += WriteWithTabs(--tab, i == animations.size() - 1 ? "}" : "}," );

			}

			json += WriteWithTabs(--tab, "]");

		json += WriteWithTabs(--tab, "}");

		ofstream myfile;
		myfile.open(file);
		if (myfile) {
			myfile << json;
			myfile.close();
		}
		else {
			ErrorHandler::Handle(string("Could not create json file: ") + file);
		}
	}

}