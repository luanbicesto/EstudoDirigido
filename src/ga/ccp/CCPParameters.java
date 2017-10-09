package ga.ccp;

public class CCPParameters {
    /*CROSSOVER*/
    public enum SelectParentsTypeEnum {
	Tournament,
	NoSelection,
	SemiTournament
    }
    public static final SelectParentsTypeEnum selectParentsType = SelectParentsTypeEnum.Tournament;
    public static final double PERCENTAGE_SEMI_TOURNAMENT = 0.1;
    public static final double MAX_PERCENTAGE_LOCUS_CROSSOVER = 0.4;
    /*CROSSOVER*/
    
    public static final String INSTANCE_NAME = "RanReal240_03.txt";
    
    /*Local Search*/
    public enum LocalSearchStrategy {
	Swap,
	OneChange
    }
    public static final boolean ENABLE_ONE_CHANGE = true;
    public static final boolean ENABLE_SWAP = true;
    public static final double MAX_PERCENTAGE_NUMBER_NODES_LS_SWAP = 0.3;
    public static final double MAX_PERCENTAGE_NUMBER_NODES_LS_ONE_CHANGE = 0.3;
    /*Local Search*/
    
}
