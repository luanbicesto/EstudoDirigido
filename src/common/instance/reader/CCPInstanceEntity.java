package common.instance.reader;

public class CCPInstanceEntity {
    private int k; //number of clusters
    private int n; //number of itens
    private double[][] edgeWeights;
    private double[] nodeWeights;
    private double[] lowerBound;
    private double[] upperBound;
    private String name;
    
    public int getK() {
        return k;
    }
    public void setK(int k) {
        this.k = k;
    }
    public int getN() {
        return n;
    }
    public void setN(int n) {
        this.n = n;
    }
    public double[][] getEdgeWeights() throws Exception {
	if(this.edgeWeights == null) {
	    if(n == 0) {
		throw new Exception("impossible to create the EdgeWeights array: N = 0");
	    }
	    this.edgeWeights = new double[n][n];
	}
	
        return edgeWeights;
    }
    public void setEdgeWeights(double[][] edgeWeights) {
        this.edgeWeights = edgeWeights;
    }
    public double[] getNodeWeights() throws Exception {
	if(this.nodeWeights == null) {
	    if(n == 0) {
		throw new Exception("impossible to create the NodeWeights array: N = 0");
	    }
	    this.nodeWeights = new double[n];
	}
	
        return nodeWeights;
    }
    public void setNodeWeights(double[] nodeWeights) {
        this.nodeWeights = nodeWeights;
    }
    public double[] getLowerBound() throws Exception {
	if(this.lowerBound == null) {
	    if(k == 0) {
		throw new Exception("impossible to create the LowerBound array: K = 0");
	    }
	    this.lowerBound = new double[k];
	}
	
        return lowerBound;
    }
    public void setLowerBound(double[] lowerBound) {
        this.lowerBound = lowerBound;
    }
    public double[] getUpperBound() throws Exception {
	if(this.upperBound == null) {
	    if(k == 0) {
		throw new Exception("impossible to create the UpperBound array: K = 0");
	    }
	    this.upperBound = new double[k];
	}
	
        return upperBound;
    }
    public void setUpperBound(double[] upperBound) {
        this.upperBound = upperBound;
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
    
}
