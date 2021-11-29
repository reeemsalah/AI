package code;

import java.util.List;

public abstract class SearchProblem {
	List<Operator> operators;
	String initialState;
	public abstract String stateSpace(String state,Operator o);
	public abstract boolean goalTest(String state);
	public abstract int pathCost(String state,int depth);

}
