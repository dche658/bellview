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

