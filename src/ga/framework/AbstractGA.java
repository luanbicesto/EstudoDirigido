package ga.framework;

import java.util.Collections;
import java.util.Comparator;

public abstract class AbstractGA {

    protected int generations;
    protected Chromosome bestChromosome;
    private boolean verbose = true;
    
    public abstract Population initializePopulation();
    public abstract Population selectParents(Population population);
    public abstract Population crossover(Population parents);
    public abstract Population mutate(Population offsprings);
    public abstract Population selectNextPopulation(Population mutants);
    
    public AbstractGA(Integer generations) {
	this.generations = generations;
    }
    
    public Chromosome solve() {
	Chromosome offspringBestChromosome;
	/* starts the initial population */
	Population population = initializePopulation();
	bestChromosome = getBestChromosome(population);
	
	for (int g = 1; g <= generations; g++) {
	    Population parents = selectParents(population);
	    Population offsprings = crossover(parents);
	    Population mutants = mutate(offsprings);
	    Population newpopulation = selectNextPopulation(mutants);
	    population = newpopulation;
	    offspringBestChromosome = getBestChromosome(population);
	    if (offspringBestChromosome.fitness() > bestChromosome.fitness()) {
		bestChromosome = offspringBestChromosome;
		if (verbose) {
		    System.out.println("(Gen. " + g + ") BestSol = " + bestChromosome.fitness());
		}
	    }
	}
	
	return bestChromosome;
    }
    
    protected Chromosome getBestChromosome(Population population) {
	return Collections.min(population, new Comparator<Chromosome>() {
	    @Override
	    public int compare(Chromosome o1, Chromosome o2) {
		return Double.compare(o1.fitness(), o2.fitness());
	    }
	});
    }
}
