package ilp.gurobi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import common.instance.reader.CCPInstanceEntity;
import ga.ccp.CCPChromosome;
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
		HashMap<Integer, Integer> hash = new HashMap<>();
		ArrayList<Integer> clusters;
		double[][] benefits;
		
		clusters = new ArrayList<>(currentMapping.keySet());
		Collections.sort(clusters);
		benefits = computeBenefits(clusters, currentMapping, chromosome);
		
		return hash;
	}
	
	private HashMap<Integer, Integer> solveLinearProgram(double[][] benefits, ArrayList<Integer> clusters) throws GRBException {
		model = new GRBModel(env);
		createVars(clusters);
		addObjectiveFunction(clusters, benefits);
		addOnlyOneTargetClusterRestriction(clusters);
		model.optimize();
		
		return extractNewMatch(model);
	}
	
	private HashMap<Integer, Integer> extractNewMatch(GRBModel model) {
		HashMap<Integer, Integer> hash = new HashMap<>();
		
		return hash;
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
}
