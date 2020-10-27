#include "error_handler.h"
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

	bool CAFF::Read(byte* data, size_t length)
	{
		valid = false;
		if (data == nullptr || length == 0)
			return false;

		size_t cursor = 0;

		if (!ReadHeader(data, length, cursor))
			return false;

		if (!ReadBlocks(data, length, cursor))
			return false;

		valid = true;
		return true;
	}

	bool CAFF::ReadHeader(byte* data, size_t length, size_t& cursor)
	{
		byte ID = ReadBinary<byte>(data, length, cursor);
		if (ID != 0x1)
		{
			ErrorHandler::Handle("Header block mandatory 0x1 validated");
			return false;
		}

		long long int blocklength = ReadBinary<long long int>(data, length, cursor);
		if (blocklength != sizeof(byte[4]) + sizeof(long long int) + sizeof(long long int))
		{
			ErrorHandler::Handle("Header block length violated");
			return false;
		}

		//header.magic[0] = ReadBinary<byte>(data, length, cursor);
		//header.magic[1] = ReadBinary<byte>(data, length, cursor);
		//header.magic[2] = ReadBinary<byte>(data, length, cursor);
		//header.magic[3] = ReadBinary<byte>(data, length, cursor);
		ReadBinaryToBuffer<byte>(data, length, cursor, header.magic, sizeof(header.magic));

		header.header_size = ReadBinary<long long int>(data, length, cursor);
		header.num_anim = ReadBinary<long long int>(data, length, cursor);

		return true;
	}

	bool CAFF::ReadBlocks(byte* data, size_t length, size_t& cursor)
	{
		while (cursor < length)
		{
			if (!ReadBlock(data, length, cursor))
				return false;
		}

		return true;
	}

	bool CAFF::ReadBlock(byte* data, size_t length, size_t& cursor)
	{
		byte ID = ReadBinary<byte>(data, length, cursor);
		if (ID == 0x1)
		{
			ErrorHandler::Handle("Multiple header blocks found");
			return false;
		}

		long long int blocklength = ReadBinary<long long int>(data, length, cursor);
		if (blocklength <= 0)
		{
			ErrorHandler::Handle("Block length invalid");
			return false;
		}

		if (ID == 0x2)
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

	bool CAFF::ReadCredits(byte* data, size_t length, size_t& cursor)
	{
		credits.Y = ReadBinary<short int>(data, length, cursor);
		credits.M = ReadBinary<byte>(data, length, cursor);
		credits.D = ReadBinary<byte>(data, length, cursor);
		credits.h = ReadBinary<byte>(data, length, cursor);
		credits.m = ReadBinary<byte>(data, length, cursor);
		credits.creator_len = ReadBinary<long long int>(data, length, cursor);

		if(credits.creator_len < 0)
		{
			ErrorHandler::Handle("Credits block has invalid creator_len");
			return false;
		}
		else if (credits.creator_len > 0)
		{
			char* buffercreator = new char[credits.creator_len + 1];
			try {
				ReadBinaryToBuffer<char>(data, length, cursor, buffercreator, credits.creator_len);
			}
			catch (...)
			{
				delete[] buffercreator;
				throw;
			}

			buffercreator[credits.creator_len] = '\0';
			credits.creator = string(buffercreator);

			delete[] buffercreator;
		}
		return true;
	}

	bool CAFF::ReadAnimation(byte* data, size_t length, size_t& cursor)
	{
		// TODO
		return true;
	}
}