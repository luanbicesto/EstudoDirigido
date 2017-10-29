package ga.ccp;

import common.instance.reader.CCPInstanceEntity;

public class Common {

	public static boolean isClusterOverWeighted(int clusterId, double targetClusterWeight, CCPInstanceEntity instance)
			throws Exception {
		return Double.compare(targetClusterWeight, instance.getUpperBound()[clusterId]) > 0;
	}

	public static boolean isClusterUnderWeighted(int clusterId, double targetClusterWeight, CCPInstanceEntity instance)
			throws Exception {
		return Double.compare(targetClusterWeight, instance.getLowerBound()[clusterId]) < 0;
	}

	public static boolean isClusterWithinWeights(int clusterId, double targetClusterWeight, CCPInstanceEntity instance)
			throws Exception {
		return !isClusterUnderWeighted(clusterId, targetClusterWeight, instance)
				&& !isClusterOverWeighted(clusterId, targetClusterWeight, instance);
	}
	
	public static double getRunningTime(long startTime) {
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

		return (double) totalTime / (double) 1000;
	}
}
