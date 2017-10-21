package ilp.gurobi;

import common.instance.reader.CCPInstanceEntity;
import common.instance.reader.InstanceReader;
import ga.ccp.CCPChromosome;
import ga.ccp.GA_CCP;
import gurobi.GRBEnv;
import gurobi.GRBModel;

public class MathHeuristic {
	private GRBEnv env;
    private GRBModel model;
    CCPSolver ccpSolver;
    
	public static void main(String[] args) throws Exception {
		MathHeuristic mathHeuristic = new MathHeuristic();
		mathHeuristic.solveMathHeuristic();
	}
	
	public void solveMathHeuristic() throws Exception {
		CCPChromosome bestSolution;
		CCPInstanceEntity instance = InstanceReader.readerInstance(MathHeuristicParameters.INSTANCE_TYPE,
				MathHeuristicParameters.INSTANCE_NAME);
		GA_CCP ccp = new GA_CCP(instance);
		bestSolution = (CCPChromosome)ccp.solve();
		applyMathHeuristic(bestSolution.getCodification());
	}
	
	public Integer[] solve(CCPSolver ccpSolver, Integer[] currentSolution) throws Exception {
		ccpSolver.newModel();
        ccpSolver.createVarsFromSolution(currentSolution);
        ccpSolver.addObjectiveFunctionQuadratic();
        ccpSolver.addElementOnlyOneClusterConstraint();
        ccpSolver.addLimitsConstraint();
        ccpSolver.addMathHeuristicConstraint(MathHeuristicParameters.P, currentSolution);
        ccpSolver.optimize();
        
        return ccpSolver.getSolution();
	}
	
	public void applyMathHeuristic(Integer[] initialSolution) throws Exception {
		Integer[] lastBestSolution = initialSolution;
		
		ccpSolver = new CCPSolver();
		ccpSolver.readInstance(MathHeuristicParameters.INSTANCE_TYPE, MathHeuristicParameters.INSTANCE_NAME);
		lastBestSolution = solve(ccpSolver, lastBestSolution);
	}
	
}
