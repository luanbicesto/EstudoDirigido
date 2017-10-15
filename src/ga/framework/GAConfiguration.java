package ga.framework;

public class GAConfiguration {
    public static final long TOTAL_RUNNING_TIME = 60;
    public static final int NUMBER_GENERATIONS = 192000;
    public static final int POPULATION_SIZE = 100;
    public static final double MUTATION_RATE = 1.0 / (double)POPULATION_SIZE;
    public static boolean ENABLE_MUTATION_SWAP = false;
    
    public static boolean ENABLE_HYBRID_POPULATION = true;
    public static boolean ENABLE_LS_BEST_CHROMOSOME_OFFSPRINGS = false;
    public static int PERCENTAGE_HYBRID_POPULATION = 10; //not used
    public static int ABSOLUTE_HYBRID_POPULATION = 20;
    public static double PERCENTAGE_APPLY_HYBRID_TRANSFORMATION = 0.5;
    
    /*New population*/
    public static final boolean ENABLE_NEW_POPULATION = true;
    public static final boolean ENABLE_MUTATION = true;
    public static final double TIMES_SIZE_NEW_POPULATION = 2; //not used
    public static double PERCENTAGE_APPLY_NEW_POPULATION = 0.1;
    /*New population*/
    
    /*Not used*/
    public static int MAX_NUMBER_GENERATIONS_NO_IMPROVEMENT = 10000;
    public static int MAX_NUMBER_GENERATIONS_PlATO_TREATMENT = 5000;
    public static int MAX_VALUE_MUTATION_NUMERATOR = 4;
    public static double MINIMUM_IMPROVEMENT = 1.0;
}
