package ga.ccp;

import java.util.ArrayList;

import common.instance.reader.CCPInstanceEntity;
import common.instance.reader.InstanceReader;
import ga.ccp.CCPParameters.LocalSearchStrategy;
import ga.framework.AbstractGA;
import ga.framework.GAConfiguration;
import ga.framework.Population;

public class GA_CCP extends AbstractGA<CCPChromosome> {
    
    private CCPInstanceEntity instance;
    private LocalSearchWrapper localSearch;
    
    public static void main(String[] args) throws Exception {
	CCPInstanceEntity instance = InstanceReader.readerInstance(InstanceReader.InstanceType.RanReal240, CCPParameters.INSTANCE_NAME);
	GA_CCP ccp = new GA_CCP(instance);
	ccp.solve();
    }
    
    public GA_CCP(CCPInstanceEntity instance) {
	super();
	this.instance = instance;
	this.localSearch = new LocalSearchWrapper(instance, rng);
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
	
	//initializeBufferIndividuals();

	return newPopulation;
    }

    @Override
    public CCPPopulation selectParents(Population<CCPChromosome> population) {
	CCPPopulation parents = null;
	
	switch (CCPParameters.selectParentsType) {
	case Tournament:
	    parents = selectParentsByTournament((CCPPopulation)population);
	    break;
	case NoSelection:
	    parents = (CCPPopulation)population;
	    break;

	default:
	    break;
	}
	
	return parents;
    }

    @Override
    public CCPPopulation crossover(Population<CCPChromosome> parents) {
	CCPPopulation offspring = new CCPPopulation();
	double maxNumberLocusCrossover = 0.0;
	
	maxNumberLocusCrossover = instance.getN() * CCPParameters.MAX_PERCENTAGE_LOCUS_CROSSOVER;
	for (int i = 0; i < popSize; i = i + 2) {
	    createOffspring(parents, offspring, maxNumberLocusCrossover);
	}
	
	return offspring;
    }
    
    private void createOffspring(Population<CCPChromosome> parents, CCPPopulation offspring, double maxNumberLocusCrossover) {
	int numberLocusCrossover = 0;
	
	CCPChromosome parent1 = parents.get(rng.nextInt(parents.size()));
	CCPChromosome parent2 = getSecondParent(parents);
	CCPChromosome offspring1 = parent1.clone();
	CCPChromosome offspring2 = parent2.clone();
	
	numberLocusCrossover = rng.nextInt((int)Math.round(maxNumberLocusCrossover)) + 1;	
	for(int i = 0; i < numberLocusCrossover; i++) {
	    crossover(parent1, offspring2);
	    crossover(parent2, offspring1);
	}

	offspring.add(offspring1);
	offspring.add(offspring2);
    }
    
    private CCPChromosome getSecondParent(Population<CCPChromosome> parents) {
	CCPChromosome parent2 = parents.get(rng.nextInt(parents.size()));
	
	if(GAConfiguration.ENABLE_NEW_POPULATION &&
	   rng.nextDouble() < applyNewPopulationPercentage) {
	    //parent2 = newPopulation.get(rng.nextInt(newPopulation.size()));
	    try {
		parent2 = generateRandomChromosome();
	    } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	
	return parent2;
    }
    
    private void crossover(CCPChromosome parent, CCPChromosome offspring) {
	int locusIndex = rng.nextInt(parent.getCodification().length);
	int parentClusterId = parent.getCodification()[locusIndex];
	try {
	    offspring.setAllele(locusIndex, parentClusterId);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    public Population<CCPChromosome> mutate(Population<CCPChromosome> offsprings) {
	boolean chromosomeChanged;
	for (CCPChromosome chromosome : offsprings) {
	    chromosomeChanged = false;
	    for (int i = 0; i < chromosome.getCodification().length; i++) {
		if (rng.nextDouble() < mutationRate) {
		    chromosomeChanged = true;
		    mutateGene(chromosome, i);
		}
	    }

	    if (chromosomeChanged) {
		//chromosome.setFitness();
	    }
	}

	return offsprings;
    }
    
    @Override
    public void applyLocalSearch(CCPChromosome chromosome) {
	int lsIndex = 0;
	
	lsIndex = rng.nextInt(2);
	localSearch.applyLocalSearch(LocalSearchStrategy.values()[lsIndex], chromosome);
    }
    
    private void mutateGene(CCPChromosome chromosome, int locus) {
	int newCluster = 0;

	newCluster = rng.nextInt(instance.getK());
	try {
	    chromosome.setAllele(locus, newCluster);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    public Population<CCPChromosome> selectNextPopulation(Population<CCPChromosome> offsprings) {
	CCPChromosome worst = (CCPChromosome) getWorstChromosome(offsprings);

	if (worst.getFitness() < bestChromosome.getFitness()) {
	    offsprings.remove(worst);
	    offsprings.add((CCPChromosome) bestChromosome);
	}

	return offsprings;
    }
    
    private CCPChromosome generateRandomChromosome() throws Exception {
	CCPChromosome chromosome = new CCPChromosome(instance);
	ArrayList<ArrayList<Integer>> nodesByCluster = new ArrayList<>(instance.getK());
	ArrayList<Double> clustersCurrentWeight = new ArrayList<>(instance.getK());
	
	for(int i = 0; i < instance.getK(); i++) {
	    nodesByCluster.add(i, new ArrayList<Integer>());
	    clustersCurrentWeight.add(i, 0.0);
	}
	
	for(int i = 0; i < instance.getN(); i++) {
	    int clusterId = getNextAvailableClusterId(i, clustersCurrentWeight);
	    chromosome.getCodification()[i] = clusterId;
	    nodesByCluster.get(clusterId).add(i);
	    clustersCurrentWeight.set(clusterId, clustersCurrentWeight.get(clusterId) + instance.getNodeWeights()[i]);
	}
	chromosome.initialize(nodesByCluster, clustersCurrentWeight);
	
	return chromosome;
    }
    
    //TODO: Exception when no cluster is available
    private int getNextAvailableClusterId(int nodeIndex, ArrayList<Double> clustersCurrentWeight) throws Exception {
	int clusterId = 0;
	double clusterWeight = 0.0;
	double targetClusterWeight = 0.0;
	
	clusterId = rng.nextInt(instance.getK());
	clusterWeight = clustersCurrentWeight.get(clusterId);
	targetClusterWeight = clusterWeight + instance.getNodeWeights()[nodeIndex];  
	
	while(Common.isClusterOverWeighted(clusterId, targetClusterWeight, instance)) {
	    clusterId = (clusterId + 1) % instance.getK();
	    clusterWeight = clustersCurrentWeight.get(clusterId);
	    targetClusterWeight = clusterWeight + instance.getNodeWeights()[nodeIndex];
	}
	
	return clusterId;
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
