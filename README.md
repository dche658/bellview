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

If a Java Runtime Environment (JRE) or Java Development Kit (JDK) is not 
available on your system you can install from one of the installer packages 
(deb file on Linux or exe on Windows). These include a JRE image and
should run out of the box once installation is complete.

If you do have a JRE or JDK installed (Version 17 or later is required) 
you have the option of running from the binary distribution files
(tar or zip archives). Suitable JREs or JDKs are available from 
[https://adoptium.net/](https://adoptium.net/) or 
[https://www.azul.com/](https://www.azul.com/)

Extract the zip or tar archive and execute the startup script for your
operating system

Windows

	bin/bellview.bat
	
Linux

	bin/bellview

Mac owners will need to build the application from the source files which
are available at
[https://github.com/dche658/bellview](https://github.com/dche658/bellview).
Windows and Linux users have the option of doing the same if they wish.

At the GitHub repository, if you click on the green "<> Code" button you 
should see a link to "Download ZIP". Download and then extract the
bellview-master.zip archive.

Open a terminal window and change to the bellview-master directory.

Run the command 

    gradlew assemble
    
Hopefully, this will download all the necessary dependencies and install the zip 
distribution archive in the /bellview-master/app/build/distributions
directory. This is best done when you are not behind a corporate firewall.


### Change log

**Version 2.0.0 beta 4: 1 June 2023**

- Refactor to use iText 8 instead of iText 5

**Version 2.0.0 beta 3: 18 April 2023**

- add window icon
- fixes to dialog titles
- bump to javafx 20

**Version 2.0.0 beta 2: 11 April 2023**

- Fix update of analyte when importing data

**Version 2.0.0 beta 1: 2 April 2023**

- Change user interface to javafx 17 LTS
- Removed support for gamma distribution
- Add ability to import and filter by gender and age
- Support log transformation of the form log(x+C)
- Remove requirement for Apache Derby and just store data in RAM

**Version 1.2.6: 16 Dec 2021**

- Bump log4j2 dependency to 2.16.0. 
- Bump Apache Derby dependency to 10.14.2.0 which is still compatible with java 8. 
- Use default look and feel instead of forcing to Nimbus.

**Version 1.2.5: 4 Feb 2021**

- Minor refactoring to use Apache POI 4.1.2. 
- Fix Excel importer to read sample data that includes non-numeric formulas. 

**Version 1.2.4: 16 Jan 2021**

- Change to BSD license.

**Version 1.2:**

- No additional features but internals updated to replace deprecated
- APIs and run under java 1.8 to 12. This version will only run on java 1.8 or above.

**Version 1.1**

- Added ability to import data from excel (.xlsx) spreadsheets using Apache POI.

**Version 1.0: Original release**

- Some time ago. I cannot remember when.

