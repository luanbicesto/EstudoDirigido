package ga.ccp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import common.instance.reader.CCPInstanceEntity;
import common.instance.reader.InstanceReader;
import ga.framework.AbstractGA;
import ga.framework.Population;

public class GA_CCP extends AbstractGA<CCPChromosome> {
    
    private CCPInstanceEntity instance;
    
    public static void main(String[] args) throws Exception {
	CCPInstanceEntity instance = InstanceReader.readerInstance(InstanceReader.InstanceType.RanReal240, CCPParameters.INSTANCE_NAME);
	GA_CCP ccp = new GA_CCP(instance);
	ccp.solve();
    }
    
    public GA_CCP(CCPInstanceEntity instance) {
	super();
	this.instance = instance;
    }

    @Override
    public CCPPopulation initializePopulation() {
	CCPPopulation newPopulation = new CCPPopulation();
	
	while (newPopulation.size() < popSize) {
	    try {
		newPopulation.add(generateRandomChromosome());
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	return newPopulation;
    }

    @Override
    public CCPPopulation selectParents(Population<CCPChromosome> population) {
	CCPPopulation parents = null;
	
	switch (CCPParameters.selectParentsType) {
	case Tournament:
	    parents = selectParentsByTournament((CCPPopulation)population);
	    break;

	default:
	    break;
	}
	
	return parents;
    }

    @Override
    public CCPPopulation crossover(Population<CCPChromosome> parents) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public CCPPopulation mutate(Population<CCPChromosome> offsprings) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public CCPPopulation selectNextPopulation(Population<CCPChromosome> mutants) {
	// TODO Auto-generated method stub
	return null;
    }
    
    private CCPChromosome generateRandomChromosome() throws Exception {
	CCPChromosome chromosome = new CCPChromosome(instance.getN(), instance.getEdgeWeights());
	ArrayList<Set<Integer>> nodesByCluster = new ArrayList<>(instance.getK());
	
	for(int i = 0; i < instance.getK(); i++) {
	    nodesByCluster.add(i, new HashSet<Integer>());
	}
	
	for(int i = 0; i < instance.getN(); i++) {
	    int clusterId = rng.nextInt(instance.getK());
	    chromosome.getCodification()[i] = clusterId;
	    nodesByCluster.get(clusterId).add(i);
	}
	chromosome.setFitness(nodesByCluster);
	
	return chromosome;
    }
    
    private CCPPopulation selectParentsByTournament(CCPPopulation population) {
	CCPPopulation parents = new CCPPopulation();

	while (parents.size() < popSize) {
	    int index1 = rng.nextInt(popSize);
	    CCPChromosome parent1 = population.get(index1);
	    int index2 = rng.nextInt(popSize);
	    CCPChromosome parent2 = population.get(index2);
	    if (parent1.getFitness() > parent2.getFitness()) {
		parents.add(parent1);
	    } else {
		parents.add(parent2);
	    }
	}

	return parents;
    }
    
}
