# Build instrictions

To build CAFFparser you can use **Visual Studio 2019** or use the **Makefile**.

To use the Makefile on Windows, follow these steps:

1. Install Mingw-w64 via the SourceForge website. Click Mingw-w64 to download the Windows Mingw-w64 installer. (https://sourceforge.net/projects/mingw-w64/files/Toolchains%20targetting%20Win32/Personal%20Builds/mingw-builds/installer/mingw-w64-install.exe/download)
    - Run the installer.
    - For **Architecture select x86_64** and then select Next.
    - Next again to use the default installation folder and install MinGW.
    - Add the bin folder of the MinGW installation to PATH environment variables
1. Run build_with_make.bat

# Run instrictions
1. Start the parser with CAFFparser.exe and paremeters
    - First parameter is the caff file's full path
    - Second parameter is the output directory (make sure, that this directorx already exists)
    - Example: "CAFFparser.exe c:\BME\SzamitogepBiztonsag\test\1.caff c:\BME\SzamitogepBiztonsag\test\output\"
1. The metadata of the CAFF file is found in the "output.json" file in the output directory.
1. The images contained in the CAFF file are found in the output directory with names: ##_img.bmp where ## is the number of the ciff (example: "1_img.bmp").
