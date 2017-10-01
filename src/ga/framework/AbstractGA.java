package ga.framework;

import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public abstract class AbstractGA<C extends Chromosome> {

    protected int generations;
    protected int popSize;
    protected Chromosome bestChromosome;
    protected final Random rng = new Random(0);
    private boolean verbose = true;
    
    public abstract Population<C> initializePopulation();
    public abstract Population<C> selectParents(Population<C> population);
    public abstract Population<C> crossover(Population<C> parents);
    public abstract Population<C> mutate(Population<C> offsprings);
    public abstract Population<C> selectNextPopulation(Population<C> mutants);
    
    public AbstractGA() {
	this.generations = GAConfiguration.NUMBER_GENERATIONS;
	this.popSize = GAConfiguration.POPULATION_SIZE;
    }
    
    public Chromosome solve() {
	Chromosome offspringBestChromosome;
	/* starts the initial population */
	Population<C> population = initializePopulation();
	bestChromosome = getBestChromosome(population);
	
	for (int g = 1; g <= generations; g++) {
	    Population<C> parents = selectParents(population);
	    Population<C> offsprings = crossover(parents);
	    Population<C> mutants = mutate(offsprings);
	    Population<C> newpopulation = selectNextPopulation(mutants);
	    population = newpopulation;
	    offspringBestChromosome = getBestChromosome(population);
	    if (offspringBestChromosome.getFitness() > bestChromosome.getFitness()) {
		bestChromosome = offspringBestChromosome;
		if (verbose) {
		    System.out.println("(Gen. " + g + ") BestSol = " + bestChromosome.getFitness());
		}
	    }
	}
	
	return bestChromosome;
    }
    
    protected Chromosome getBestChromosome(Population<C> population) {
	return Collections.min(population, new Comparator<Chromosome>() {
	    @Override
	    public int compare(Chromosome o1, Chromosome o2) {
		return Double.compare(o1.getFitness(), o2.getFitness());
	    }
	});
    }
}
