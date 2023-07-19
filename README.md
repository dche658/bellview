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

Application installer packages are available for Windows and Linux 
(bellview-x.x.x-win.exe and bellview-x.x.x-linux_amd64.deb respectively)

These packages include a JRE image and should run out of the box once 
installation is complete.

The zip binary Bellview-x.x.x-bin.zip can be run on any platform but requires
a Java Runtime Environment (JRE) or Java Development Kit JDK (Version 17
or later) and JavaFX installed. Startup scripts can be found in the bin directory
but you will need to set the JAVA_HOME environment variable to the location of
your JDK or JRE first. 

Suitable JREs or JDKs are available from
[https://adoptium.net/](https://adoptium.net/) or 
[https://www.azul.com/](https://www.azul.com/)

JavaFX SDKs are available from 
[https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/) 
alternatively, Azul has JRE or JDK packages that include JavaFX (JRE FX, and JDK FX).
At the time of writing these could be downloaded from 
[https://www.azul.com/downloads/?package=jdk#zulu](https://www.azul.com/downloads/?package=jdk#zulu) 
but make sure you scroll down the page a bit and select the correct package 
and version (17 or later)

### Windows

Search for the option to edit environment variables for your account.
Alternatively, from the Control Panel -> System and Security -> System 
-> Advanced system settings -> Environment Variables

Create a new entry with variable name JAVA_HOME, and variable value the
path to your JDK or JRE (typically something like 
\Program Files\Java\zulu17.40.19-ca-fx-jre17.0.6-win_x64)

Alternatively JAVA_HOME can be set directly in the startup script. 
See line 20 of app.bat, remove the @rem statement, and update the path as 
necessary.

The application can then be started with:

	bin/app.bat
	

### Linux or Mac

The JAVA_HOME environment variable can be configured in the usual way for
your shell such as .bashrc or .zshrc

Alternatively JAVA_HOME can be set directly startup script. See line 70 of
bin/app, uncomment the line and update the path as needed. On Debian based 
systems this will be typically JAVA_HOME="/usr/lib/jvm/zulu-fx-17-amd64"

Ensure permissions have been updated so that the startup script can be 
executed, then start with:

	bin/app
	

## Source

The source files are available from
[https://github.com/dche658/bellview](https://github.com/dche658/bellview).

At the GitHub repository, if you click on the green "<> Code" button you 
should see a link to "Download ZIP". Download and then extract the
bellview-master.zip archive.

Open a terminal window and change to the bellview-master directory.

To build a platform specific installer, run the command 

    gradlew jpackage
    
Hopefully, this will download all the necessary dependencies and create the
installer package in /bellview/app/build/jpackage

Alternatively to build a runtime image execute

		gradlew runtime

### Change log

**Version 2.0.1: 18 July 2023**

- Change to non-modular application to fix problem with exporting PDF

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
