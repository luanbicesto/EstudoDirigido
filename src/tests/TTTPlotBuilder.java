package tests;

import java.util.ArrayList;

import common.instance.reader.CCPInstanceEntity;
import common.instance.reader.InstanceReader;
import ga.ccp.CCPParameters;
import ga.ccp.Common;
import jdk.nashorn.internal.runtime.regexp.joni.constants.CCVALTYPE;

public class TTTPlotBuilder {
	private String targetInstanceName;
	private InstanceReader.InstanceType instanceType;
	private double targetValue;
	private int executionTimes;
	
	private static final int DEFAULT_EXECUTION_TIMES = 200;
	private static final boolean RUN_GA_VERSION = false;
	
	public TTTPlotBuilder(String targetInstanceName, InstanceReader.InstanceType instanceType, double targetValue) {
		this(targetInstanceName, instanceType, targetValue, DEFAULT_EXECUTION_TIMES);
	}
	
	public TTTPlotBuilder(String targetInstanceName, InstanceReader.InstanceType instanceType, double targetValue, int executionTimes) {
		this.targetInstanceName = targetInstanceName;
		this.instanceType = instanceType;
		this.targetValue = targetValue;
		this.executionTimes = executionTimes;
	}
	
	public static void main(String[] args) throws Exception {
		TTTPlotBuilder tttPlotBuilder = null;
		
		if(RUN_GA_VERSION) {
			tttPlotBuilder = new TTTPlotBuilder(CCPParameters.INSTANCE_NAME, CCPParameters.INSTANCE_TYPE, 356678); //480-01
		} else {
			tttPlotBuilder = new TTTPlotBuilder(CCPParameters.INSTANCE_NAME, CCPParameters.INSTANCE_TYPE, 522443); //480-01
		}
		
		tttPlotBuilder.buildTTTPlot();
	}
	
	public void buildTTTPlot() throws Exception {
		ArrayList<ArrayList<Long>> runningTimes = getRunningTimes(this.targetInstanceName, this.instanceType);
	}
	
	private ArrayList<ArrayList<Long>> getRunningTimes(String instanceName, InstanceReader.InstanceType instanceType) throws Exception {
		int numberAlgorithms = RUN_GA_VERSION ? 3 : 2;
		int seed = 0;
		CCPInstanceEntity instance = null;
		
		ArrayList<ArrayList<Long>> runningTimes = new ArrayList<>(numberAlgorithms);
		for(int i = 0; i < numberAlgorithms; i++) {
			runningTimes.add(new ArrayList<>(this.executionTimes));
		}
		
		instance = InstanceReader.readerInstance(instanceType, instanceName);
		for(int i = 0; i < this.executionTimes; i++) {
			runningTimes.get(0).add(getRunningTimeHGA1Version(instance, seed));
			runningTimes.get(1).add(getRunningTimeHGA2Version(instance, seed));
			
			if(RUN_GA_VERSION) {
				runningTimes.get(2).add(getRunningTimeGAVersion(instance, seed));
			}
			seed++;
		}
		
		return runningTimes;
	}
	
	private long getRunningTimeGAVersion(CCPInstanceEntity instance, int seed) {
		long startTime = System.currentTimeMillis();
		Runner.runGAVersion(instance, this.targetValue, seed);
		return (long)Common.getRunningTime(startTime);
	}
	
	private long getRunningTimeHGA1Version(CCPInstanceEntity instance, int seed) {
		long startTime = System.currentTimeMillis();
		Runner.runHGA1Version(instance, this.targetValue, seed);
		return (long)Common.getRunningTime(startTime);
	}
	
	private long getRunningTimeHGA2Version(CCPInstanceEntity instance, int seed) {
		long startTime = System.currentTimeMillis();
		Runner.runHGA2Version(instance, this.targetValue, seed);
		return (long)Common.getRunningTime(startTime);
	}
}
