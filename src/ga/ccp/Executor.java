package ga.ccp;

import java.util.ArrayList;

import common.instance.reader.CCPInstanceEntity;
import common.instance.reader.InstanceReader;
import common.instance.reader.InstanceReader.InstanceType;

public class Executor {
    private InstanceType instanceClassType = InstanceType.RanReal240;
    
    public static void main(String[] args) {
	Executor exec = new Executor();
	exec.execute();
    }
    
    private void execute() {
	switch (instanceClassType) {
	case RanReal240:
	    handleRanReal240();
	    break;

	default:
	    break;
	}
    }
    
    private void handleRanReal240() {
	ArrayList<String> instanceNames = null;
	ArrayList<Object[]> summary = new ArrayList<>();
	
	instanceNames = getAllInstanceNames240();
	for(int i = 0; i < instanceNames.size(); i++) {
	    CCPInstanceEntity instance;
	    Object[] instanceSummary = new Object[2];
	    try {
		instance = InstanceReader.readerInstance(InstanceReader.InstanceType.RanReal240, instanceNames.get(i));
		GA_CCP ccp = new GA_CCP(instance);
		CCPChromosome bestChromosome = (CCPChromosome) ccp.solve();
		instanceSummary[0] = instanceNames.get(i);
		instanceSummary[1] = bestChromosome.getFitness();
		summary.add(instanceSummary);
		
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	printSummary(summary);
    }
    
    private void printSummary(ArrayList<Object[]> summary) {
	double average = 0.0;
	for(int i = 0; i < summary.size(); i++) {
	    System.out.println("Instance name: " + summary.get(i)[0].toString() + " Fitness: " + summary.get(i)[1].toString());
	    average += (double)summary.get(i)[1];
	}
	System.out.println("Average: " + average / (double)summary.size());
    }
    
    private ArrayList<String> getAllInstanceNames240() {
	ArrayList<String> allInstanceNames = new ArrayList<>();
	allInstanceNames.add("RanReal240_01.txt");
	allInstanceNames.add("RanReal240_02.txt");
	allInstanceNames.add("RanReal240_03.txt");
	allInstanceNames.add("RanReal240_04.txt");
	allInstanceNames.add("RanReal240_05.txt");
	allInstanceNames.add("RanReal240_06.txt");
	allInstanceNames.add("RanReal240_07.txt");
	allInstanceNames.add("RanReal240_08.txt");
	allInstanceNames.add("RanReal240_09.txt");
	allInstanceNames.add("RanReal240_10.txt");
	allInstanceNames.add("RanReal240_11.txt");
	allInstanceNames.add("RanReal240_12.txt");
	allInstanceNames.add("RanReal240_13.txt");
	allInstanceNames.add("RanReal240_14.txt");
	allInstanceNames.add("RanReal240_15.txt");
	allInstanceNames.add("RanReal240_16.txt");
	allInstanceNames.add("RanReal240_17.txt");
	allInstanceNames.add("RanReal240_18.txt");
	allInstanceNames.add("RanReal240_19.txt");
	allInstanceNames.add("RanReal240_20.txt");
	
	return allInstanceNames;
    }
}
