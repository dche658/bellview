# Bellview

This application has been designed as a tool to simplify the process for 
performing Bhattacharya analysis on large data sets for the indirect
determination of reference intervals.

Version 2 has been substantially rewritten primarily to gain familiarity 
javafx for the graphical user interface. However, the ability to import
age and gender has been added so that partitioning can be done within
the application rather than forcing the user to do this prior to importing
data.

## Installation

If a Java Runtime Environment is not available on your system you can install from the packages that include a JRE. If you do have a JRE you have the option of running from the binary distribution files. If using the latter, the archive should be extracted before executing the bin/Bellview (linux) or bin/Bellview.bat (windows) script. 

- bellview_2.0.0-linux_amd64.deb Application image that include a Java Runtime Environment
- Bellview-2.0.0-linux.tar Binary distribution for Linux
- Bellview-2.0.0-linux.zip Binary distriubtion for Linux

The source code is available from
[https://github.com/dche658/bellview](https://github.com/dche658/bellview)


## Change log

### Version 2.0.0 beta 2: 11 April 2023

- Fix update of analyte when importing data

### Version 2.0.0 beta 1: 2 April 2023

- Change user interface to javafx 17 LTS
- Removed support for gamma distribution
- Add ability to import and filter by gender and age
- Support log transformation of the form log(x+C)
- Remove requirement for Apache Derby and just store data in RAM

### Version 1.2.6: 16 Dec 2021

- Bump log4j2 dependency to 2.16.0. 
- Bump Apache Derby dependency to 10.14.2.0 which is still compatible with java 8. 
- Use default look and feel instead of forcing to Nimbus.

### Version 1.2.5: 4 Feb 2021

- Minor refactoring to use Apache POI 4.1.2. 
- Fix Excel importer to read sample data that includes non-numeric formulas. 

### Version 1.2.4: 16 Jan 2021

- Change to BSD license.

### Version 1.2: 

- No additional features but internals updated to replace deprecated
- APIs and run under java 1.8 to 12. This version will only run on java 1.8 or above.

### Version 1.1

- Added ability to import data from excel (.xlsx) spreadsheets using Apache POI.

### Version 1.0: Original release

- Some time ago. I cannot remember when.

## SHA 256 checksum

### Windows

CertUtil -hashfile InFile SHA256

1fa636e55276072db917db74cd02286bc9ccc69d45e9921a698acbe056a30098  bellview-2.0.0.exe

0ea99bce4c6a2a66b58eadcb9789c2642d93ede7b7bca795670dc5fe9c2e75d9  bellview-2.0.0.zip

8f00dbd9644cf26382e65a3a9b5ae25349afda27789e78de1c6105cae1a6078f  bellview_2.0.0-linux_amd64.deb

74ec9352c9d4e806e688bc6b0a0d7ad5194a94bde5776e7cb455095cae769195  Bellview-2.0.0-linux.tar

0eb1dedbbe6548a1e811564152e69cab3737a3bc61b24cd0fbf042482b86214e  Bellview-2.0.0-linux.zip


c04a65567234850e862fa21ff5c5a3cc220cb71a712bd961aa99f7274db18d25 Bellview-1.2.6-bin.zip
