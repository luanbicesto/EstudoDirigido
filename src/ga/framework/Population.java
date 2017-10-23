package ga.framework;

import java.util.ArrayList;

public abstract class Population<C extends Chromosome> extends ArrayList<C> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public Population() {
    }
    
    public Population(Population<C> population) {
    	super(population);
    }
}
