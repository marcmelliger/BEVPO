package analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringJoiner;

import model.dayphases.ChargingActivityStats;
import runmodel.RunAnalysis;

public class AnalyzeChargingActivities {

	public static void exportChargingActivitiesToCSV(){

		File file = new File(RunAnalysis.basePathOutput + "scenarios/" + RunAnalysis.getScenarioName() + "/chargingActivity_it" + RunAnalysis.getMonteCarloIteration() + ".csv");

		if(!file.getParentFile().exists())
			file.getParentFile().mkdirs();

		FileWriter fw;
		try {
			fw = new FileWriter(file);
			PrintWriter out = new PrintWriter(fw);
			out.println("carTrip,name,distance,message,reason");

			for(ChargingActivityStats stats : ChargingActivityStats.listOfChargingEvents){

				StringJoiner sj = new StringJoiner(",");

				sj.add(Integer.toString(stats.getCarTrip().getCarTripID()));
				sj.add(stats.getChargingActivity().getName());
				sj.add(Double.toString(stats.getCarTrip().getTotalDistance()));
				sj.add(stats.getMessage());
				sj.add(stats.getReason());

				out.println(sj.toString());

			}

			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
