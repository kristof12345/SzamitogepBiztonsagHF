# Build instructions

To build CAFFparser you can use **Visual Studio 2019** or use the **Makefile**.

To use the Makefile on Windows, follow these steps:

1. Install Mingw-w64 via the SourceForge website. [Click Mingw-w64](https://sourceforge.net/projects/mingw-w64/files/Toolchains%20targetting%20Win32/Personal%20Builds/mingw-builds/installer/mingw-w64-install.exe/download) to download the Windows Mingw-w64 installer.
    - Run the installer.
    - For **Architecture select x86_64** and then select Next.
    - Next again to use the default installation folder and install MinGW.
    - Add the bin folder of the MinGW installation to PATH environment variables
1. Run build_with_make.bat

# Run instructions
1. Start the parser with CAFFparser.exe and paremeters
    - First parameter is the caff file's full path
    - Second parameter is the output directory (make sure, that this directorx already exists)
    - Example: `CAFFparserBin.exe c:\BME\SzamitogepBiztonsag\test\1.caff c:\BME\SzamitogepBiztonsag\test\output\`
1. The metadata of the CAFF file is found in the `output.json` file in the output directory.
1. The images contained in the CAFF file are found in the output directory with names: `##_img.bmp` where `##` is the number of the ciff (example: `1_img.bmp`).

# Testing instructions
The solution was tested on Linux with Valgrind and AFL. See the results in the [wiki](https://github.com/kristof12345/SzamitogepBiztonsagHF/wiki/Testing-Native-component-using-Valgrind-and-AFL)
## Valgrind
1. Install Valgrind with terminal command `sudo apt-get install valgrind`
1. Run program with Valgrind with terminal command: `valgrind ./CAFFparserBin 1.caff out`
## AFL
1. Install AFL
`git clone https://github.com/mirrorer/afl`
Change the value of `MAX_FILE` in `config.h` to `(8 * 1024 * 1024)`
```
cd afl
make && sudo make install
```
2. Run AFL 
Create inputs directory  in the solution directory and copy some sample caff files into the inputs directory.
Run command `./run_afl.sh` from the solution directory
