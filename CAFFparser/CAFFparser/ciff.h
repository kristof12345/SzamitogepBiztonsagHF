#pragma once
#include<vector>
#include<iostream>

#include "binary_helper.h"
#include <vector>

using namespace std;

namespace CAFFparser
{
	struct CIFFheader
	{
		byte magic[4];
		long long int header_size;
		long long int content_size;
		long long int width;
		long long int height;
		string caption;
		vector<string> tags;
	};

	struct Pixel
	{
		byte r;
		byte g;
		byte b;
	};

	class CIFF
	{
	protected:
		bool valid;
		CIFFheader header;
		vector<vector<Pixel>> pixels;

	public:
		CIFF();

		const bool GetValid() const;
		const CIFFheader& GetHeader() const;

		bool Read(byte* data, size_t length);
		void ExportToBMP(string file) const;

	protected:
		bool ReadHeader(byte* data, size_t length, size_t& cursor);
		bool ReadPixels(byte* data, size_t length, size_t& cursor);
	};
}