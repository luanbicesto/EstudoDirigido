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
    
    private CCPChromosome(CCPChromosome source) {
	this(source.instance);
	this.codification = source.codification;
	this.fitness = source.getFitness();
	this.nodesByCluster = source.getNodesByCluster();
	this.clustersCurrentWeight = source.getCurrentClustersWeight();
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
	CCPChromosome clone = new CCPChromosome(this);
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
		    fitness += this.instance.getEdgeWeights()[nodesClusterList.get(j)][nodesClusterList.get(k)];
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
		nodeContribution += this.instance.getEdgeWeights()[locus][nodesCluster.get(i)];
	    }
	}
	
	return nodeContribution;
    }

}
