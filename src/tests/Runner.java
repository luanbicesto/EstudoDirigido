package tests;

import common.instance.reader.CCPInstanceEntity;
import common.instance.reader.InstanceReader;
import ga.ccp.CCPParameters;
import ga.ccp.CcpRuntimeConfiguration;
import ga.ccp.GA_CCP;
import ga.framework.Chromosome;

public class Runner {
	
	public static void main(String[] args) throws Exception {
		CCPInstanceEntity instance = null;
		
		instance = InstanceReader.readerInstance(CCPParameters.INSTANCE_TYPE, CCPParameters.INSTANCE_NAME);
		Runner.runGAVersion(instance);
	}
	
	public static double runGAVersion(CCPInstanceEntity instance) {
		return run(instance, CcpRuntimeConfiguration.getGAConfiguration());
	}
	
	public static double runGAVersion(CCPInstanceEntity instance, double TTTValue, int seed) {
		return runTimeToTarget(instance, CcpRuntimeConfiguration.getGAConfiguration(), TTTValue, seed);
	}
	
	public static double runHGA1Version(CCPInstanceEntity instance) {
		return run(instance, CcpRuntimeConfiguration.getHGA1Configuration());
	}
	
	public static double runHGA1Version(CCPInstanceEntity instance, double TTTValue, int seed) {
		return runTimeToTarget(instance, CcpRuntimeConfiguration.getHGA1Configuration(), TTTValue, seed);
	}
	
	public static double runHGA2Version(CCPInstanceEntity instance) {
		return run(instance, CcpRuntimeConfiguration.getHGA2Configuration());
	}
	
	public static double runHGA2Version(CCPInstanceEntity instance, double TTTValue, int seed) {
		return runTimeToTarget(instance, CcpRuntimeConfiguration.getHGA2Configuration(), TTTValue, seed);
	}
	
	private static double runTimeToTarget(CCPInstanceEntity instance, CcpRuntimeConfiguration baseCcpConf, double TTTValue, int seed) {
		baseCcpConf.setTimeToTargetValue(TTTValue);
		baseCcpConf.setSeed(seed);
		
		return run(instance, baseCcpConf);
	}
	
	private static double run(CCPInstanceEntity instance, CcpRuntimeConfiguration ccpConf) {
		GA_CCP ccpSolver = new GA_CCP(instance, ccpConf);
		Chromosome bestChromosome = ccpSolver.solve();
		return bestChromosome.getFitness();
	}
}
