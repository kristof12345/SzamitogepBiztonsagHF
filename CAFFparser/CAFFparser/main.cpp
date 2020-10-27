#include "caff.h"
#include <iostream>
#include <fstream>

int main()
{
    std::ifstream myfile("3.caff", std::ios::in | std::ios::binary);
    
    if (myfile.is_open())
    {
        Block block;
        while (myfile >> block) {
            std::cout << block << std::endl;
        }
        myfile.close();
    }

    else std::cout << "Unable to open file";

    return 0;
}