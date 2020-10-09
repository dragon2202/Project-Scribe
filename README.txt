TELAT
A graphical user interface for interacting with, loading from and writing to excel test data sheets.

Property of the Naval Undersea Warfare Center, Newport Division.

Installation
1. Copy and paste entire folder off of the CD on to the computer.
2. Go to: scribe>Project Scribe>dist
3. Run: Project Scribe.jar

Usage
1. Load excel test file:
File>load>dist>SORT.xls
2. Select variant from variant dropdown menu.
3. Enter data in to test steps.
4. Save

Modes
* View Mode:
    * Allows the user to export data to .csv
    * Printing of excel sheet.
* Edit Mode:
    * Allows the user to edit the test steps.
    * The password is stored in password.txt
* Test Mode:
    * Default mode that allows the user to enter data in to the tests.

 Metrics and Other Data Entry
* CCLog
* Shift Entry
* Counts
* Executive Summary

Dependencies
* Apache POI
* JavaFX
* JavaFX Scene Builder

Known Issues
* Currently the user may have to reload the 
test file multiple times when switching to other modes.
* When attempting to run from a disk, the .jar will not function.
  To fix this, copy the ENTIRE contents of the disk on to your computer
  and then follow the instructions to the run the application.
  This issue may be caused by implicitely relative paths to excel files.
* Printing is not fully functional.
* On Screen Keyboard does not function in CCLog edit/view/create event window.

Updates:
* On Screen Keyboard functionality/menu option.
* Keyboard shortcuts added to all windows.
* A rudimentary help menu.