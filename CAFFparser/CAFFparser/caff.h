#pragma once
#include<vector>
#include<iostream>

#include "binary_helper.h"
#include <vector>

using namespace std;

namespace CAFFparser
{
	struct CAFFheader
	{
		byte magic[4];
		long long int header_size;
		long long int num_anim;
	};

	struct CAFFcredits
	{
		short int Y;
		byte M;
		byte D;
		byte h;
		byte m;
		long long int creator_len;
		string creator;
	};

	class CAFF
	{
	protected:
		bool valid;
		CAFFheader header;
		CAFFcredits credits;

	public:
		CAFF();

		const bool GetValid() const;
		const CAFFheader& GetHeader() const;

		bool Read(byte* data, size_t length);

	protected:
		bool ReadHeader(byte* data, size_t length, size_t& cursor);
		bool ReadBlocks(byte* data, size_t length, size_t& cursor);
		bool ReadBlock(byte* data, size_t length, size_t& cursor);
		bool ReadCredits(byte* data, size_t length, size_t& cursor);
		bool ReadAnimation(byte* data, size_t length, size_t& cursor);
	};
}