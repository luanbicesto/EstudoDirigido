package ga.ccp;

public class CCPParameters {
    /*CROSSOVER*/
    public enum SelectParentsTypeEnum {
	Tournament,
	NoSelection
    }
    public static final SelectParentsTypeEnum selectParentsType = SelectParentsTypeEnum.Tournament;
    
    public static final double MAX_PERCENTAGE_LOCUS_CROSSOVER = 0.1;
    /*CROSSOVER*/
    
    public static final String INSTANCE_NAME = "RanReal240_01.txt";
    
    /*Local Search*/
    public enum LocalSearchStrategy {
	Swap,
	OneChange
    }
    public static final double PERCENTAGE_SWAP = 0.5;
    /*Local Search*/
    
}
