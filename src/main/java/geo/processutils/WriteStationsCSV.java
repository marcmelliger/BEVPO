package geo.processutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class WriteStationsCSV {

	public static void main(String[] args) {
		parseExistingChargingStations("/Users/marc/Documents/ETH/Masterarbeit/Data/Input/chargermap/CH_level3_compact.json");

	}

	private static void parseExistingChargingStations(String filename) {

		File file = new File(filename);

		try {
			String jsonString = Files.toString(file, Charsets.UTF_8);

			JSONArray allPoi = new JSONArray(jsonString);

			String outputString = "ID;Lat;Lng;Power;Name\n";

			for(int i = 0; i < allPoi.length(); i++){
				JSONObject poi = allPoi.getJSONObject(i);

				String ID =  Integer.toString(poi.getInt("ID"));
				double lat = poi.getJSONObject("AddressInfo").getDouble("Latitude");
				double lng = poi.getJSONObject("AddressInfo").getDouble("Longitude");
				String title = poi.getJSONObject("AddressInfo").getString("Title");

				Integer power = 0;
				Integer powerOutput = 0;

				for(int j = 0; j < poi.getJSONArray("Connections").length(); j++){


					try {
						power = poi.getJSONArray("Connections").getJSONObject(j).getInt("PowerKW");
						// get Stations with highest power
						if (power > powerOutput && power >= 40 ) {
							powerOutput = power;
						} 
					} catch (Exception e) {}


				}

				if(powerOutput != 0)
					outputString = outputString + ID + ";" + lat + ";" + lng + ";" + powerOutput + ";" + title + "\n";

			}
			writeCSV(outputString);


		} catch (IOException | JSONException e1) {
			e1.printStackTrace();
		}

	}


	private static void writeCSV(String output) {

		BufferedWriter bw = null;

		try{
			File file = new File("/Users/marc/Documents/ETH/Masterarbeit/Data/Input/chargermap/CH_existingStations_coord.csv");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(output);
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
