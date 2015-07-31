Damerell D, Ceroni A, Maass K, Ranzinger R, Dell A, Haslam SM. The GlycanBuilder and GlycoWorkbench glycoinformatics tools: updates and new developments. Biol Chem. 2012 Nov;393(11):1357-62. doi: 10.1515/hsz-2012-0135.

A.Ceroni, K. Maass, H.Geyer, R.Geyer, A.Dell and S.M.Haslam: GlycoWorkbench: A Tool for the Computer-Assisted Annotation of Mass Spectra of Glycans Journal of Proteome Research, 7 (4), 1650--1659, 2008 ( PMID: 18311910)

GlycoWorkbench is a suite of software tools designed for the rapid drawing of glycan structures and for assisting the process of structure determination from mass spectrometry data. The graphical interface of GlycoWorkbench provides an environment in which structure models can be rapidly assembled, their mass computed, their fragments automatically matched with MSn data and the results compared to assess the best candidate. GlycoWorkbench can greatly reduce the time needed for the interpretation and annotation of mass spectra of glycans.

First we must thank both past and present members of Professor Anne Dell's Biopolymer mass spectrometry group within which the majority of the development of this tool has taken place. We must also give a big thanks to the Glycoconjugate biochemistry group at JLUG and especially to Tobias Lehr, which have provided continuous testing and feedback to GlycoWorkbench and made it possible to reach its current level of functionality.

# Manual #

The Media Wiki Manual is currently unavailable whilst we prepare to move to new servers.

