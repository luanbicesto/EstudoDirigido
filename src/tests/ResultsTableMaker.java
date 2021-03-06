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
		
		resultTable = resultsTableMaker.getResultTableForInstances(buildRanReal480BestResultsInstanceSet());
		System.out.println(resultTable);
	}
	
	private static ArrayList<Object[]> buildRanReal240BestResultsInstanceSet() {
		ArrayList<Object[]> instanceSet = new ArrayList<>();
		
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_01", 222931.65});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_02", 201691.73});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_03", 196199.95});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_04", 222711.88});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_05", 192892.46});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_06", 214256.87});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_07", 206830.64});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_08", 202576.92});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_09", 206739.99});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_10", 189846.25});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_11", 202179.11});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_12", 198691.78});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_13", 199484.53});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_14", 226584.34});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_15", 188426.05});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_16", 201554.40});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_17", 192373.26});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_18", 192334.36});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_19", 196657.15});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal240, "RanReal240_20", 209825.28});
		
		return instanceSet;
	}
	
	private static ArrayList<Object[]> buildRanReal480BestResultsInstanceSet() {
		ArrayList<Object[]> instanceSet = new ArrayList<>();
		
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_01", 547892.82});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_02", 502322.88});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_03", 486391.16});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_04", 513016.49});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_05", 474143.57});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_06", 523003.55});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_07", 536212.13});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_08", 523419.36});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_09", 548621.16});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_10", 511338.69});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_11", 516058.83});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_12", 491899.13});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_13", 524255.44});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_14", 505311.16});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_15", 508386.57});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_16", 541827.80});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_17", 529671.66});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_18", 516230.55});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_19", 514385.62});
		instanceSet.add(new Object[] {InstanceReader.InstanceType.RanReal480, "RanReal480_20", 508986.95});
		
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
