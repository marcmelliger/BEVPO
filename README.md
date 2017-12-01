# BEVPO 
## Or Battery Electric Vehicle Potential Model.

The purpose of this JAVA model is to assess the potential of BEV (battery electric vehicle) trips. To do that, BEVPO simulated trips by BEVs based on national travel survey such as the Mikrozensus Mobilität in Switzerland.   

The travel surveys I used for my study are protected from copyright and can not be published. However, this document explains how to prepare the data from such a travel survey.





# Installation

## Create your own fork of BEVPO
If you don't have a GitHub Account yet, creat one.

First create a fork of my BEVPO repository. For this, go to https://github.com/MMWeb87/BEVPO/ and click "Fork". Now a new Fork of the BEVPO model should be available in your account. Remember the path of your fork for the next step.

## Install BEVPO locally in Eclipse

I recommend to use an IDE such as Eclipse to run the BEVPO model. To set up BEVPO in eclipse, follow these steps in Eclipse:

1. Select Window > Show View > Other.... In the dialogfield click Git > Git Repositories
2. In the now visible perspective click the button "Clone a Git Repository"
3. In the URI field enter the path to your fork (e.g. https://github.com/MMWeb87/BEVPO/ in my case) and click "Next >"
4. Select the master branch if not already selected and click "Next >"
5. Leave the Directory field on the default value unless you want to change your the local git repository pat and remember that path

The files are now on your local drive but not yet imported as project in Eclipse. To do that:

6. Select File > Import...
7. Select Maven > Existing Maven Projects
8. Click on "Browse" and select the project in the git folder created in step 5
9. Make sure that pom.xml is selected and click OK

Now the project named dataAnalysis should be visible in the Package Explorer Perspective. If no errors appear, you are ready to set up the Configuration and prepare the data. If errors appear, read the next section:

## Troubleshooting

It can happen that there are errors in the project (visible from the red X next to the dataAnalysis project. This issue can occur due to corrupted jar files in the maven dependencies.  On Mac OS do the following

1. Locate the error in the source file (Follow the X)
2. If the error is related to the import command like "com.google.common.base.Charsets" (if not all imports are visible click the plus besides the first import on top), you have to manually remove the affected files.
3. In the case of "com.google.common.base.Charsets", the files belongs to guava-21.jar. This can be found out by opening the Folder "Maven Dependencies" in the Package Exporer Perspective. There you see all dependencies. In eclipse, the folder names are shown besided the .jar files. So if the problem was related to "import com.google.common.base.Charsets" there should be a guava-21.0.jar with the path "/Users/[yourname]/.m2/repository/com/google/guava/guava/21.0".
4. Open the terminal and enter "cd /Users/[yourname]/.m2/repository/com/google/guava/guava/". Check that you are in the correct folder with the command "ls": you shoud see a folder called 21.0.
5. Remove this folder with the command: "rm -R guava"
6. In Eclipse: Right click the project dataAnalysis in the package expolorer and select Maven > Update Project. Check "Force Update of Snapshots/Releases" and click OK. 

The issue should now be fixed and the errors disappear. 

# Configuration

For a successful BEVPO model run, you need to 

- create the input folders with the correctly formated data files
- create empty output folders
- Adjust the java run files
- create property files that hold the configuration of the scenario and policy packages.
- make a new Run configuration.


## Creating and Setting the main paths
All relevant paths of the model are stored in src.main.java.runmodel.RunAnalysis.java.

Assuming your project data is located in */Users/mmweb/bevpo/*, you need to manually create the following folders and adjust the paths in RunAnalysis.java


|Name |Variable Name|Path   |Description|
|---|---|---|---|
|Travel Surveys               |basePathQueries|/Data/Queries/| contains the travel survey in the csv format. Data structure below|
|Input                        |baseInputPath|/Data/Input/| Input folder for all data but the travel survey|
|Charging Station Coordinates |basePathChargermap|  /Data/Input/chargermap   |Coordinates and name of charging stations  |
|Car models                   |basePathCar| /Data/Input/cars/csv/|Specifications of the car models|
|Charing Station Types        |basePathChargers|/Input/chargers/csv/|Specifications of available chargin station types|
|Output                       |basePathOutput|/Output/|Output folder for all the model calculations|
|Temp Folder                  |basePathSer|/Serialised/|Temp folder for all Google Maps Queries|






# Configuring the configuration
The properties files represent store the configurations, i.e. scenarios and policy packages


