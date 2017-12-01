# BEVPO 
## Or Battery Electric Vehicle Potential Model.

The purpose of this JAVA model is to assess the potential of BEV (battery electric vehicle) trips. To do that, BEVPO simulated trips by BEVs based on national travel survey such as the Mikrozensus Mobilität in Switzerland.   

The travel surveys I used for my study are protected from copyright and can not be published. However, this document explains how to prepare the data from such a travel survey.


# Installation

I recommend to use an IDE such as Eclipse to run the BEVPO model. To set up BEVPO in eclipse, follow these steps in Eclipse:

1. Select Window > Show View > Other.... In the dialogfield click Git > Git Repositories
2. In the now visible perspective click the button "Clone a Git Repository"
3. In the URI field enter: https://github.com/MMWeb87/BEVPO/ and click "Next >"
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
- create property files that hold 



