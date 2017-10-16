package ga.ccp;

import java.util.ArrayList;
import java.util.Collections;
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
	case TripleSwap:
	    //applyNSwap(chromosome, 3);
	    applyNSwapTimes(chromosome, CCPParameters.NUMBER_TRIPLE_SWAP_EXECUTIONS, 3);
	    break;
	case QuadrupleSwap:
	    applyNSwapTimes(chromosome, CCPParameters.NUMBER_QUADRUPLE_SWAP_EXECUTIONS, 4);
	    break;

	default:
	    break;
	}
    }
    
    private void applyNSwapTimes(CCPChromosome chromosome, int times, int n) {
	for(int i = 0; i < times; i ++) {
	    applyNSwap(chromosome, n);
	}
    }
    
    private void applyNSwap(CCPChromosome chromosome, int n) {
	ArrayList<Integer> clusters = new ArrayList<>(n);
	ArrayList<Integer> nodes = new ArrayList<>(n);
	ArrayList<Integer> nodesOfCluster = null;
	ArrayList<Integer> clustersIds = new ArrayList<>(this.allClustersIndices);
	int nodeIndex = -1;
	int selectedNode = 0;
	double currentContribution = 0.0;
	
	//select clusters
	for(int i = 0; i < n; i++) {
	    int cluster = clustersIds.get(rng.nextInt(clustersIds.size()));
	    clusters.add(cluster);
	    clustersIds.remove(new Integer(cluster));
	}
	
	//sort clusters
	//Collections.sort(clusters);
	
	//select one node from each cluster
	for(int i = 0; i < clusters.size(); i++) {
	    nodesOfCluster = chromosome.getNodesByCluster().get(clusters.get(i));
	    if(nodesOfCluster.size() == 0) {
		return;
	    }
	    nodeIndex = rng.nextInt(nodesOfCluster.size());
	    selectedNode = nodesOfCluster.get(nodeIndex);
	    nodes.add(selectedNode);
	}
	
	//compute current contribution
	for(int i = 0; i < nodes.size(); i++) {
	    try {
		currentContribution += chromosome.computeNodeContributionInCluster(nodes.get(i));
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	
	//Identify the best N swap move
	double bestConfigurationContribution = 0.0;
	double configurationContribution = 0.0;
	int bestSteps = 0;
	int bestType = -1;
	removeNodesFromCluster(chromosome, nodes);
	for(int steps = 1; steps <= n-1; steps++) {
	    for(int type = 0; type < 2; type++) {
		if(validNSwapStep(steps, type, chromosome, nodes, clusters)) {
		    try {
			configurationContribution = computeNSwapContribution(steps, type, chromosome, nodes, clusters);
			if(configurationContribution > bestConfigurationContribution) {
			    bestConfigurationContribution = configurationContribution;
			    bestSteps = steps;
			    bestType = type;
			}
		    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
	    }
	}
	
	//Apply swap
	applyNSwapMove(chromosome, nodes, bestSteps, bestType,
		    bestConfigurationContribution, currentContribution, clusters);
	
    }
    
    private void applyNSwapMove(CCPChromosome chromosome, ArrayList<Integer> nodes, int bestSteps, int bestType,
	    double bestConfigurationContribution, double currentContribution, ArrayList<Integer> clusters) {
	double swapContribution = 0.0;
	int futureClusterIndex = -1;
	int futureCluster = 0;

	swapContribution = bestConfigurationContribution - currentContribution;
	for (int i = 0; i < nodes.size(); i++) {
	    if (swapContribution > 0) {
		futureClusterIndex = computeFutureClusterIndexOfNodeNSwap(bestSteps, bestType, i, clusters);
		futureCluster = clusters.get(futureClusterIndex);
	    } else {
		futureCluster = clusters.get(i); // if the move is not valid
						 // then return the node to its
						 // original cluster
	    }

	    try {
		chromosome.moveNodeCluster(nodes.get(i), futureCluster);
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
    
    private double computeNSwapContribution(int steps, int type, CCPChromosome chromosome, ArrayList<Integer> nodes, ArrayList<Integer> clusters) throws Exception {
	double nSwapContribution = 0.0;
	int futureClusterIndex = -1;
	int futureCluster = 0;
	
	for (int i = 0; i < nodes.size(); i++) {
	    futureClusterIndex = computeFutureClusterIndexOfNodeNSwap(steps, type, i, clusters);
	    futureCluster = clusters.get(futureClusterIndex);
	    nSwapContribution += computeNodeContributionInCluster(nodes.get(i), futureCluster, chromosome);
	}
	
	return nSwapContribution;
    }
    
    private void removeNodesFromCluster(CCPChromosome chromosome, ArrayList<Integer> nodes) {
	// remove nodes from original clusters
	for (int i = 0; i < nodes.size(); i++) {
	    try {
		chromosome.removeNodeFromCluster(nodes.get(i), true);
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
    
    private boolean validNSwapStep(int steps, int type, CCPChromosome chromosome, ArrayList<Integer> nodes, ArrayList<Integer> clusters) {
	int futureClusterIndex = -1;
	int futureCluster = 0;
	boolean validSwapMovent = true;
	
	try {
	    //verifies with the configuration is valid
	    for (int i = 0; i < nodes.size(); i++) {
		futureClusterIndex = computeFutureClusterIndexOfNodeNSwap(steps, type, i, clusters);
		futureCluster = clusters.get(futureClusterIndex);
		if(!canMigrateToCluster(nodes.get(i), futureCluster, chromosome, false)) {
		    validSwapMovent = false;
		    break;
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	return validSwapMovent;
    }
    
    private int computeFutureClusterIndexOfNodeNSwap(int steps, int type, int nodeIndex, ArrayList<Integer> clusters) {
	int futureClusterIndex = -1;
	
	if(type == 0) { //right direction
	    futureClusterIndex = (nodeIndex + steps) % clusters.size();
	} else { //left direction
	    futureClusterIndex = nodeIndex - steps;
	    if(futureClusterIndex < 0) {
		futureClusterIndex += clusters.size();
	    }
	}
	
	return futureClusterIndex;
    }
    
    private void applyOneChange(CCPChromosome chromosome, boolean applyToAllNodes) throws Exception {
	ArrayList<Integer> allNodesIndicesCopy = new ArrayList<>(allNodesIndices);
	ArrayList<Integer> allClustersIndicesCopy;
	int node, cluster;
	int indexNode = 0, indexCluster = 0;
	int originalClusterId = 0;
	double currentNodeContributionCluster = 0.0;
	double migrateNodeContributionCluster = 0.0;
	double bestContribution = 0.0;
	int nodesToImprove = getNodesToImprove(LocalSearchStrategy.OneChange);
	int nodesImproved = 0;
	int targetCluster = -1;
	boolean nodeImproved = false;
	
	while(allNodesIndicesCopy.size() > 0) {
	    indexNode = rng.nextInt(allNodesIndicesCopy.size());
	    node = allNodesIndicesCopy.get(indexNode);
	    originalClusterId = chromosome.getCodification()[node];
	    currentNodeContributionCluster = chromosome.computeNodeContributionInCluster(node);
	    allClustersIndicesCopy = new ArrayList<>(allClustersIndices);
	    bestContribution = 0.0;
	    nodeImproved = false;
	    targetCluster = -1;
	    
	    while(!allClustersIndicesCopy.isEmpty()) {
		indexCluster = rng.nextInt(allClustersIndicesCopy.size());
		cluster = allClustersIndicesCopy.get(indexCluster);
		if(cluster != originalClusterId) {
		    migrateNodeContributionCluster = computeOneChangeImprovement(node, cluster, chromosome);
		    double fitnessDifference = migrateNodeContributionCluster - currentNodeContributionCluster;
		    if(fitnessDifference > bestContribution) {
			bestContribution = fitnessDifference;
			targetCluster = cluster;
			nodeImproved = true;
			if(!CCPParameters.BEST_IMPROVING) {
			    break;
			}
		    }
		}
		allClustersIndicesCopy.remove(new Integer(cluster));
	    }
	    
	    if(nodeImproved) {
		migrateNode(node, targetCluster, chromosome, bestContribution);
		nodesImproved++;
	    }
	    
	    if((nodesImproved == nodesToImprove && !applyToAllNodes) || CCPParameters.ONE_NODE_OPTION) {
		break;
	    }
	    
	    allNodesIndicesCopy.remove(new Integer(node));
	}
    }
    
    private int getNodesToImprove(LocalSearchStrategy strategy) {
	double percentageNodes = 0.0;
	switch (strategy) {
	case OneChange:
	    percentageNodes = CCPParameters.MAX_PERCENTAGE_NUMBER_NODES_LS_ONE_CHANGE;
	    break;
	case Swap:
	    percentageNodes = CCPParameters.MAX_PERCENTAGE_NUMBER_NODES_LS_SWAP;
	    break;

	default:
	    break;
	}
	return (int)Math.round((instance.getN() * percentageNodes));
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
    
    private double computeNodeContributionInCluster(int node, int cluster, CCPChromosome chromosome) throws Exception {
	double improvement = 0.0;
	int originalCluster = 0;

	originalCluster = chromosome.getCodification()[node];
	chromosome.getCodification()[node] = cluster;
	chromosome.getNodesByCluster().get(cluster).add(new Integer(node));
	improvement = chromosome.computeNodeContributionInCluster(node);
	chromosome.getNodesByCluster().get(cluster).remove(new Integer(node));
	chromosome.getCodification()[node] = originalCluster;

	return improvement;
    }
    
    private boolean canMigrateToCluster(int node, int targetCluster, CCPChromosome chromosome) {
	return canMigrateToCluster(node, targetCluster, chromosome, true);
    }
    
    private boolean canMigrateToCluster(int node, int targetCluster, CCPChromosome chromosome, boolean nodeInCluster) {
	double targetClusterFutureWeight = 0.0;
	double originalClusterFutureWeight = 0.0;
	int originalCluster = 0;
	double nodeWeight = 0.0;
	
	try {
	    if(nodeInCluster) {
		originalCluster = chromosome.getCodification()[node];
	    }
	    
	    targetClusterFutureWeight = chromosome.getCurrentClustersWeight().get(targetCluster) + instance.getNodeWeights()[node];
	    
	    nodeWeight = nodeInCluster ? instance.getNodeWeights()[node] : 0;
	    originalClusterFutureWeight = chromosome.getCurrentClustersWeight().get(originalCluster) - nodeWeight;
	    
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
	int nodesToImprove = getNodesToImprove(LocalSearchStrategy.Swap);
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
		    if(swapImprovement > bestSwapImprovement) {
			swapNode = node2;
			bestSwapImprovement = swapImprovement;
			if(!CCPParameters.BEST_IMPROVING) {
			    break;
			}
		    }
		}
	    }
	    
	    if(swapNode != -1) {
		swapNodes(node1, swapNode, bestSwapImprovement, chromosome);
		allNodesIndicesCopy.remove(new Integer(swapNode));
		nodesImproved++;
		if((nodesImproved == nodesToImprove && !applyToAllNodes) || CCPParameters.ONE_NODE_OPTION) {
		    break;
		}
	    }
	    
	    allNodesIndicesCopy.remove(new Integer(node1));
	}
	//chromosome.verifyFitness();
    }
    
    public void swapNodes(int node1, int node2, double swapImprovement, CCPChromosome chromosome) throws Exception {
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
    
    public double computeSwapImprovement(int node1, int node2, CCPChromosome chromosome) {
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
    
    public boolean canSwapNodes(int node1, int node2, CCPChromosome chromosome, int[] clusterOfNodes) throws Exception {
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
