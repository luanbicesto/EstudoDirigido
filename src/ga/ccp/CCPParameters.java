package ga.ccp;

public class CCPParameters {
    /*Select parents for crossover*/
    public enum SelectParentsTypeEnum {
	Tournament
    }
    
    public static final SelectParentsTypeEnum selectParentsType = SelectParentsTypeEnum.Tournament;
    public static final String INSTANCE_NAME = "RanReal240_01.txt";
}
