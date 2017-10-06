package ga.framework;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import ga.ccp.CCPChromosome;

public abstract class AbstractGA<C extends Chromosome> {

    protected int generations;
    protected int popSize;
    protected double mutationRate;
    protected C bestChromosome;
    protected final Random rng = new Random(0);
    protected final Random rng2 = new Random(1);
    private boolean verbose = true;
    
    public abstract Population<C> initializePopulation();
    public abstract Population<C> selectParents(Population<C> population);
    public abstract Population<C> crossover(Population<C> parents);
    public abstract Population<C> mutate(Population<C> offsprings);
    public abstract Population<C> selectNextPopulation(Population<C> offsprings);
    public abstract void applyLocalSearch(C chromosome);
    
    public AbstractGA() {
	this.generations = GAConfiguration.NUMBER_GENERATIONS;
	this.popSize = GAConfiguration.POPULATION_SIZE;
	this.mutationRate = GAConfiguration.MUTATION_RATE;
    }
    
    public Chromosome solve() {
	long startTime = System.currentTimeMillis();
	C offspringBestChromosome;
	/* starts the initial population */
	Population<C> population = initializePopulation();
	bestChromosome = getBestChromosome(population);
	
	for (int g = 1; g <= generations; g++) {
	    applyLocalSearch(population);
	    Population<C> parents = selectParents(population);
	    Population<C> offsprings = crossover(parents);
	    Population<C> mutants = mutate(offsprings);
	    Population<C> newpopulation = selectNextPopulation(mutants);
	    population = newpopulation;
	    offspringBestChromosome = getBestChromosome(population);
	    
	    if(GAConfiguration.ENABLE_LS_BEST_CHROMOSOME_OFFSPRINGS) {
		applyLocalSearch(offspringBestChromosome);
	    }
	    if (offspringBestChromosome.getFitness() > bestChromosome.getFitness()) {
		bestChromosome = offspringBestChromosome;
		if (verbose) {
		    System.out.println("(Gen. " + g + ") BestSol = " + bestChromosome.getFitness());
		}
	    }
	}
	
	applyLocalSearch(bestChromosome);
	((CCPChromosome)bestChromosome).verifyFitness();
	long endTime = System.currentTimeMillis();
	long totalTime = endTime - startTime;
	System.out.println("Time = " + (double) totalTime / (double) 1000 + " seg");
	return bestChromosome;
    }
    
    protected C getBestChromosome(Population<C> population) {
	return Collections.max(population, new Comparator<Chromosome>() {
	    @Override
	    public int compare(Chromosome o1, Chromosome o2) {
		return Double.compare(o1.getFitness(), o2.getFitness());
	    }
	});
    }
    
    protected Chromosome getWorstChromosome(Population<C> population) {
	return Collections.min(population, new Comparator<Chromosome>() {
	    @Override
	    public int compare(Chromosome o1, Chromosome o2) {
		return Double.compare(o1.getFitness(), o2.getFitness());
	    }
	});
    }

    private void applyLocalSearch(Population<C> population) {
	int chromosomeIndex = 0;
	C chromosome = null;
	int numberHybridCromossomes = 0;
	
	if (GAConfiguration.ENABLE_HYBRID_POPULATION && 
	    rng2.nextDouble() < GAConfiguration.PERCENTAGE_APPLY_HYBRID_TRANSFORMATION) {
	    numberHybridCromossomes = GAConfiguration.ABSOLUTE_HYBRID_POPULATION;
	    for (int i = 0; i < numberHybridCromossomes; i++) {
		chromosomeIndex = rng.nextInt(population.size());
		chromosome = population.get(chromosomeIndex);
		applyLocalSearch(chromosome);
	    }
	}
    }
}
