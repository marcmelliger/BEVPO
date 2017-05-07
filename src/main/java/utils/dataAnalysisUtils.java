package utils;

import java.util.Formatter;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import manager.ManageDatafile;
import runmodel.RunAnalysis;

public class dataAnalysisUtils {

	public dataAnalysisUtils() {
	}
	
	public static String formatString(String format, Object ... args){
		
		Formatter formatter = new Formatter();
		
		String formattedString = formatter.format(format, args).toString();
		
		formatter.close();
		
		return formattedString;

	}
	
	public static Random random(){
		
		Random random = new Random();
		if(RunAnalysis.useRandomSeed){
			random.setSeed(RunAnalysis.randomSeed);
		}
		
		return random;
		
	}
	
	public static String printHashCode(Object object){
		
		//return " (" + Integer.toHexString(object.hashCode()).toString() + ") ";
		return "";
		
	}
	
	public static TreeSet<String> getSetOfActivities(){
	
		Set<Entry<Integer, String>> activityStringsSet = RunAnalysis.activityStrings.entrySet();
		TreeSet<String> activities = new TreeSet<>();
		
		for(Entry<Integer, String> entry: activityStringsSet){
			activities.add(entry.getValue());
			if(ManageDatafile.isAssignDirectionPurposes()){
				activities.add(entry.getValue() + "#returnhome");
				activities.add(entry.getValue() + "#transfer");

			}
		}
		
		return activities;
			
	}
	

}
