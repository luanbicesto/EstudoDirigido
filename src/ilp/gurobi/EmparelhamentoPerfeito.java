package ilp.gurobi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;

import common.instance.reader.CCPInstanceEntity;
import ga.ccp.CCPChromosome;
import ga.ccp.CCPParameters;
import ga.ccp.LocalSearchWrapper;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class EmparelhamentoPerfeito {
	private LocalSearchWrapper localSearch;
	private CCPInstanceEntity instance;
	private GRBEnv env;
    private GRBModel model;
    private GRBVar[][] modelVars;
	
	public EmparelhamentoPerfeito(CCPInstanceEntity instance, Random rng) {
		localSearch = new LocalSearchWrapper(instance, rng);
		this.instance = instance;
		try {
			this.env = new GRBEnv("perfectMatch.log");
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
	}
	
	public HashMap<Integer, Integer> computePerfectMatch(HashMap<Integer, Integer> currentMapping, CCPChromosome chromosome) throws Exception {
		ArrayList<Integer> clusters;
		double[][] benefits;
		
		clusters = new ArrayList<>(currentMapping.keySet());
		//Collections.sort(clusters);
		benefits = computeBenefits(clusters, currentMapping, chromosome);
		return solveLinearProgram(benefits, clusters, currentMapping);
		
	}
	
	private HashMap<Integer, Integer> solveLinearProgram(double[][] benefits, ArrayList<Integer> clusters, HashMap<Integer, Integer> currentMapping) throws GRBException {
		model = new GRBModel(env);
		model.set("OutputFlag", "0");
		createVars(clusters);
		addObjectiveFunction(clusters, benefits);
		addOnlyOneTargetClusterRestriction(clusters);
		addOnlyOneTargetClusterRestriction2(clusters);
		model.optimize();
		
		return extractNewMatch(model, currentMapping);
	}
	
	private HashMap<Integer, Integer> extractNewMatch(GRBModel model, HashMap<Integer, Integer> currentMapping) throws GRBException {
		HashMap<Integer, Integer> newMatch = new HashMap<>();
		ArrayList<Integer> clusters = null;
		
		clusters = new ArrayList<>(currentMapping.keySet());
		for(int i = 0; i < clusters.size(); i++) {
			for(int j = 0; j < clusters.size(); j++) {
				if(modelVars[i][j].get(GRB.DoubleAttr.X) > 0.95) {
                	int sourceNode = currentMapping.get(clusters.get(i));
                	newMatch.put(clusters.get(j), sourceNode);
                }
			}
		}
		
		return newMatch;
	}
	
	private void addObjectiveFunction(ArrayList<Integer> clusters, double[][] benefits) throws GRBException {
		GRBLinExpr expr = new GRBLinExpr();;
		
		for(int i = 0; i < clusters.size(); i++) {
			for(int j = 0; j < clusters.size(); j++) {
				expr.addTerm(benefits[i][j], modelVars[i][j]);
			}
		}
		model.setObjective(expr, GRB.MAXIMIZE);
	}
	
	private void addOnlyOneTargetClusterRestriction(ArrayList<Integer> clusters) throws GRBException {
		GRBLinExpr expr;
		
		for(int j = 0; j < clusters.size(); j++) {
			expr = new GRBLinExpr();
			for(int i = 0; i < clusters.size(); i++) {
				expr.addTerm(1.0, modelVars[i][j]);
			}
			model.addConstr(expr, GRB.EQUAL, 1.0, "c" + Integer.toString(j));
		}
	}
	
	private void addOnlyOneTargetClusterRestriction2(ArrayList<Integer> clusters) throws GRBException {
		GRBLinExpr expr;
		
		for(int i = 0; i < clusters.size(); i++) {
			expr = new GRBLinExpr();
			for(int j = 0; j < clusters.size(); j++) {
				expr.addTerm(1.0, modelVars[i][j]);
			}
			model.addConstr(expr, GRB.EQUAL, 1.0, "c" + Integer.toString(i));
		}
	}
	
	private void createVars(ArrayList<Integer> clusters) throws GRBException {
		modelVars = new GRBVar[clusters.size()][clusters.size()];
		for(int i = 0; i < clusters.size(); i++) {
			for(int j = 0; j < clusters.size(); j++) {
				modelVars[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "x" + Integer.toString(i) + "_" + Integer.toString(j));
			}
		}
	}
	
	private double[][] computeBenefits(ArrayList<Integer> clusters, HashMap<Integer, Integer> currentMapping
			                         , CCPChromosome chromosome) throws Exception {
		double[][] benefits = new double[clusters.size()][clusters.size()];
		double benefit = 0.0;
		double currentNodeContribution = 0.0;
		double targetClusterNodeContribution = 0.0;
		
		for(int i = 0; i < clusters.size(); i++) {
			for(int j = 0; j < clusters.size(); j++) {
				if(j == i) {
					benefits[i][j] = 0.0;
				} else {
					int sourceNode = currentMapping.get(clusters.get(i));
					int targetNode = currentMapping.get(clusters.get(j));
					
					if(canMigrateToCluster(sourceNode, targetNode, clusters.get(j), chromosome)) {
						currentNodeContribution = chromosome.computeNodeContributionInCluster(sourceNode);
						targetClusterNodeContribution = localSearch.computeOneChangeImprovement(sourceNode, clusters.get(j), chromosome);
						benefit = targetClusterNodeContribution - currentNodeContribution - instance.getEdgeWeights()[sourceNode][targetNode];
					} else {
						benefit = -Double.MAX_VALUE;
					}
					
					benefits[i][j] = benefit;
				}
			}
		}
		
		return benefits;
	}
	
	private boolean canMigrateToCluster(int sourceNode, int targetNode, int targetCluster, CCPChromosome chromosome) throws Exception {
		boolean canMigrate = false;
		
		chromosome.removeNodeFromCluster(targetNode, true);
		canMigrate = localSearch.canMigrateToCluster(sourceNode, targetCluster, chromosome);
		chromosome.updateClusterControls(targetNode);
		
		return canMigrate;
	}
	
	public void applyPerfectMatch(CCPChromosome chromosome) {
		HashMap<Integer, Integer> clusterMapping;
		HashMap<Integer, Integer> newClusterMapping;
		
		clusterMapping = localSearch.selectNodeCluster(CCPParameters.PERFECT_MATCH_NUMBER_CLUSTERS, chromosome);
		try {
			newClusterMapping = computePerfectMatch(clusterMapping, chromosome);
			applyNewNodesMappingPerfectMatch(newClusterMapping, chromosome);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void applyNewNodesMappingPerfectMatch(HashMap<Integer, Integer> newClusterMapping, CCPChromosome chromosome) throws Exception {
		ArrayList<Integer> nodes;
		
		nodes = new ArrayList<Integer>(newClusterMapping.values());
		for(int i = 0; i < nodes.size(); i++) {
			chromosome.removeNodeFromCluster(nodes.get(i), false);
		}
		
		for(Entry<Integer, Integer> entry : newClusterMapping.entrySet()) {
			Integer node = entry.getValue();
			Integer cluster = entry.getKey();
			chromosome.moveNodeCluster(node, cluster);
		}
	}
}
