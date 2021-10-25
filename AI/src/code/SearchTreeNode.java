package code;

public abstract class SearchTreeNode {
	State state;
	SearchTreeNode parent;
	Operator operator;
	int depth;
	int pathCost;
}
