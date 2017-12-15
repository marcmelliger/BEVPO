# BEVPO 
## Or Battery Electric Vehicle Potential Model.

The purpose of this JAVA model is to assess the potential of BEV (battery electric vehicle) trips. To do that, BEVPO simulated trips by BEVs based on national travel survey such as the Mikrozensus Mobilität in Switzerland.   

The travel surveys I used for my study are protected by copyright and can not be published. However, this document explains how to prepare the data from such a travel survey.





# Installation

## Create your own fork of BEVPO
If you don't have a GitHub Account yet, create one.

First, create a fork of my BEVPO repository. For this, go to https://github.com/MMWeb87/BEVPO/ and click "Fork". Now a new Fork of the BEVPO model should be available in your account. Remember the path of your fork for the next step.

## Install BEVPO locally in Eclipse

I recommend using an IDE such as Eclipse to run the BEVPO model. To set up BEVPO in eclipse, follow these steps in Eclipse:

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

Now the project named BEVPO should be visible in the Package Explorer Perspective. If no errors appear, you are ready to set up the Configuration and prepare the data. If errors appear, read the next section:

## Troubleshooting

It can happen that there are errors in the project (visible from the red X next to the BEVPO project. This issue can occur due to corrupted jar files in the maven dependencies.  On Mac OS do the following

1. Locate the error in the source file (Follow the X)
2. If the error is related to the import command like "com.google.common.base.Charsets" (if not all imports are visible click the plus besides the first import on top), you have to manually remove the affected files.
3. In the case of "com.google.common.base.Charsets", the files belongs to guava-21.jar. This can be found out by opening the Folder "Maven Dependencies" in the Package Exporer Perspective. There you see all dependencies. In eclipse, the folder names are shown besided the .jar files. So if the problem was related to "import com.google.common.base.Charsets" there should be a guava-21.0.jar with the path "/Users/[yourname]/.m2/repository/com/google/guava/guava/21.0".
4. Open the terminal and enter "cd /Users/[yourname]/.m2/repository/com/google/guava/guava/". Check that you are in the correct folder with the command "ls": you shoud see a folder called 21.0.
5. Remove this folder with the command: "rm -R guava"
6. In Eclipse: Right click the project BEVPO in the package expolorer and select Maven > Update Project. Check "Force Update of Snapshots/Releases" and click OK. 

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

Assuming your project data is located in */Users/user/bevpo/data/*, you need to manually create the following folders and adjust the paths in RunAnalysis.java


|Name |Variable Name|Path   |Description|
|---|---|---|---|
|Travel Surveys               |basePathQueries|/queries/| contains the travel survey in the csv format. Data structure below|
|Input                        |baseInputPath|/input/| Input folder for all data but the travel survey|
|Charging Station Coordinates |basePathChargermap|  /input/chargermap/   |Coordinates and name of charging stations  |
|Car models                   |basePathCar| /input/cars/|Specifications of the car models|
|Charing Station Types        |basePathChargers|/input/chargers/|Specifications of available chargin station types|
|Output                       |basePathOutput|/output/|Output folder for all the model calculations|
|Temp Folder                  |basePathSer|/serialised/|Temp folder for all Google Maps Queries|

Next you have to create specifc folders for the countries you want to simulate. For instance, if you want to make simulations based on Swiss travel surveys, open the File RunAnalysisSwiss.java. If you want to create an other country, you should rename this file: right click on RunAnalysisSwiss and Refactor > Rename..., rename the file to RunAnalysis[Country] and ignore the warnings.

Adjust the following variables in RunAnalysis[Country].java

```
countryNameLong = "Switzerland"; // Fullname
countryNameShort = "CH"; // Countrycode big letter
countryToplevel = "ch"; // toplevel domain
```


### Travel surveys
The travel survey data file contains all the travel diaries. It is based on single legs to ceratain activities (compare model concept report).

The path to the travel survey data file needs to be set in RunAnalysis[Country].java
```
LEG_DATA_FILENAME = "CH/travel_survey.csv";
```

Each record of the data file is an individual legs that has been covered by one car. The file need to have the following structure (the names of the fields can be adjusted in RunAnalysis[Country].java)

|Field|Description|
|---|---|
|householdID|An ID that is unique to individual households. Ther can be many legs with different cars with the same householdID|
|personID|In multi person households, the personID identifies the persons|
|legID|The legID identifies the individual legs. The ID starts at 1 for every car and person in a household| 
|distance|The distance of the leg in km|
|legStartTime|The start time of the leg in minutes after midnight|
|legEndTime|The end time of the leg in minutes after midnight|
|legStartAddress|The postalcode of the leg's start location|
|legEndAddress|The postalcode of the leg's destination|
|legStartAddressAlt|The municaplity of the leg's start location|
|legEndAddressAlt|The municaplity of the leg's destination|
|legStartAddressManual|Other type of address if Google Map Routing fails|
|legEndAddressManual|Other type of address if Google Map Routing fails|
|legPurpose|A purpose number for the trip, e.g. 2 for Work. Corresponds to the definition in RunAnalysis[Country]|
|leisurePurpose|A separate purpose number for leisture trips. Corresponds to the definition in RunAnalysis[Country]|
|wayPurpose|The purpose numbers of these ways  (Some travel surveys summarise individual legs to ways). It is used to set the last purpose of a trip in the simulation.|
|driverType|Type of the driver. Used to differentiate between drivers and co-passengers. Defined in RunAnalysis[Country].java in legCoPassenger|
|carType|Type of the car. E.g . HomeCar. Defined in RunAnalysis[Country].java in carTypeStrings.|
|carID|An ID that is unique to individual cars in a household|
|homeParking|Variable used to define if parking spaces are available. <=0 is no parking space, >0 is with parking space|

The following table shows some example entries:

|householdID|personID|legID|distance|legStartTime|legEndTime|legStartAddress|legEndAddress|legStartAddressAlt|legEndAddressAlt|legStartAddressManual|legEndAddressManual|legPurpose|leisurePurpose|wayPurpose|driverType|carType|carID|homeParking|
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
|6|1|1|33	|700	|1000	|1800	|1050	|Municp1	|Municp2		|||8	|1  |8|		8|	1|	1|	0|
|6|2|1|12	|700	|1000	|1800	|1050	|Municp1	|Municp2		|||8	|1  |8|		8|	1|	1|	0|
|6|1|2|33.1	|1400	|1600	|1050	|1800	|Municp2	|Municp1		|||11	 |-99|8|	8|	1|	1|	0|
|6|2|2|12.2	|1400	|1600	|1050	|1800	|Municp2	|Municp1		|||11	  |-99|8|	8|	1|	1|	0|
|7|1|1|0.5	|500	|510	|1200	|1201	|Municp3	|Municp3		|||3	|-99|3|		8|	1|	1|	1|
|7|1|2|10	|900	|940	|1201	|1000	|Municp4	|Municp5		|||4	|-99|4|		8|	1|	1|	1|
|7|1|3|7	|940	|950	|1000	|1900	|Municp5	|Municp6		|||3	|-99|3|		8|	1|	1|	1|
|7|1|4|20	|1010	|1050	|1900	|1201	|Municp6	|Municp4		|||3	|-99|3|		8|	1|	1|	1|
|7|1|5|10	|1100	|1300	|1201	|1200	|Municp4	|Municp3		|||11	|-99|3|		8|	1|	1|	1|
|7|2|4|20	|1000	|1300	|8000	|8704	|Zurich		|Herrlibrg		|||3	|-99|3|		8|	1|	2|	1|
|7|2|5|10	|1300	|1500	|8704	|8000	|Herrliberg |Zurich		   |||11  |-99|3|		8|	1|	2|	1|

In most cases, you want query a national travel survey that only contains car legs. To build the required data file and consider this condition, a SQL statement like the following one is suitable. This example queries the Swiss national travel survey Mikrozensus Mobilität 2011 and produced a valid data file.

```
SELECT
     etappen.HHNR as householdID, etappen.ZIELPNR as personID, etappen.ETNR as legID, #IDs
     etappen.rdist as distance, # Distance
     etappen.f51100 as legStartTime, etappen.f51400 as legEndTime, # Time

     etappen.S_PLZ as legStartAddress, etappen.Z_PLZ as legEndAddress,
     etappen.S_Ort as legStartAddressAlt, 
     etappen.Z_Ort as legEndAddressAlt, 
     '' as legStartAddressManual,
     '' as legEndAddressManual,# Addresses

     etappen.f52900 as legPurpose, etappen.f51700 as leisurePurpose, # Purpose
     wege.wzweck1 as wayPurpose, wege.wzweck2 as wayHomewayPurpose, # WayPurposes
     etappen.f51300 as driverType, etappen.f51310a as carType, etappen.f51310b as carID, # Carinfo
     haushalte.f31100 as homeParking # Parking at home
FROM etappen
     JOIN haushalte ON etappen.HHNR = haushalte.HHNR
     JOIN wege ON etappen.HHNR = wege.HHNR AND etappen.ZIELPNR = wege.ZIELPNR AND etappen.WEGNR = wege.WEGNR

WHERE (etappen.f51300 = 7 OR etappen.f51300 = 8) AND NOT (etappen.S_KANTON = -97 OR etappen.Z_KANTON = -97)
ORDER BY householdID,legStartTime,personID,carType ASC
```


### Charging Station Coordinates
This Charging Station Coordinates data file contains all charging stations that can be added along the legs. The model uses this data for its Google Maps Routing.

The path to the charging station coordinates data file needs to be set in RunAnalysis[Country].java.
```
CHARGING_STATIONS_FILENAME = "CH_stations.csv";
```

Each record of the data file is an individual charging station at a specific coordinate. The file need to have the following structure (the names of the fields can be adjusted in RunAnalysis[Country].java):


|Field|Description|
|---|---|
|ID|A unique identifier|
|Lat|Latitude of the charging station|
|Lng|Longitued of the charging station|
|Power|The power of the charging station in kW|
|Name|The name of the charging station|
|Type|Either service_station or existing_station|
|Double|To exlude a dataset set to 1|

The following table shows an entry

|ID|Lat|Lng|Power|Name|Type|Double|
|---|---|---|---|---|---|---|
|71982|	47.409|	8.601|	50	|Nissan Neugut Garage Flury in Wallisellen|	existing_station	|0|

### Car models
This car models data file contains the specifics of all car models available to the simulation. The share of car models in the individual configurations is found in another data file, the "car model share" file. Both files use the same ID fields.

The path to the Car models data file needs to be set in RunAnalysis[Country].java. Do not add the csv ending.
```
CAR_MODEL_FILENAME = "carmodels";
```


Each record of the data file is an individual car model. The csv file needs to have the following structure (the names of the fields can be adjusted in RunAnalysis[Country].java).

|Field|Description|Format|
|---|---|---|
|carModelID|A unique identifier|Unique integer|
|carModel|Name of the car model|String|
|modelYear|Year of the car model|Integer|
|range|Range in km|Float|
|batteryCapacity|Capacity of battery in kWh|Float|
|energyConsumption|Average energy Consumption of the car in kWh/km|Float|
|availablePlugs|Relates to the charger data file and lists available plugs in car model|list separated with ;|
|price|informative price|String|

### Car models share
For the simulations, the share of car model needs to be known.

The path to the Car models share data file needs to be set in the configurations. Do not add the csv ending.
```
CAR_MODEL_SHARE_FILENAME = share_baseline_CH
```

|Field|Description|Format|
|---|---|---|
|carModelID|A unique identifier. Needs to be the same as in Car models|Unique integer|
|probability|THe probability that this car models gets assigned in the random assignment of car models|Float 0 - 1|


### Charging station trypes
For the assignment of charging stations at activities, the typed of charging stations need to be defined in this file.

|Field|Description|Format|
|---|---|---|
|chargerID|A unique identifier|Unique integer|
|chargerType|A name for the charger|string|
|chargerPower|The power of the Charger in kW|float|


### Charging station trypes
THe distribution of the chargers is set in this file. It is a matrix of all activites that are available in the travel survey and defined in RunAnalysis[Country].java

Compare file input 


# Configuring the configuration

The configuration files, most simulation variables can be set. The files are located in: ```config/[countryNameShort]/``` and have the ending properties; e.g. config/CH/CH_baseline.properties

The following variables can be set:

TODO: describe car_model share file, charger matrix file

|Variable|Description|Format|
|---|---|---|
|Filenames|||
|CAR_MODEL_SHARE_FILENAME|Name of the car model share file|String|
|CHARGER_MATRIX_FILENAME|Name of the charger matrix file|String|
|Scenario Variable||
|considerHouseholdParkingSpaces| |true or false|
|serviceAreasEnabled| |true or false|
|enableChargingDecisionLevel| |0-3|


# Using Google Api

You need to get a Api key and enter it in the respective file in the input folder.

The folder serialised stores all temporaray data from the google queries.


# Running a simulation
To run a simulation, you need to have a configuration file set up. If you use Eclipse, proceed as follows to run a simulation:

1. Run > Run Configurations...
2. Select Java Application and press the "New" Button
3. In the Name field enter a Name for your configuration 
4. In the project field select the project BEVPO
5. In the field Main class select "Search..." and search for "RunAnalysis[Country].java"
6. Open the tab "Arguments" and enter the name of your configuration, e.g. "CH_baseline" (i.e. without the ".properties" fileending).




