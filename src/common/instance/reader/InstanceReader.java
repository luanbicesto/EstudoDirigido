package common.instance.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

public class InstanceReader {
    private static String INSTANCE_FOLDER = "instances/";
    
    public enum InstanceType {
	RanReal240,
	RanReal480,
	Handover,
	Sparse82
    }
    
    public static CCPInstanceEntity readerInstance(InstanceType instanceType, String instanceName) throws Exception {
	CCPInstanceEntity instance = null;
	switch (instanceType) {
	case RanReal240:
	    instance = handleRanReal(instanceName, "RanReal240");
	    break;
	case RanReal480:
	    instance = handleRanReal(instanceName, "RanReal480");
	    break;
	case Sparse82:
		instance = handleRanReal(instanceName, "Sparse82");
		break;
	default:
	    break;
	}
	
	return instance;
    }
    
    private static CCPInstanceEntity handleRanReal(String instanceName, String folderName) throws Exception{
	CCPInstanceEntity instance = new CCPInstanceEntity();
	String filePath = INSTANCE_FOLDER + folderName + "/" + instanceName;
	
	Reader fileInst = new BufferedReader(new FileReader(filePath));
	StreamTokenizer tokenizer = new StreamTokenizer(fileInst);
	
	//read number of nodes
	instance.setN(readNextInteger(tokenizer));
	
	//read number of clusters
	instance.setK(readNextInteger(tokenizer));
	
	//Ignore cluster type
	tokenizer.nextToken();
	
	//Read upper and lower bounds
	for(int i = 0; i < instance.getK(); i++) {
	    instance.getLowerBound()[i] = readNextDouble(tokenizer);
	    instance.getUpperBound()[i] = readNextDouble(tokenizer);
	}
	
	//Ignore letter W called sentinel
	tokenizer.nextToken();
	
	//Read nodes weights
	for(int i = 0; i < instance.getN(); i++) {
	    instance.getNodeWeights()[i] = readNextDouble(tokenizer);
	}
	
	//Read edges weigths
	int nodeAIndex = 0;
	int nodeBIndex = 0;
	double weight = 0.0;
	for(int i = 0; i < instance.getN(); i++) {
	    for(int j = i + 1; j < instance.getN(); j++) {
		nodeAIndex = readNextInteger(tokenizer);
		nodeBIndex = readNextInteger(tokenizer);
		weight = readNextDouble(tokenizer);
		instance.getEdgeWeights()[nodeAIndex][nodeBIndex] = weight;
		instance.getEdgeWeights()[nodeBIndex][nodeAIndex] = weight;
	    }
	}
	
	return instance;
    }
    
    private static int readNextInteger(StreamTokenizer tokenizer) throws IOException {
	tokenizer.nextToken();
	return (int)tokenizer.nval;
    }
    
    private static double readNextDouble(StreamTokenizer tokenizer) throws IOException {
	tokenizer.nextToken();
	return tokenizer.nval;
    }
}
