package ga.ccp;

import common.instance.reader.InstanceReader.InstanceType;

public class CCPParameters {
	/* CROSSOVER */
	public enum SelectParentsTypeEnum {
		Tournament, NoSelection, SemiTournament
	}

	public static final SelectParentsTypeEnum selectParentsType = SelectParentsTypeEnum.Tournament;
	public static final double PERCENTAGE_SEMI_TOURNAMENT = 0.1;
	public static final double MAX_PERCENTAGE_LOCUS_CROSSOVER = 0.12;
	public static final boolean ORDERED_CROSSOVER_ENABLED = false;
	/* CROSSOVER */

	public static final String INSTANCE_NAME = "RanReal480_01.txt";
	public static InstanceType INSTANCE_TYPE = InstanceType.RanReal480;

	/* Local Search */
	public enum LocalSearchStrategy {
		Swap, OneChange, TripleSwap, QuadrupleSwap
	}

	public static final int PERFECT_MATCH_NUMBER_CLUSTERS = 4;
	public static final boolean ENABLE_PERFECT_MATCH = false;
	public static final boolean ENABLE_ONLY_ONE_LOCAL_SEARCH = false;
	public static final int NUMBER_LOCAL_SEARCH_TYPE = 3;
	public static final boolean ENABLE_ONE_CHANGE = true;
	public static final boolean ENABLE_SWAP = true;
	public static final boolean ENABLE_TRIPLE_SWAP = true;
	public static final boolean ENABLE_QUADRUPLE_SWAP = false;
	public static final double MAX_PERCENTAGE_NUMBER_NODES_LS_SWAP = 0.01;
	public static final double MAX_PERCENTAGE_NUMBER_NODES_LS_ONE_CHANGE = 0.02;
	public static final int NUMBER_TRIPLE_SWAP_EXECUTIONS = 1;
	public static final int NUMBER_QUADRUPLE_SWAP_EXECUTIONS = 10;
	public static final boolean BEST_IMPROVING_ONE_CHANGE = true;
	public static final boolean BEST_IMPROVING_SWAP = false;
	public static final boolean ONE_NODE_OPTION = false;
	/* Local Search */

}
