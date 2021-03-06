package ga.ccp;

import ga.framework.GAConfiguration;

public class CcpRuntimeConfiguration {
	public enum MOMENT_LOCAL_SEARCH{
		BEGINNING,
		END
	}
	
	private boolean enableHybridLocalSearch;
	private MOMENT_LOCAL_SEARCH momentLocalSearch;
	private double percentageLocalSearch;
	private int absoluteHybridPupulation;
	private double timeToTargetValue;
	private int seed;
	
	private static final double DEFAULT_TIME_TO_TARGET_VALUE = 0.0;
	private static final int DEFAULT_SEED = 0;
	
	public CcpRuntimeConfiguration() {
		this(DEFAULT_TIME_TO_TARGET_VALUE, DEFAULT_SEED);
	}
	
	public CcpRuntimeConfiguration(double timeToTargetValue, int seed) {
		this.timeToTargetValue = timeToTargetValue;
		this.seed = seed;
	}
	
	public boolean isEnableHybridLocalSearch() {
		return enableHybridLocalSearch;
	}
	public void setEnableHybridLocalSearch(boolean enableHybridLocalSearch) {
		this.enableHybridLocalSearch = enableHybridLocalSearch;
	}
	public MOMENT_LOCAL_SEARCH getMomentLocalSearch() {
		return momentLocalSearch;
	}
	public void setMomentLocalSearch(MOMENT_LOCAL_SEARCH momentLocalSearch) {
		this.momentLocalSearch = momentLocalSearch;
	}
	public double getPercentageLocalSearch() {
		return percentageLocalSearch;
	}
	public void setPercentageLocalSearch(double percentageLocalSearch) {
		this.percentageLocalSearch = percentageLocalSearch;
	}
	public int getAbsoluteHybridPupulation() {
		return absoluteHybridPupulation;
	}
	public void setAbsoluteHybridPupulation(int absoluteHybridPupulation) {
		this.absoluteHybridPupulation = absoluteHybridPupulation;
	}
	public double getTimeToTargetValue() {
		return timeToTargetValue;
	}
	public void setTimeToTargetValue(double timeToTargetValue) {
		this.timeToTargetValue = timeToTargetValue;
	}
	public int getSeed() {
		return seed;
	}
	public void setSeed(int seed) {
		this.seed = seed;
	}

	public static CcpRuntimeConfiguration getDefaultConfiguration() {
		CcpRuntimeConfiguration ccpConfiguration = new CcpRuntimeConfiguration();
		
		ccpConfiguration.setEnableHybridLocalSearch(GAConfiguration.ENABLE_HYBRID_POPULATION);
		ccpConfiguration.setPercentageLocalSearch(GAConfiguration.PERCENTAGE_APPLY_HYBRID_TRANSFORMATION);
		ccpConfiguration.setAbsoluteHybridPupulation(GAConfiguration.ABSOLUTE_HYBRID_POPULATION);
		ccpConfiguration.setMomentLocalSearch(MOMENT_LOCAL_SEARCH.END);
		
		return ccpConfiguration;
	}
	
	public static CcpRuntimeConfiguration getGAConfiguration() {
		CcpRuntimeConfiguration ccpConfiguration = new CcpRuntimeConfiguration();
		
		ccpConfiguration.setEnableHybridLocalSearch(false);
		ccpConfiguration.setPercentageLocalSearch(GAConfiguration.PERCENTAGE_APPLY_HYBRID_TRANSFORMATION);
		ccpConfiguration.setAbsoluteHybridPupulation(GAConfiguration.ABSOLUTE_HYBRID_POPULATION);
		ccpConfiguration.setMomentLocalSearch(MOMENT_LOCAL_SEARCH.END);
		
		return ccpConfiguration;
	}
	
	public static CcpRuntimeConfiguration getHGA1Configuration() {
		CcpRuntimeConfiguration ccpConfiguration = new CcpRuntimeConfiguration();
		
		ccpConfiguration.setEnableHybridLocalSearch(true);
		ccpConfiguration.setPercentageLocalSearch(GAConfiguration.PERCENTAGE_APPLY_HYBRID_TRANSFORMATION);
		ccpConfiguration.setAbsoluteHybridPupulation(GAConfiguration.ABSOLUTE_HYBRID_POPULATION);
		ccpConfiguration.setMomentLocalSearch(MOMENT_LOCAL_SEARCH.BEGINNING);
		
		return ccpConfiguration;
	}
	
	public static CcpRuntimeConfiguration getHGA2Configuration() {
		CcpRuntimeConfiguration ccpConfiguration = new CcpRuntimeConfiguration();
		
		ccpConfiguration.setEnableHybridLocalSearch(true);
		ccpConfiguration.setPercentageLocalSearch(GAConfiguration.PERCENTAGE_APPLY_HYBRID_TRANSFORMATION);
		ccpConfiguration.setAbsoluteHybridPupulation(GAConfiguration.ABSOLUTE_HYBRID_POPULATION);
		ccpConfiguration.setMomentLocalSearch(MOMENT_LOCAL_SEARCH.END);
		
		return ccpConfiguration;
	}
	
	
}
