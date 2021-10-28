package code;

import java.util.Arrays;
import java.util.List;

public class GraphNode {
	static String stateSpace="";
	static List<Operator>operators=Arrays.asList(NeoActions.values());
	String state;
	GraphNode parent;
	Operator operator;
	String []edges;
	
	public GraphNode(String state,GraphNode parent,Operator operator) {
		this.state=state;
		this.parent=parent;
		this.edges=new String[operators.size()];
		this.operator=operator;
		stateSpace+=state;
	}
	public int compareTo(GraphNode n) {
		return state.compareTo(n.state);
	}

}
