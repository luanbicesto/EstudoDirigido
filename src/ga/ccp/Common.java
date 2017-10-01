package ga.ccp;

import common.instance.reader.CCPInstanceEntity;

public class Common {

    public static boolean isClusterOverWeighted(int clusterId, double targetClusterWeight, CCPInstanceEntity instance) throws Exception {
	return Double.compare(targetClusterWeight, instance.getUpperBound()[clusterId]) > 0;
    }
    
    public static boolean isClusterUnderWeighted(int clusterId, double targetClusterWeight, CCPInstanceEntity instance) throws Exception {
	return Double.compare(targetClusterWeight, instance.getLowerBound()[clusterId]) < 0;
    }
}
