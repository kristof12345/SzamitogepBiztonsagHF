#pragma once
#include<vector>
#include<iostream>

class CAFF
{
public:
	int a;
	int b;
};

struct Block {
	char id;                     // 1 byte
	long long length;            // 8 bytes
	std::vector<char> data;
};

std::istream& operator>>(std::istream& is, Block& block);
std::ostream& operator<<(std::ostream& os, Block const& block);