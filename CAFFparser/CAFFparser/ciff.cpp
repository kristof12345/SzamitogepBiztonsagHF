#include "error_handler.h"
#include "ciff.h"

namespace CAFFparser
{
	CIFF::CIFF()
		: valid(false)
		, header()
	{

	}

	const bool CIFF::GetValid() const
	{
		return valid;
	}

	const CIFFheader& CIFF::GetHeader() const
	{
		return header;
	}

	bool CIFF::Read(byte* data, size_t length)
	{
		valid = false;
		if (data == nullptr || length == 0)
			return false;

		size_t cursor = 0;

		if (!ReadHeader(data, length, cursor))
			return false;

		if (!ReadPixels(data, length, cursor))
			return false;

		if (cursor != length)
		{
			ErrorHandler::Handle("CIFF is not as long as specified");
			return false;
		}

		valid = true;
		return true;
	}

	void CIFF::ExportToBMP(string file) const
	{
		int w = header.width;
		int h = header.height;

		FILE* f;
		unsigned char* img = NULL;
		int filesize = 54 + 3 * w * h;  //w is your image width, h is image height, both int

		img = (unsigned char*)malloc(3 * w * h);
		memset(img, 0, 3 * w * h);

		for (int i = 0; i < w; i++)
		{
			for (int j = 0; j < h; j++)
			{
				int x = i;
				int y = (h - 1) - j;
				img[(x + y * w) * 3 + 2] = (unsigned char)(pixels[i][j].r);
				img[(x + y * w) * 3 + 1] = (unsigned char)(pixels[i][j].g);
				img[(x + y * w) * 3 + 0] = (unsigned char)(pixels[i][j].b);
			}
		}

		unsigned char bmpfileheader[14] = { 'B','M', 0,0,0,0, 0,0, 0,0, 54,0,0,0 };
		unsigned char bmpinfoheader[40] = { 40,0,0,0, 0,0,0,0, 0,0,0,0, 1,0, 24,0 };
		unsigned char bmppad[3] = { 0,0,0 };

		bmpfileheader[2] = (unsigned char)(filesize);
		bmpfileheader[3] = (unsigned char)(filesize >> 8);
		bmpfileheader[4] = (unsigned char)(filesize >> 16);
		bmpfileheader[5] = (unsigned char)(filesize >> 24);

		bmpinfoheader[4] = (unsigned char)(w);
		bmpinfoheader[5] = (unsigned char)(w >> 8);
		bmpinfoheader[6] = (unsigned char)(w >> 16);
		bmpinfoheader[7] = (unsigned char)(w >> 24);
		bmpinfoheader[8] = (unsigned char)(h);
		bmpinfoheader[9] = (unsigned char)(h >> 8);
		bmpinfoheader[10] = (unsigned char)(h >> 16);
		bmpinfoheader[11] = (unsigned char)(h >> 24);

		fopen_s(&f, file.c_str(), "wb");
		if (f != 0)
		{
			fwrite(bmpfileheader, 1, 14, f);
			fwrite(bmpinfoheader, 1, 40, f);
			for (int i = 0; i < h; i++)
			{
				fwrite(img + (w * (h - i - 1) * 3), 3, w, f);
				fwrite(bmppad, 1, (4 - (w * 3) % 4) % 4, f);
			}

			fclose(f);
		}

		free(img);
	}

	bool CIFF::ReadHeader(byte* data, size_t length, size_t& cursor)
	{
		ReadBinaryToBuffer<byte>(data, length, cursor, header.magic, sizeof(header.magic));

		header.header_size = ReadBinary<long long int>(data, length, cursor);
		if (header.header_size < 0)
		{
			ErrorHandler::Handle("CIFF header size invalid");
			return false;
		}

		header.content_size = ReadBinary<long long int>(data, length, cursor);
		if (header.header_size < 0)
		{
			ErrorHandler::Handle("CIFF content size invalid");
			return false;
		}

		header.width = ReadBinary<long long int>(data, length, cursor);
		header.height = ReadBinary<long long int>(data, length, cursor);

		if (cursor < header.header_size)
		{
			char c = ReadBinary<char>(data, (size_t)header.header_size, cursor);
			while (c != '\n')
			{
				header.caption += c;
				c = ReadBinary<char>(data, (size_t)header.header_size, cursor);
			}
		}

		while (cursor < header.header_size)
		{
			string tag;
			char c = ReadBinary<char>(data, (size_t)header.header_size, cursor);
			while (c != '\0')
			{
				tag += c;
				c = ReadBinary<char>(data, (size_t)header.header_size, cursor);
			}
			header.tags.push_back(tag);
		}

		return true;
	}

	bool CIFF::ReadPixels(byte* data, size_t length, size_t& cursor)
	{
		if(header.width * header.height * 3 != header.content_size)
		{
			ErrorHandler::Handle("CIFF content size is not equal to width * height * 3");
			return false;
		}

		pixels.resize((size_t)header.width); //for performance increase
		for (size_t x = 0; x < header.width; ++x)
		{
			pixels[x].resize((size_t)header.height);
			for (size_t y = 0; y < (size_t)header.height; ++y)
			{
				Pixel& pixel = pixels[x][y];

				pixel.r = ReadBinary<byte>(data, length, cursor);
				pixel.g = ReadBinary<byte>(data, length, cursor);
				pixel.b = ReadBinary<byte>(data, length, cursor);
			}
		}

		return true;
	}
}