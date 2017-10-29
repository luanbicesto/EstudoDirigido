package ilp.gurobi;

import common.instance.reader.CCPInstanceEntity;
import common.instance.reader.InstanceReader;
import ga.ccp.CCPChromosome;
import ga.ccp.Common;
import ga.ccp.GA_CCP;
import ga.framework.Chromosome;
import sun.awt.image.PNGImageDecoder.Chromaticities;

public class MathHeuristic {
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
		//bestSolution = (CCPChromosome)ccp.solve();
		bestSolution = ccp.generateRandomChromosome();
		System.out.println("Initial fitness: " + bestSolution.getFitness());
		applyMathHeuristic(bestSolution.getCodification(), bestSolution);
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
	
	public void applyMathHeuristic(Integer[] initialSolution, CCPChromosome intialChromosome) throws Exception {
		Integer[] lastBestSolution = initialSolution;
		long startTime = System.currentTimeMillis();
		ccpSolver = new CCPSolver();
		ccpSolver.readInstance(MathHeuristicParameters.INSTANCE_TYPE, MathHeuristicParameters.INSTANCE_NAME);
		
		while(Common.getRunningTime(startTime) <= MathHeuristicParameters.TOTAL_RUNNING_TIME) {
			lastBestSolution = solve(ccpSolver, lastBestSolution);
		}
		
		intialChromosome.setCodification(lastBestSolution);
		double newFitness = intialChromosome.computeFitness(true);
		System.out.println("Final fitness: " + newFitness);
	}
	
}
