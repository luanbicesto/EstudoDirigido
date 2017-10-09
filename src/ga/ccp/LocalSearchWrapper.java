package ga.ccp;

import java.util.ArrayList;
import java.util.Random;

import common.instance.reader.CCPInstanceEntity;
import ga.ccp.CCPParameters.LocalSearchStrategy;

public class LocalSearchWrapper {
    
    private ArrayList<Integer> allNodesIndices;
    private ArrayList<Integer> allClustersIndices;
    private Random rng;
    private CCPInstanceEntity instance;
    
    public LocalSearchWrapper(CCPInstanceEntity instance, Random rng) {
	allNodesIndices = new ArrayList<>();
	allClustersIndices = new ArrayList<>();
	
	for(int i = 0; i < instance.getN(); i++) {
	    allNodesIndices.add(i);
	}
	
	for(int k = 0; k < instance.getK(); k++) {
	    allClustersIndices.add(k);
	}
	
	this.rng = rng;
	this.instance = instance;
    }
    
    public void applyLocalSearch(LocalSearchStrategy localSearchStrategy, CCPChromosome chromosome, boolean applyToAllNodes) {
	switch (localSearchStrategy) {
	case Swap:
	    try {
		applySwap(chromosome, applyToAllNodes);
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    break;
	case OneChange:
	    try {
		applyOneChange(chromosome, applyToAllNodes);
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    break;

	default:
	    break;
	}
    }
    
    private void applyOneChange(CCPChromosome chromosome, boolean applyToAllNodes) throws Exception {
	ArrayList<Integer> allNodesIndicesCopy = new ArrayList<>(allNodesIndices);
	ArrayList<Integer> allClustersIndicesCopy;
	int node, cluster;
	int indexNode = 0, indexCluster = 0;
	int originalClusterId = 0;
	double currentNodeContributionCluster = 0.0;
	double migrateNodeContributionCluster = 0.0;
	int nodesToImprove = getNodesToImprove();
	int nodesImproved = 0;
	
	while(allNodesIndicesCopy.size() > 0) {
	    indexNode = rng.nextInt(allNodesIndicesCopy.size());
	    node = allNodesIndicesCopy.get(indexNode);
	    originalClusterId = chromosome.getCodification()[node];
	    currentNodeContributionCluster = chromosome.computeNodeContributionInCluster(node);
	    allClustersIndicesCopy = new ArrayList<>(allClustersIndices);
	    
	    while(!allClustersIndicesCopy.isEmpty()) {
		indexCluster = rng.nextInt(allClustersIndicesCopy.size());
		cluster = allClustersIndicesCopy.get(indexCluster);
		if(cluster != originalClusterId) {
		    migrateNodeContributionCluster = computeOneChangeImprovement(node, cluster, chromosome);
		    double fitnessDifference = migrateNodeContributionCluster - currentNodeContributionCluster;
		    if(fitnessDifference >= 1) {
			migrateNode(node, cluster, chromosome, fitnessDifference);
			nodesImproved++;
			break;
		    }
		}
		allClustersIndicesCopy.remove(new Integer(cluster));
	    }
	    
	    if(nodesImproved == nodesToImprove && !applyToAllNodes) {
		break;
	    }
	    
	    allNodesIndicesCopy.remove(new Integer(node));
	}
    }
    
    private int getNodesToImprove() {
	return (int)Math.round((instance.getN() * CCPParameters.MAX_PERCENTAGE_NUMBER_NODES_LS));
    }
    
    private void migrateNode(int node, int targetCluster, CCPChromosome chromosome, double fitnessDifference) throws Exception {
	chromosome.setAllele(node, targetCluster, false);
	chromosome.addToFitness(fitnessDifference);
    }
    
    private double computeOneChangeImprovement(int node, int targetCluster, CCPChromosome chromosome) throws Exception {
	double improvement = 0.0;
	int originalCluster = 0;
	
	if(canMigrateToCluster(node, targetCluster, chromosome)) {
	    originalCluster = chromosome.getCodification()[node];
	    chromosome.getCodification()[node] = targetCluster;
	    chromosome.getNodesByCluster().get(targetCluster).add(new Integer(node));
	    improvement = chromosome.computeNodeContributionInCluster(node);
	    chromosome.getNodesByCluster().get(targetCluster).remove(new Integer(node));
	    chromosome.getCodification()[node] = originalCluster;
	}
	
	return improvement;
    }
    
    private boolean canMigrateToCluster(int node, int targetCluster, CCPChromosome chromosome) {
	double targetClusterFutureWeight = 0.0;
	double originalClusterFutureWeight = 0.0;
	int originalCluster = 0;
	
	try {
	    originalCluster = chromosome.getCodification()[node];
	    targetClusterFutureWeight = chromosome.getCurrentClustersWeight().get(targetCluster) + instance.getNodeWeights()[node];
	    originalClusterFutureWeight = chromosome.getCurrentClustersWeight().get(originalCluster) - instance.getNodeWeights()[node];
	    
	    return Common.isClusterWithinWeights(targetCluster, targetClusterFutureWeight, instance) &&
		   Common.isClusterWithinWeights(originalCluster, originalClusterFutureWeight, instance);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	return false;
    }
    
    private void applySwap(CCPChromosome chromosome, boolean applyToAllNodes) throws Exception {
	int node1 = 0, node2 = 0;
	int indexNode = 0;
	ArrayList<Integer> allNodesIndicesCopy = new ArrayList<>(allNodesIndices);
	double swapImprovement = 0.0;
	double bestSwapImprovement = 0.0;
	int swapNode = -1;
	int nodesToImprove = getNodesToImprove();
	int nodesImproved = 0;
	
	while(allNodesIndicesCopy.size() > 0) {
	    indexNode = rng.nextInt(allNodesIndicesCopy.size());
	    node1 = allNodesIndicesCopy.get(indexNode);
	    swapImprovement = 0.0;
	    swapNode = -1;
	    bestSwapImprovement = 0.0;
	    
	    for(int j = 0; j < allNodesIndicesCopy.size(); j++) {
		indexNode = rng.nextInt(allNodesIndicesCopy.size());
		node2 = allNodesIndicesCopy.get(indexNode);
		if(node1 != node2) {
		    swapImprovement = computeSwapImprovement(node1, node2, chromosome);
		    if(swapImprovement > 0) {
			swapNode = node2;
			bestSwapImprovement = swapImprovement;
			nodesImproved++;
			break;
		    }
		}
	    }
	    
	    if(swapNode != -1) {
		swapNodes(node1, swapNode, bestSwapImprovement, chromosome);
		allNodesIndicesCopy.remove(new Integer(swapNode));
		if(nodesImproved == nodesToImprove && !applyToAllNodes) {
		    break;
		}
	    }
	    
	    allNodesIndicesCopy.remove(new Integer(node1));
	}
	//chromosome.verifyFitness();
    }
    
    private void swapNodes(int node1, int node2, double swapImprovement, CCPChromosome chromosome) throws Exception {
	double[] nodeWeight = new double[2];
	double[] futureClustersWeight = new double[2];
	int[] clusterOfNodes = {0,0};
	
	clusterOfNodes[0] = chromosome.getCodification()[node1];
	clusterOfNodes[1] = chromosome.getCodification()[node2];
	nodeWeight[0] = instance.getNodeWeights()[node1];
	nodeWeight[1] = instance.getNodeWeights()[node2];
	futureClustersWeight[0] = chromosome.getCurrentClustersWeight().get(clusterOfNodes[0]) - nodeWeight[0] + nodeWeight[1];
	futureClustersWeight[1] = chromosome.getCurrentClustersWeight().get(clusterOfNodes[1]) + nodeWeight[0] - nodeWeight[1];
	
	chromosome.getCodification()[node1] = clusterOfNodes[1];
	chromosome.getCodification()[node2] = clusterOfNodes[0];
	chromosome.addToFitness(swapImprovement);
	chromosome.getCurrentClustersWeight().set(clusterOfNodes[0], futureClustersWeight[0]);
	chromosome.getCurrentClustersWeight().set(clusterOfNodes[1], futureClustersWeight[1]);
	swapNodesByCluster(node1, node2, clusterOfNodes[0], clusterOfNodes[1], chromosome);
    }
    
    private void swapNodesByCluster(Integer node1, Integer node2, int clusterNode1, int clusterNode2, CCPChromosome chromosome) {
	chromosome.getNodesByCluster().get(clusterNode1).remove(node1);
	chromosome.getNodesByCluster().get(clusterNode2).remove(node2);
	chromosome.getNodesByCluster().get(clusterNode1).add(node2);
	chromosome.getNodesByCluster().get(clusterNode2).add(node1);
    }
    
    private double computeSwapImprovement(int node1, int node2, CCPChromosome chromosome) {
	double swapImprovement = 0.0;
	int[] clusterOfNodes = {0,0};
	double[] originalContribution = {0.0, 0.0};
	double[] newContribution = {0.0, 0.0};
	
	try {
	    clusterOfNodes[0] = chromosome.getCodification()[node1];
	    clusterOfNodes[1] = chromosome.getCodification()[node2];

	    if(canSwapNodes(node1, node2, chromosome, clusterOfNodes)) {
		originalContribution[0] = chromosome.computeNodeContributionInCluster(node1);
		originalContribution[1] = chromosome.computeNodeContributionInCluster(node2);
		chromosome.getCodification()[node1] = clusterOfNodes[1];
		chromosome.getCodification()[node2] = clusterOfNodes[0];
		swapNodesByCluster(node1, node2, clusterOfNodes[0], clusterOfNodes[1], chromosome);
		newContribution[0] = chromosome.computeNodeContributionInCluster(node1);
		newContribution[1] = chromosome.computeNodeContributionInCluster(node2);
		chromosome.getCodification()[node1] = clusterOfNodes[0];
		chromosome.getCodification()[node2] = clusterOfNodes[1];
		swapNodesByCluster(node2, node1, clusterOfNodes[0], clusterOfNodes[1], chromosome);
		swapImprovement = ((originalContribution[0] + originalContribution[1]) * -1) + newContribution[0] + newContribution[1]; 
	    }
	    
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	//chromosome.verifyFitness();
	return swapImprovement;
    }
    
    private boolean canSwapNodes(int node1, int node2, CCPChromosome chromosome, int[] clusterOfNodes) throws Exception {
	double[] nodeWeight = new double[2];
	double[] futureClustersWeight = new double[2];
	
	nodeWeight[0] = instance.getNodeWeights()[node1];
	nodeWeight[1] = instance.getNodeWeights()[node2];
	
	futureClustersWeight[0] = chromosome.getCurrentClustersWeight().get(clusterOfNodes[0]) - nodeWeight[0] + nodeWeight[1];
	futureClustersWeight[1] = chromosome.getCurrentClustersWeight().get(clusterOfNodes[1]) + nodeWeight[0] - nodeWeight[1];
	
	return Common.isClusterWithinWeights(clusterOfNodes[0], futureClustersWeight[0], instance) &&
	       Common.isClusterWithinWeights(clusterOfNodes[1], futureClustersWeight[1], instance) &&
	       clusterOfNodes[0] != clusterOfNodes[1];
    }
}
