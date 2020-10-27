#include "caff.h"

std::istream& operator>>(std::istream& is, Block& block) {
    is.read(&block.id, sizeof(block.id));
    is.read((char*)&block.length, sizeof(block.length));
    block.data.resize(block.length);
    is.read(&block.data[0], block.length);
    return is;
}

std::ostream& operator<<(std::ostream& os, Block const& block) {
    os << "id: " << std::hex << (int)block.id << std::endl;
    os << "length: " << std::dec << block.length << std::endl;
    os << "data: " << std::hex;
    /*for (char c : block.data) {
        os << (int)c;
    }*/
    os << std::dec << std::endl;
    return os;
}