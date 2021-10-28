package code;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchProblem {
	ArrayList<String> stateSpace; //may need a graph instead of an Arraylist
	List<Operator> operators;
	String initialState;
	public abstract boolean goalTest(String state);
	public abstract int pathCost(Operator o);

}
