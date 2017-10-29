package ilp.gurobi;

import common.instance.reader.CCPInstanceEntity;
import common.instance.reader.InstanceReader;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;

public class CCPSolver {
    private GRBEnv env;
    private GRBModel model;
    private int p; //number of clusters
    private int n; //number of itens
    private GRBVar[][] elements;
    private double[][] edgeWeights;
    private double[] nodeWeights;
    private double[] lowerBound;
    private double[] upperBound;
    
	public static void main(String[] args) throws Exception {
		CCPSolver solver = new CCPSolver();
		solver.solve();
	}
    
    public CCPSolver() {
    	try {
			env = new GRBEnv("ccpQuadratic.log");
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public GRBModel getModel() {
		return model;
	}

	public void setModel(GRBModel model) {
		this.model = model;
	}

	public void optimize() throws Exception {
		model.optimize();
	}
	
	public void newModel() throws Exception {
		this.env = new GRBEnv();
		this.model = new GRBModel(env); 
	}
	
	public Integer[] getSolution() throws Exception {
		Integer[] solution = new Integer[n];
		
		for(int i = 0; i < n; i++) {
            for(int k = 0; k < p; k++) {
                if(elements[i][k].get(GRB.DoubleAttr.X) > 0.95) {
                	solution[i] = k;
                }
            }
        }
		
		return solution;
	}
    
    public void solve() throws Exception{
        try {
            
            model = new GRBModel(env);
            model.set(GRB.DoubleParam.TimeLimit, SolverParameters.TIME_LIMIT);
            readInstance(InstanceReader.InstanceType.RanReal240, SolverParameters.INSTANCE_NAME);
            createVars();
            addObjectiveFunctionQuadratic();
            addElementOnlyOneClusterConstraint();
            addLimitsConstraint();
            model.optimize();
            //printSolution();
        } catch (GRBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void printSolution() throws GRBException {
        for(int i = 0; i < n; i++) {
            for(int k = 0; k < p; k++) {
                System.out.println(elements[i][k].get(GRB.StringAttr.VarName) + " " + elements[i][k].get(GRB.DoubleAttr.X));
            }
        }
    }
    
	public void readInstance(InstanceReader.InstanceType instanceType, String instanceName) throws Exception {
		CCPInstanceEntity instance = InstanceReader.readerInstance(instanceType, instanceName);
		n = instance.getN();
		p = instance.getK();
		edgeWeights = instance.getEdgeWeights();
		nodeWeights = instance.getNodeWeights();
		lowerBound = instance.getLowerBound();
		upperBound = instance.getUpperBound();
	}
    
    private void createVars() throws GRBException{
        elements = new GRBVar[n][p];
        for(int i = 0; i < n; i++) {
            for(int k = 0; k < p; k++) {
                elements[i][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x" + Integer.toString(i) + "_" + Integer.toString(k));
            }
        }
    }
    
    public void createVarsFromSolution(Integer[] solution) throws GRBException{
        elements = new GRBVar[n][p];
        double variableValue = 0.0;
        
        for(int i = 0; i < n; i++) {
            for(int k = 0; k < p; k++) {
            	variableValue = solution[i] == k ? 1.0 : 0.0;
                elements[i][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x" + Integer.toString(i) + "_" + Integer.toString(k));
            }
        }
    }
    
    public void addMathHeuristicConstraint(int p, Integer[] solution) throws GRBException {
    	GRBLinExpr expr = new GRBLinExpr();
    	
    	for(int i = 0; i < solution.length; i++) {
    		expr.addTerm(1.0, elements[i][solution[i]]);
    	}
    	
    	model.addConstr(expr, GRB.GREATER_EQUAL, n - p, "mathHeuristic");
    }
    
    public void addObjectiveFunctionQuadratic() throws GRBException{
        GRBQuadExpr expr = new GRBQuadExpr();
        for(int k = 0; k < p; k++) {
            for(int i = 0; i < n-1; i++) {
                for(int j = i+1; j < n; j++) {
                    expr.addTerm(edgeWeights[i][j], elements[i][k], elements[j][k]);
                }
            }
        }
        model.setObjective(expr, GRB.MAXIMIZE);
    }
        
    public void addElementOnlyOneClusterConstraint() throws GRBException {
        GRBLinExpr expr;
        for(int i = 0; i < n; i++) {
            expr = new GRBLinExpr();
            for(int k = 0; k < p; k++) {
                expr.addTerm(1.0, elements[i][k]);
            }
            model.addConstr(expr, GRB.EQUAL, 1.0, "c" + Integer.toString(i));
        }
    }
    
    public void addLimitsConstraint() throws GRBException{
        GRBLinExpr expr;
        for(int k = 0; k < p; k++) {
            expr = new GRBLinExpr();
            for(int i = 0; i < n; i++) {
                expr.addTerm(nodeWeights[i], elements[i][k]);
            }
            model.addConstr(expr, GRB.GREATER_EQUAL, lowerBound[k], "k_lower_" + Integer.toString(k));
            model.addConstr(expr, GRB.LESS_EQUAL, upperBound[k], "k_upper_" + Integer.toString(k));
        }
    }
}