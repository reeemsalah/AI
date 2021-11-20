package code;

public class MatrixSearchTreeNode extends SearchTreeNode implements Comparable{
    public MatrixSearchTreeNode(String state, SearchTreeNode parent,
        Operator operator, int depth, int pathCost){
            this.state = state;
            this.parent = parent;
            this.operator = operator;
            this.depth = depth;
            this.pathCost = pathCost;
    }

	@Override
	public int compareTo(Object o) {
		MatrixSearchTreeNode node =(MatrixSearchTreeNode)o;
		return this.pathCost-node.pathCost;
	}
	@Override
	public String toString()
	{
		return operator+" "+this.pathCost+"";
	}
}