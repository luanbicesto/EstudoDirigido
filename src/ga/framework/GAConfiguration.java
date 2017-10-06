package ga.framework;

public class GAConfiguration {
    public static final int NUMBER_GENERATIONS = 640000;
    public static final int POPULATION_SIZE = 100;
    public static final double MUTATION_RATE = 1 / (double)POPULATION_SIZE;
    
    public static boolean ENABLE_HYBRID_POPULATION = true;
    public static boolean ENABLE_LS_BEST_CHROMOSOME_OFFSPRINGS = false;
    public static int PERCENTAGE_HYBRID_POPULATION = 10; //not used
    public static int ABSOLUTE_HYBRID_POPULATION = 3;
    public static double PERCENTAGE_APPLY_HYBRID_TRANSFORMATION = 0.1;
}
