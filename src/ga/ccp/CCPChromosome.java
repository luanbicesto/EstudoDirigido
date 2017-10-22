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

		for (int i = 0; i < nodesByCluster.size(); i++) {
			clone.add(i, new ArrayList<Integer>(this.nodesByCluster.get(i)));
		}

		return clone;
	}

	public void updateClusterControls(int node) throws Exception {
		int clusterId = 0;
		double currentClusterWeight;
		
		clusterId = this.codification[node];
		currentClusterWeight = this.clustersCurrentWeight.get(clusterId);
		this.nodesByCluster.get(clusterId).add(new Integer(node));
		this.clustersCurrentWeight.set(clusterId, currentClusterWeight + instance.getNodeWeights()[node]);
	}
	
	public void removeNodeFromCluster(int locus, boolean logicalRemoval) throws Exception {
		double nodeContribution = 0.0;
		double currentClusterWeight = 0.0;
		int clusterId = 0;

		clusterId = this.codification[locus];
		if (!logicalRemoval) {
			nodeContribution = computeNodeContributionInCluster(locus);
		}

		currentClusterWeight = this.clustersCurrentWeight.get(clusterId);
		this.nodesByCluster.get(clusterId).remove(new Integer(locus));
		this.clustersCurrentWeight.set(clusterId, currentClusterWeight - instance.getNodeWeights()[locus]);

		if (!logicalRemoval) {
			this.fitness = this.fitness - nodeContribution;
			this.codification[locus] = -1;
		}
	}

	public boolean setAllele(int locus, Integer targetClusterId) throws Exception {
		return setAllele(locus, targetClusterId, true);
	}

	public boolean setAllele(int locus, Integer targetClusterId, boolean shouldSetFitness) throws Exception {
		int originalClusterId = 0;
		double targetClusterWeight = 0.0;
		double originalClusterFutureWeight = 0.0;

		originalClusterId = this.getCodification()[locus];
		originalClusterFutureWeight = this.clustersCurrentWeight.get(originalClusterId)
				- this.instance.getNodeWeights()[locus];
		targetClusterWeight = this.clustersCurrentWeight.get(targetClusterId) + this.instance.getNodeWeights()[locus];
		if (originalClusterId != targetClusterId
				&& !Common.isClusterOverWeighted(targetClusterId, targetClusterWeight, instance)
				&& !Common.isClusterUnderWeighted(originalClusterId, originalClusterFutureWeight, instance)) {
			double previousContribution = 0.0;
			double currentContribution = 0.0;

			// Update fitness and adjust nodes by cluster
			if (shouldSetFitness) {
				previousContribution = computeNodeContributionInCluster(locus);
			}

			this.getCodification()[locus] = targetClusterId;
			this.nodesByCluster.get(originalClusterId).remove(new Integer(locus));
			this.nodesByCluster.get(targetClusterId).add(new Integer(locus));

			if (shouldSetFitness) {
				currentContribution = computeNodeContributionInCluster(locus);
				this.fitness = this.fitness - previousContribution + currentContribution;
			}

			// Update weights of the clusters
			this.clustersCurrentWeight.set(originalClusterId, originalClusterFutureWeight);
			this.clustersCurrentWeight.set(targetClusterId, targetClusterWeight);
			return true;
		}

		return false;
	}

	public void moveNodeCluster(int locus, int targetClusterId) {
		double currentContribution = 0.0;
		double targetClusterWeight = 0.0;

		try {
			targetClusterWeight = this.clustersCurrentWeight.get(targetClusterId)
					+ this.instance.getNodeWeights()[locus];
			this.getCodification()[locus] = targetClusterId;
			this.nodesByCluster.get(targetClusterId).add(new Integer(locus));
			currentContribution = computeNodeContributionInCluster(locus);
			this.fitness += currentContribution;
			this.clustersCurrentWeight.set(targetClusterId, targetClusterWeight);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public boolean canSetAllele(int locus, Integer targetClusterId) throws Exception {
		int originalClusterId = 0;
		double targetClusterWeight = 0.0;
		double originalClusterFutureWeight = 0.0;

		originalClusterId = this.getCodification()[locus];
		originalClusterFutureWeight = this.clustersCurrentWeight.get(originalClusterId)
				- this.instance.getNodeWeights()[locus];
		targetClusterWeight = this.clustersCurrentWeight.get(targetClusterId) + this.instance.getNodeWeights()[locus];

		return originalClusterId != targetClusterId
				&& !Common.isClusterOverWeighted(targetClusterId, targetClusterWeight, instance)
				&& !Common.isClusterUnderWeighted(originalClusterId, originalClusterFutureWeight, instance);
	}

	public void initialize(ArrayList<ArrayList<Integer>> nodesByCluster, ArrayList<Double> clustersCurrentWeight)
			throws Exception {
		double fitness = 0.0;
		for (int i = 0; i < nodesByCluster.size(); i++) {
			ArrayList<Integer> nodesClusterList = nodesByCluster.get(i);
			fitness += computeCusterContribution(nodesClusterList);
		}

		this.fitness = fitness;
		this.nodesByCluster = nodesByCluster;
		this.clustersCurrentWeight = clustersCurrentWeight;
	}

	public double computeNodeContributionInCluster(int locus) throws Exception {
		int nodeClusterId = 0;
		ArrayList<Integer> nodesCluster = null;
		double nodeContribution = 0.0;

		nodeClusterId = this.getCodification()[locus];
		nodesCluster = this.nodesByCluster.get(nodeClusterId);

		for (int i = 0; i < nodesCluster.size(); i++) {
			if (nodesCluster.get(i) != locus) {
				nodeContribution += getEdgeValue(locus, nodesCluster.get(i));
			}
		}

		return nodeContribution;
	}

	public void setFitness() {
		double fitness = computeFitness(false);
		this.fitness = fitness;
	}

	public void addToFitness(double improvement) {
		this.fitness += improvement;
	}

	private double computeFitness(boolean computeFromScratch) {
		double fitness = 0.0;
		for (int k = 0; k < instance.getK(); k++) {
			fitness += computeClusterContribution(k, computeFromScratch);
		}

		return fitness;
	}

	private double computeClusterContribution(int clusterId, boolean computeFromScratch) {
		ArrayList<Integer> clusterNodes;

		clusterNodes = getNodesOfCluster(clusterId, computeFromScratch);
		printClusterWeight(clusterId, clusterNodes);
		return computeCusterContribution(clusterNodes);
	}

	private void printClusterWeight(int clusterId, ArrayList<Integer> clusterNodes) {
		double totalWeight = 0.0;
		int nodeId = 0;

		for (int i = 0; i < clusterNodes.size(); i++) {
			nodeId = clusterNodes.get(i);
			try {
				totalWeight += instance.getNodeWeights()[nodeId];
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("Node weight: " + clusterId + " -> " + totalWeight);
	}

	private double computeCusterContribution(ArrayList<Integer> clusterNodes) {
		double clusterContribution = 0.0;

		for (int i = 0; i < clusterNodes.size(); i++) {
			for (int j = i + 1; j < clusterNodes.size(); j++) {
				try {
					clusterContribution += getEdgeValue(clusterNodes.get(i), clusterNodes.get(j));
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

	private double getEdgeValue(int i, int j) {
		try {
			return instance.getEdgeWeights()[i][j];
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;
	}

	public void verifyFitness() {
		double fitness = 0.0;

		fitness = computeFitness(true);
		System.out.println("Current fitness: " + this.fitness + " | Calculated: " + fitness);
		System.out.println("Clusters weights are valid: " + isClustersWeightsValid());
		System.out.println(clustersCurrentWeight.toString());
	}

	private boolean isClustersWeightsValid() {
		boolean validClustersWeights = true;

		for (int i = 0; i < this.clustersCurrentWeight.size(); i++) {
			try {
				if (Common.isClusterOverWeighted(i, clustersCurrentWeight.get(i), this.instance)
						|| Common.isClusterUnderWeighted(i, clustersCurrentWeight.get(i), this.instance)) {
					validClustersWeights = false;
					break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return validClustersWeights;
	}

}
