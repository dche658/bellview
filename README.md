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

## Checksums

**In Windows you can use the following command to calculate the checksum.**

CertUtil -hashfile InFile SHA256

1fa636e55276072db917db74cd02286bc9ccc69d45e9921a698acbe056a30098  bellview-2.0.0-win.exe

0ea99bce4c6a2a66b58eadcb9789c2642d93ede7b7bca795670dc5fe9c2e75d9  bellview-2.0.0-win.zip

**In Linux you can use the following command to calculate the check sum**

shasum -a 256 InFile

feb47b11ba8a084c357aa995330384377ea5bb1189d8003708fc9f6951a029b2  bellview_2.0.0-linux-amd64.deb

186ecea5f742a6e274f747b12b875c585b4de13934d9684864dd5c3f85f79887  bellview-2.0.0-linux.tar

4b3373e4181395cbc78d17eeab9340852c67219da19ce4a84e3e2be0b430f963  bellview-2.0.0-linux.zip


c04a65567234850e862fa21ff5c5a3cc220cb71a712bd961aa99f7274db18d25 Bellview-1.2.6-bin.zip
