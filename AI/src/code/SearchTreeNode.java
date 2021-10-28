package code;

public abstract class SearchTreeNode {
	String state;
	SearchTreeNode parent;
	Operator operator;
	int depth;
	int pathCost;
}
