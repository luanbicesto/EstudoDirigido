package ga.ccp;

import java.util.ArrayList;

import common.instance.reader.CCPInstanceEntity;
import ga.framework.Chromosome;

public class CCPChromosome extends Chromosome {
    private Integer[] codification;
    private double fitness;
    private ArrayList<ArrayList<Integer>> nodesByCluster;
    private ArrayList<Double> clustersCurrentWeight;
    private CCPInstanceEntity instance;

    public CCPChromosome(CCPInstanceEntity instance) {
	codification = new Integer[instance.getN()];
	this.instance = instance;
    }
    
    public Integer[] getCodification() {
        return codification;
    }
    
    @Override
    public double getFitness() {
	return fitness;
    }
    
    public ArrayList<ArrayList<Integer>> getNodesByCluster() {
        return nodesByCluster;
    }
    
    public ArrayList<Double> getCurrentClustersWeight() {
        return this.clustersCurrentWeight;
    }

    public CCPChromosome clone() {
	CCPChromosome clone = new CCPChromosome(this.instance);
	clone.codification = this.codification.clone();
	clone.fitness = this.fitness;
	clone.nodesByCluster = cloneNodesByCluster();
	clone.clustersCurrentWeight = new ArrayList<>(this.clustersCurrentWeight);
	return clone;
    }
    
    private ArrayList<ArrayList<Integer>> cloneNodesByCluster() {
	ArrayList<ArrayList<Integer>> clone = new ArrayList<>(this.nodesByCluster.size());
	
	for(int i = 0; i < nodesByCluster.size(); i++) {
	    clone.add(i, new ArrayList<Integer>(this.nodesByCluster.get(i)));
	}
	
	return clone;
    }
    
    public void setAllele(int locus, Integer targetClusterId) throws Exception {
	int originalClusterId = 0;
	double targetClusterWeight = 0.0;
	double originalClusterFutureWeight = 0.0;
	
	originalClusterId = this.getCodification()[locus];
	originalClusterFutureWeight = this.clustersCurrentWeight.get(originalClusterId) - this.instance.getNodeWeights()[locus];
	targetClusterWeight = this.clustersCurrentWeight.get(targetClusterId) + this.instance.getNodeWeights()[locus];
	if(originalClusterId != targetClusterId &&
	   !Common.isClusterOverWeighted(targetClusterId, targetClusterWeight, instance) &&
	   !Common.isClusterUnderWeighted(originalClusterId, originalClusterFutureWeight, instance)) {
	    double previousContribution = 0.0;
	    double currentContribution = 0.0;
	    
	    //Update fitness and adjust nodes by cluster
	    previousContribution = computeNodeContributionInCluster(locus);
	    this.getCodification()[locus] = targetClusterId;
	    this.nodesByCluster.get(originalClusterId).remove(new Integer(locus));
	    this.nodesByCluster.get(targetClusterId).add(new Integer(locus));
	    currentContribution = computeNodeContributionInCluster(locus);
	    this.fitness = this.fitness - previousContribution + currentContribution;
	    
	    //Update weights of the clusters
	    this.clustersCurrentWeight.set(originalClusterId, originalClusterFutureWeight);
	    this.clustersCurrentWeight.set(targetClusterId, targetClusterWeight);
	}
    }
    
    public void initialize(ArrayList<ArrayList<Integer>> nodesByCluster, ArrayList<Double> clustersCurrentWeight) throws Exception {
	double fitness = 0.0;
	for(int i = 0; i < nodesByCluster.size(); i++) {
	    ArrayList<Integer> nodesClusterList = nodesByCluster.get(i);
	    
	    for(int j = 0; j < nodesClusterList.size(); j++) {
		for(int k = j+1; k < nodesClusterList.size(); k++) {
		    fitness += Math.round(this.instance.getEdgeWeights()[nodesClusterList.get(j)][nodesClusterList.get(k)]);
		}
	    }
	}
	
	this.fitness = fitness;
	this.nodesByCluster = nodesByCluster;
	this.clustersCurrentWeight = clustersCurrentWeight;
    }
    
    private double computeNodeContributionInCluster(int locus) throws Exception {
	int nodeClusterId = 0;
	ArrayList<Integer> nodesCluster = null;
	double nodeContribution = 0.0;
	
	nodeClusterId = this.getCodification()[locus];
	nodesCluster = this.nodesByCluster.get(nodeClusterId);
	
	for(int i = 0; i < nodesCluster.size(); i++) {
	    if(nodesCluster.get(i) != locus) {
		nodeContribution += Math.round(this.instance.getEdgeWeights()[locus][nodesCluster.get(i)]);
	    }
	}
	
	return nodeContribution;
    }
    
    public void setFitness() {
	double fitness = computeFitness(false);
	this.fitness = fitness;
    }

    public void verifyFitness() {
	double fitness = 0.0;
	
	fitness = computeFitness(true);
	System.out.println("Current fitness: " + this.fitness + " | Calculated: " + fitness);
    }
    
    
    private double computeFitness(boolean computeFromScratch) {
	double fitness = 0.0;
	for(int k = 0; k < instance.getK(); k++) {
	    fitness += computeClusterContribution(k, computeFromScratch);
	}
	
	return fitness;
    }
    
    private double computeClusterContribution(int clusterId, boolean computeFromScratch) {
	ArrayList<Integer> clusterNodes;
	double clusterContribution = 0.0;
	
	clusterNodes = getNodesOfCluster(clusterId, computeFromScratch);
	for(int i = 0; i < clusterNodes.size(); i++) {
	    for(int j = i + 1; j < clusterNodes.size(); j++) {
		try {
		    clusterContribution += Math.round(instance.getEdgeWeights()[clusterNodes.get(i)][clusterNodes.get(j)]);
		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}
	
	return clusterContribution;
    }
    
    private ArrayList<Integer> getNodesOfCluster(int clusterId, boolean computeFromScratch) {
	ArrayList<Integer> nodes = new ArrayList<>();

	if (computeFromScratch) {
	    for (int i = 0; i < instance.getN(); i++) {
		if (this.getCodification()[i] == clusterId) {
		    nodes.add(i);
		}
	    }
	} else {
	    nodes = nodesByCluster.get(clusterId);
	}

	return nodes;
    }
    
}