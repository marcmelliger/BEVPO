package geo.processutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;

import geo.RouteLeg;
import manager.ManageDatafile;

public class GeolocateStations {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		ManageDatafile.setCharset("MacRoman");
		
		ArrayList<Map<String, String>> stationsList = 
				ManageDatafile.readCSV("/Users/marc/Documents/ETH/Masterarbeit/Data/Input/chargermap/FIN_rawdata/FIN_service_areas_ABC_north.csv");

		GeoApiContext context = new GeoApiContext().setApiKey(RouteLeg.googleApiKey);

		BufferedWriter bw = null;

		String finalString = "ID,Lat,Lng,power,title\n";


		int i = 0;
		for(Map<String, String> stationList : stationsList){

			//TreeMap<String, String> stationListA = new TreeMap<>();

			//stationList.forEach((k, v) -> stationListA.put(k, v));
			
			String address = stationList.get("Address");
			String name = stationList.get("Name");


			//String address = stationListA.firstEntry().getValue();

			GeocodingResult[] results =  GeocodingApi.geocode(context, address + "; Finland").await();
			finalString = finalString + 
					i++ + "," + 
					results[0].geometry.location.lat + "," + 
					results[0].geometry.location.lng + "," + 
					"," +
					name + 	"\n";
		}

		try{
			File file = new File("/Users/marc/Documents/ETH/Masterarbeit/Data/Input/chargermap/FIN_rawdata/FIN_service_areas_ABC_north_coord.csv");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(finalString);
			System.out.println("File written Successfully");


		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		finally
		{ 
			try{
				if(bw!=null)
					bw.close();
			} catch(Exception ex){
				System.out.println("Error in closing the BufferedWriter"+ex);
			}
		}

	}


}

