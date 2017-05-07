package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class AddressTrans {
	
	private static HashMap<String, String> addressTransMap;
	public static String filename = "/Users/marc/Documents/ETH/Masterarbeit/Data/Input/address_trans_CH.csv";

	
	private static void readAddressTransMap(){
		
		File file = new File(filename);
		
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "MacRoman"));
			
			String line;
			while((line=br.readLine())!=null){
				String str[] = line.split(",");
				addressTransMap.put(str[0], str[1]);
			
			}
			br.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	public static String getTransAddress(String orig){
		
		if(addressTransMap == null){
			addressTransMap = new HashMap<>();
			readAddressTransMap();
			
		}
		
		if(addressTransMap.containsKey(orig)){
			return addressTransMap.get(orig);
		} else {
			return "";
		}
	}

}
