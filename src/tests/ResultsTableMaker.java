package tests;

import java.util.ArrayList;

import common.instance.reader.CCPInstanceEntity;
import common.instance.reader.InstanceReader;
import ga.ccp.CcpRuntimeConfiguration;
import ga.ccp.GA_CCP;
import ga.framework.Chromosome;

public class ResultsTableMaker {
	
	private final String INSTANCE_EXTENSION = ".txt";
	
	public static void main(String[] args) throws Exception {
		ResultsTableMaker resultsTableMaker = new ResultsTableMaker();
		String resultTable = "";
		
		resultTable = resultsTableMaker.getResultTableForInstances(buildRanReal240BestResultsInstanceSet());
		System.out.println(resultTable);
	}
	
	private static ArrayList<Object[]> buildRanReal240BestResultsInstanceSet() {
		ArrayList<Object[]> instanceSet = new ArrayList<>();
		
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_01", 223220.90});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_02", 202095.20});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_03", 196391.99});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_04", 222711.88});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_05", 193570.70});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_06", 214707.61});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_07", 207115.03});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_08", 202820.87});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_09", 207004.32});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_10", 190248.29});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_11", 202692.08});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_12", 199006.57});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_13", 199766.31});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_14", 226724.31});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_15", 189022.16});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_16", 202272.69});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_17", 192878.67});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_18", 192747.90});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_19", 197005.23});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_20", 210331.30});
		
		return instanceSet;
	}
	
	public String getResultTableForInstances(ArrayList<Object[]> instanceSet) throws Exception {
		LineResultTableEntity lineResult = null;
		StringBuilder resultTable = new StringBuilder();
		
		for(Object[] instanceInfo : instanceSet) {
			lineResult = getResultTableLineForInstance((InstanceReader.InstanceType)instanceInfo[0], (String)instanceInfo[1], (double)instanceInfo[2]);
			resultTable.append(lineResult.getLatexResultLine());
			resultTable.append("\n");
		}
		
		return resultTable.toString();
	}
	
	private LineResultTableEntity getResultTableLineForInstance(InstanceReader.InstanceType instanceType, String instanceName, double stateOfArtValue) throws Exception {
		CCPInstanceEntity instance = null;
		
		instance = InstanceReader.readerInstance(instanceType, instanceName + INSTANCE_EXTENSION);
		instance.setName(instanceName);
		return  getResultTableLineForInstance(instance, stateOfArtValue);
	}
	
	private LineResultTableEntity getResultTableLineForInstance(CCPInstanceEntity instance, double stateOfArtValue) {
		LineResultTableEntity lineResults = new LineResultTableEntity();
		lineResults.setGaValue(Runner.runGAVersion(instance));
		lineResults.setHga1Value(Runner.runHGA1Version(instance));
		lineResults.setHga2Value(Runner.runHGA2Version(instance));
		lineResults.setInstanceName(instance.getName());
		lineResults.setValueStateOfArt(stateOfArtValue);
		
		return lineResults;
	}

}
