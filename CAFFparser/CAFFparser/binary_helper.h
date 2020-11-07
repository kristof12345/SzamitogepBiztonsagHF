#pragma once

#ifndef BINARY_HELPER_H
#define BINARY_HELPER_H

namespace CAFFparser
{
	using byte = unsigned char;

	template<typename T>
	T ReadBinary(byte* data, size_t length, size_t& cursor)
	{
		if (cursor + sizeof(T) > length)
			throw "ReadBinary out of range";

		T value = *(T*)(data + cursor);
		cursor += sizeof(T);
		return value;
	}

	template<typename T>
	void ReadBinaryToBuffer(byte* data, size_t length, size_t& cursor, T* buffer, size_t buffer_size)
	{
		if (cursor + sizeof(T) * buffer_size > length)
			throw "ReadBinaryToBuffer out of range";

		for (size_t i = 0; i < buffer_size; ++i)
		{
			buffer[i] = *(T*)(data + cursor);
			cursor += sizeof(T);
		}
	}
}

#endif