package ga.framework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import ga.ccp.CCPChromosome;
import ga.ccp.CcpRuntimeConfiguration;
import ga.ccp.CcpRuntimeConfiguration.MOMENT_LOCAL_SEARCH;
import ga.ccp.Common;

public abstract class AbstractGA<C extends Chromosome> {

	protected int generations;
	protected int popSize;
	protected double mutationRate;
	protected C bestChromosome;
	protected final Random rng;
	private boolean verbose = true;
	protected CcpRuntimeConfiguration ccpConfiguration;
	protected double applyNewPopulationPercentage = GAConfiguration.PERCENTAGE_APPLY_NEW_POPULATION;

	public abstract Population<C> initializePopulation();

	public abstract Population<C> selectParents(Population<C> population);

	public abstract Population<C> crossover(Population<C> parents);

	public abstract Population<C> mutate(Population<C> offsprings);

	public abstract Population<C> selectNextPopulation(Population<C> offsprings);

	public abstract void applyLocalSearch(C chromosome, boolean applyToAllNodes);

	public abstract void applyOriginalLocalSearch(C chromosome, boolean applyToAllNodes);

	public AbstractGA() {
		this(CcpRuntimeConfiguration.getDefaultConfiguration());
	}
	
	public AbstractGA(CcpRuntimeConfiguration ccpConfiguration) {
		this.generations = GAConfiguration.NUMBER_GENERATIONS;
		this.popSize = GAConfiguration.POPULATION_SIZE;
		this.mutationRate = GAConfiguration.MUTATION_RATE;
		this.ccpConfiguration = ccpConfiguration;
		this.rng = new Random(ccpConfiguration.getSeed());
	}
	
	private boolean keepRunningGA(int g, long startTime) {
		return g <= generations &&
			   Common.getRunningTime(startTime) <= GAConfiguration.TOTAL_RUNNING_TIME &&
			   (this.ccpConfiguration.getTimeToTargetValue() <= 0.0 ? true : bestChromosome.getFitness() < this.ccpConfiguration.getTimeToTargetValue());
	}
	
	public Chromosome solve() {
		long startTime = System.currentTimeMillis();
		C offspringBestChromosome;
		/* starts the initial population */
		Population<C> population = initializePopulation();
		bestChromosome = getBestChromosome(population);

		for (int g = 1; keepRunningGA(g, startTime); g++) {
			if(ccpConfiguration.getMomentLocalSearch() == MOMENT_LOCAL_SEARCH.BEGINNING) {
				applyLocalSearch(population);
			}
			
			Population<C> parents = selectParents(population);
			Population<C> offsprings = crossover(parents);
			Population<C> mutants = offsprings;
			if (GAConfiguration.ENABLE_MUTATION) {
				mutants = mutate(offsprings);
			}
			
			if(ccpConfiguration.getMomentLocalSearch() == MOMENT_LOCAL_SEARCH.END) {
				applyLocalSearch(mutants);
			}
			
			Population<C> newpopulation = selectNextPopulation(mutants);
			population = newpopulation;
			offspringBestChromosome = getBestChromosome(population);

			if (offspringBestChromosome.getFitness() > bestChromosome.getFitness()) {
				@SuppressWarnings("unchecked")
				C clone = (C)offspringBestChromosome.clone();
				bestChromosome = clone;
				if (verbose) {
					System.out.println("(Gen. " + g + ") BestSol = " + bestChromosome.getFitness());
				}
			}
		}

		if(ccpConfiguration.isEnableHybridLocalSearch()) {
			applyLocalSearch(bestChromosome, true);
		}
		
		((CCPChromosome) bestChromosome).verifyFitness();
		System.out.println("Time = " + Common.getRunningTime(startTime) + " seg");
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
		int numberOriginalLsCromossomes = 0;
		C offspringBestChromosome = null;

		if (ccpConfiguration.isEnableHybridLocalSearch()
				&& rng.nextDouble() < ccpConfiguration.getPercentageLocalSearch()) {
			numberHybridCromossomes = ccpConfiguration.getAbsoluteHybridPupulation();

			if(!GAConfiguration.PARALLEL_LOCAL_SEARCH) {
				for (int i = 0; i < numberHybridCromossomes; i++) {
					chromosomeIndex = rng.nextInt(population.size());
					chromosome = population.get(chromosomeIndex);
					applyLocalSearch(chromosome, false);
				}
			} else {
				ArrayList<C> allChromosomes = new ArrayList<>();
				for (int i = 0; i < numberHybridCromossomes; i++) {
					chromosomeIndex = rng.nextInt(population.size());
					chromosome = population.get(chromosomeIndex);
					allChromosomes.add(chromosome);
				}
				
				allChromosomes.parallelStream().forEach(c -> applyLocalSearch(c, false));
			}
		}
		
		if (GAConfiguration.ENABLE_LS_BEST_CHROMOSOME_OFFSPRINGS) {
			offspringBestChromosome = getBestChromosome(population);
			applyLocalSearch(offspringBestChromosome, false);
		}

		if (GAConfiguration.ENABLE_ORIGINAL_LS_POPULATION
				&& rng.nextDouble() < GAConfiguration.PERCENTAGE_APPLY_ORIGINAL_LOCAL_SEARCH) {
			numberOriginalLsCromossomes = GAConfiguration.ABSOLUTE_ORIGINAL_LOCAL_SEARCH_POPULATION;
			for (int i = 0; i < numberOriginalLsCromossomes; i++) {
				chromosomeIndex = rng.nextInt(population.size());
				chromosome = population.get(chromosomeIndex);
				applyOriginalLocalSearch(chromosome, false);
			}
		}
	}

}
