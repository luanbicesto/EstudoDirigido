package ga.framework;

public abstract class Chromosome {
    public abstract double getFitness();
    public abstract Chromosome clone();
}
