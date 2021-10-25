package code;

import java.util.ArrayList;

public abstract class SearchProblem {
	ArrayList<State> stateSpace; //may need a graph instead of an Arraylist
	ArrayList<Operator> operators;
	State initialState;
	public abstract boolean goalTest();
	public abstract int pathCost(Operator o);

}
