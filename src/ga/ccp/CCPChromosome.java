package ga.ccp;

import java.util.ArrayList;
import java.util.Set;

import ga.framework.Chromosome;

public class CCPChromosome extends Chromosome {
    private Integer[] codification;
    private double fitness;
    private double[][] edgeWeights;

    public CCPChromosome(int qtyNodes, double[][] edgeWeights) {
	codification = new Integer[qtyNodes];
	this.edgeWeights = edgeWeights;
    }
    
    public Integer[] getCodification() {
        return codification;
    }
    
    @Override
    public double getFitness() {
	return fitness;
    }
    
    public void setFitness(ArrayList<Set<Integer>> nodesByCluster) {
	double fitness = 0.0;
	for(int i = 0; i < nodesByCluster.size(); i++) {
	    Set<Integer> nodesCluster = nodesByCluster.get(i);
	    ArrayList<Integer> nodesClusterList = new ArrayList<>(nodesCluster);
	    
	    for(int j = 0; j < nodesClusterList.size(); j++) {
		for(int k = j+1; k < nodesClusterList.size(); k++) {
		    fitness += edgeWeights[nodesClusterList.get(j)][nodesClusterList.get(k)];
		}
	    }
	}
	
	this.fitness = fitness;
    }

}