Please use the [PDF Manual](https://code.google.com/p/glycoworkbench/downloads/detail?name=Manual%20Version%202.pdf&can=2&q=#makechanges) until it is back online

# Installation Instructions #
  * Make sure that your system has version 6 or latter of Java installed

  * Download the correct package for your system

  * To manually launch GlycoWorkbench run "java -jar -Xmx300M eurocarb-glycoworkbench-1.0rc.jar"

  * More detailed installation instructions can be found  in the manual
## Windows ##
Windows Installer (recommended)

  * [GlycoWorkbenchWin\_x86.exe (recommended)](https://code.google.com/p/glycoworkbench/downloads/detail?name=GlycoWorkbenchWin_x86.exe)
  * [GlycoWorkbenchWin\_x86-64.exe](https://code.google.com/p/glycoworkbench/downloads/detail?name=GlycoWorkbenchWin_x86-64.exe)
  * [GlycoWorkbenchWin\_x86.zip (recommended)](https://code.google.com/p/glycoworkbench/downloads/detail?name=GlycoWorkbenchWin_x86.zip)
  * [GlycoWorkbenchWin\_x86-64.zip](https://code.google.com/p/glycoworkbench/downloads/detail?name=GlycoWorkbenchWin_x86-64.zip)

You only require the 64bit version of GlycoWorkbench if your default JRE is a 64bit (x86-64) one.

If you have installed GlycoWorkbench using the provided installation package - you'll find a corresponding entry within your start menu to launch GlycoWorkbench.

If the installer doesn't work for you - download and extract the above ZIP archive to your directory of choice; GlycoWorkbench can be launched with the enclosed executable. Please refer to the included README file, if you are having problems launching GlycoWorkbench.

News: 12/07/2010, some users are still reporting issues launching GlycoWorkbench; it appears to be an issue with some combinations of Java and Windows that are installed. Please email  info@glycoworkbench.org with the following details

Java version(s) (please list all) - found within Add/Remove programs (or uninstall program on newer versions of Windows)

Windows Version (if your not sure run the builtin program "winver") - including if it's 64bit or not.

## Linux ##
  * [GlycoWorkbenchLin\_x86-64 (recommended)](https://code.google.com/p/glycoworkbench/downloads/detail?name=GlycoWorkbenchLin_x86-64.zip)
  * [GlycoWorkbenchLin\_x86](https://code.google.com/p/glycoworkbench/downloads/detail?name=GlycoWorkbenchLin_x86.zip)

Download and extract the correct ZIP archive into your directory of choice - run the program by clicking on the provided jar.

## Mac OS X ##
  * [GlycoWorkbenchMac\_Cocoa\_x86-64.zip (recommended](https://code.google.com/p/glycoworkbench/downloads/detail?name=GlycoWorkbenchMac_Cocoa_x86-64.zip)
  * [GlycoWorkbenchMac\_Cocoa\_x86.zip](https://code.google.com/p/glycoworkbench/downloads/detail?name=GlycoWorkbenchMac_Cocoa_32.zip)
  * [GlycoWorkbenchMac\_Carbon\_x86.zip](https://code.google.com/p/glycoworkbench/downloads/detail?name=GlycoWorkbenchMac_Carbon_32.zip)

The Mac OS X version of GlycoWorkbench doesn't get as much testing as the Linux and Windows builds. The last version that was known to work can be found  here - that's not to say that the latest version doesn't work. For more information on Mac OS X support please check the  Mac OS X section of the manual

Run GlycoWorkbench by clicking on the GlycoWorkbench.jar file (you might need to click open afterwards).

# Building GlycoWorkbench from Source #
#1 Checkout the UCDB project

hg clone https://code.google.com/p/ucdb/source/checkout

(Alternatively use a tool like TortoiseHG http://tortoisehg.bitbucket.org/)

Note that if you are checking out on Windows be aware that some of the file paths are very close to the Windows API maximum length of 266 characters.  Therefore it's recommended that you checkout to a directory such as C:\UCDB.  Most of the developers in the past have worked on Unix based systems where this didn't cause any problems.

#2 Download Apache Ant or install from System Repository

http://ant.apache.org/bindownload.cgi

Ant needs the environment variable "ANT\_HOME" set to the installation/extracted directory and the directory $ANT\_HOME\bin added to the system PATH environment variable.  You probably already have Ant installed so I won't go into any further details

#3 Dependency Resolution

Within the UCDB project root directory you will notice that there's an IVY script to dynamically pull in dependencies from external repositories.  Although some of the dependencies are located in the pre-fetch directory.  Our IVY import script frequently breaks so it's simpler to just follow the instructions below.

Therefore I wouldn't even both trying to pull the dependencies with Ant.

Instead download the following archive and it extract it into $UCDB\_PROJECT/external-libs

https://code.google.com/p/ucdb/downloads/detail?name=UCDB_DEPS.zip&can=2&q=#makechanges

#5 Make sure that environment variables for Java are correctly configured.

Check that the bin directory of your JDK is appended to the PATH environment variable and that JAVA\_HOME is correctly configured.

You can't build UCDB projects with OpenJDK you must be using a JDK from Oracle to be sure it will build correctly.

#4 Next, start-up a terminal/console/powershell

cd to the UCDB directory and type

ant setup

Ignore the error messages they don't matter

#5 Building GlycoWorkbench

cd application/GlycoWorkbench

ant build

# Change Log #
```
    2.1 Stable (Build 157) 09/02/13 Unreleased
    
    Another build for users to try on Mountain Lion
    
    2.1 Stable (Build 150) 16/01/13 Unreleased
    
    Contains a proposed fix for the startup issue caused by a recent Java update on Mac OS X
    
    2.1 Stable (Build 146) 21/06/2012 (following are usability fixes for Mac OS X users)
    
    Command key press now triggers all key bindings bound to Ctrl (i.e. Ctrl+A and Cmd+A both work).
    Backspace key press automatically triggers delete key event
     
    2.1 Stable (Build 139) 09/05/2012
    
    Fixed blocking behavior of settings dialog box on various flavors of Windows.
    
    2.1 Stable (Build 137) 04/05/2012

    GlycoWorkbench appears to have always read the attribute "is_msms" as "is_msm" which meant that if a user manually changed the MS level it would not be reflected in the interface if the user saved and then re-opened the session.
    Fixed a NullPointerException being thrown when a session is opened which contains imported spectra and then the user attempts to reassign the spectra.

    2.1 Stable (Build 134) 03/05/2012

    Fixed an issue that could prevent the report dialog box from being shown.

    2.1 Stable (Build 132) 19/04/2012

    Improved peak parsing routine to allow for three column CSV formatted files where the third column can be relative intensity or charge state. 

    2.1 Stable (Build 131) 11/04/2012

    Before computing peak centroids we now store the current config, destroy the current instance, and reload the config (to get around the same issue we see on GlycoWorkbench first run) 

    2.1 Stable (Build 128) 04/04/2012

    Fixed bug which stopped the cascade annotation routine from finding matching peaks within an accuracy window
    Changed structure pull threshold from 0.0001 PPM to 2 Da
    Fixed multiple issues resulting in a scans MS level being incorrectly set
    Fixed issues relating to peak picking and cascading annotations
    Ribbon is now minimized by default to free up some vertical space
    Quick access buttons have been added
    Fixed multiple issues with FileChooser dialog boxes not defaulting to the correct FileFilter
    Fixed issues with the linkage and structure tool bars not wrapping correctly (better support for small screens)
    Split spectrum toolbar in two (better support for small screens)
    Wiki help page is manipulated by GlycoWorkbench to remove left and top tool bar areas

    2.1 Stable (Build 110) 31/10/2011

    Updated Windows executables so that they are compatible with JRE 7
    Minor updates to the handling of WGGDS resources
    Delayed start-up of Profiler plugin for faster startup
    Updated manual (in the previous version the new URL was not deployed correctly)
    Slight change to the work flow of a cascade annotation 

    2.1 Stable (Build 105) 09/09/2011

    Added isotope specification support for all molecules
    Added heavy permethylation as a derivatization
    Added amine substituent
    De-selecting "Show reducing end" now works for all reducing ends
    Added ability to copy in silco generated fragment m/z values to peak list (for testing purposes)
    Annotation system can now try (if asked) all possible ion combinations (i.e. glycan structure is 2Na, annotator will try all 2Na and 1Na fragments)
    Added ability to customize core types gallery
    Added ability to customize terminal types gallery
    Added ability to add custom Residues
    Added ability to create custom substituents
    Added ability to create custom reducing ends
    Added ability to create custom modifications
    Added ability to store definition files (Residues, etc.) on a remote server
    Added ability to customize all symbolic notations (including adding new residues)
    Added ability to customize Residue fragmentation
    Redirected help system to new version 2.1 manual (although not finished quite yet)
    Added ability to customize builtin structure dictionaries
    Completed support for WGGDS web service databases

    2.0 Stable (Build 96) 19/08/2011

    Changes to profiler database search options
    Removed sequences from dictionaries with nested repeats
    Structures on the glycan canvas with repeating units can now be copied and pasted
    m/z is calculated for structures with repeating units of specified size (i.e. min and max are equal)
    Support for WGGDS substructure queries (disabled for now)

    2.0 Stable (Build 93) 03/08/2011

    Fixed bug which caused GWB to throw an exception on startup when the remembered ion count for H2PO4 >0

    2.0 Stable (Build 91) 28/07/2011

    Added support for Cl- and H2PO4- 

    2.0 Stable (Build 90) 27/07/2011

    Added ability to add all peaks from a peak list to a report
    Added checkbox to clear existing annotations before carrying out an annotation run
    Updated DJNativeSwing and SWT libraries to latest version - (required to allow the GWB help system to work with newer web browsers)
    Added missing avalon library - which was preventing PDF export

    2.0 Alpha 2 (Build 83) 20/12/2010

    Fixed broken Glyco-Peakfinder plugin
    Fixed potential issues with the update link
    Fixed possible exception being thrown on startup

    2.0 Alpha 2 (Build 66) 22/10/2010

    Individual panels can now be detached
    Notification when a new version has been released
    Simplification of application menu
    Fixed broken linkage bar
    Restored the majority of keyboard short cuts
    Improved open button behaviour
    Updated icons and other LAF issues

    2.0 Alpha 2 (Build 48) 07/09/2010

    Permanent display of linkage and recent residue bar.
    Option to not save loaded spectra in GWP file.
    Clicking the save button in application bar, saves the GWS by default.
    LAF updates: Default theme now Office Blue, themes shown by unqualified name, icon loading change for faster startup.
    Changed location of Manual to wiki.glycoworkbench.org
    Improved error handling.
    Fixed missing AppData? directory on none English versions of Windows.

    2.0 Alpha 2 (Build 42) 26/07/2010

    Fixed peak editor delete bug
    Altered glycan contextual ribbon activation behaviour
    UI speed ups

    2.0 Alpha 2 (Build 35) 13/07/2010

    Restored about menu.
    Fixed Mac OS X specific issues.

    2.0 Alpha 2 (Build 25) 12/07/2010

    Added missing JNA library (Unix users affected)
    Changed resize policy of a RibbonTask? for compatibility with Mac OS X and Linux users.

    2.0 Alpha 2 (Build 15) 07/07/2010

    S3 support (suitability for use within GWB is being prototyped, might still be removed)
    Automatic building of all packages and subsequent publication. 
    Refactored the residue contextual ribbon tasks [id: 2]
    Three packages are now created for Mac OS X (Carbon 32bit, Cocoa 32bit and Cocoa 64bit). [id:11]
    Both 32 and 64bit packages are generated for Linux.
    Switched Windows launcher to WinRun4J
    New Startup script for Mac OS X (possibly to be replaced by a complete DMG) 
    Restored peak list paste support [id:6]
    Fixed broken Buker peak list import [id:7]
    Fixed exception being thrown when empty peak is deleted [id:12]
    Fixed two UI jitter bugs [id: 16 and 17]
    Tooltip support refactored via RichTooltip [id: 3]
    Accelerator key support refactored via RichTooltip [id: 4]
    Fixed some dialog boxes being too small [id: 8]
    Converted all remaining GIF icons to PNGs [id:15]
    Fixed high CPU load when displaying the fragment editor [id: 18]
    Icons are now shown for terminals with unknown linkage [id: 5]
    Fixed insert residue gallery being shown within the structure ribbon rather than the add residue gallery. (Prevented structures from being drawn from scratch). [id: 19] 

    Version 2.0 alpha 09/06/2010 (missing 64bit Windows Zip)

    Cascade annotation of MSn data sets
    Migration to Office style ribbon interface
    Additional icons
    Ribbon support for plugins
    New splash screen and logo
    Implementation of Mediawiki help system.
    Help system, detection of internet connection.
    Bug fixes for peak list editor (introduced in version 1.3)
    Fixed broken jar click support (non-functional in version 1.3) 

    Version 1.3 21/05/2010

    Moved user configuration into home directory
    Initial integration of SWT browser (for future EurocarbDB integration)
    Updated help system. (will be heavily revised in next revision)
    Minor bug fixes.
    Splash screen for Linux and Mac OS X users
    Added charge field to Peak list panel

    Version 1.1 Build 3480 - 22/09/2008

    GAG profiling tool
    in-silico fragmentation of sulfated glycans
    graphic display of the peak list, similar to the spectra panel
    residue properties toolbar
    copy tables in spreadsheets with structures as compositions
    search databases by sub-structure
    close dialogs by pressing Escape
    select OK in dialogs by pressing Enter
    added executable, icons and splashscreen for Windows users
    fixed export to SVG 
```
