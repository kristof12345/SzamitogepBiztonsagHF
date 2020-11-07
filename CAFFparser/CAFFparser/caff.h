#pragma once

#ifndef CAFF_H
#define CAFF_H

#include<vector>
#include<iostream>

#include "binary_helper.h"
#include "ciff.h"

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

	struct Animation
	{
		long long int duration;
		CIFF ciff;
	};

	class CAFF
	{
	protected:
		bool valid;
		CAFFheader header;
		CAFFcredits credits;
		vector<Animation> animations;

	public:
		CAFF();

		const bool GetValid() const;
		const CAFFheader& GetHeader() const;
		const CAFFcredits& GetCredits() const;
		const vector<Animation>& GetAnimations() const;

		bool Read(byte* data, size_t length);
		void ExportToJson(string file);

	protected:
		bool ReadBlocks(byte* data, size_t length, size_t& cursor);
		bool ReadBlock(byte* data, size_t length, size_t& cursor, byte ID);
		bool ReadHeader(byte* data, size_t length, size_t& cursor);
		bool ReadCredits(byte* data, size_t length, size_t& cursor);
		bool ReadAnimation(byte* data, size_t length, size_t& cursor);
	};
}

#endif