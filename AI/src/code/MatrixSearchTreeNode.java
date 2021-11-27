package code;

public class MatrixSearchTreeNode extends SearchTreeNode implements Comparable{
    public MatrixSearchTreeNode(String state, SearchTreeNode parent,
        Operator operator, int depth, int pathCost,int h){
            this.state = state;
            this.parent = parent;
            this.operator = operator;
            this.depth = depth;
            this.pathCost = pathCost;
			this.h=h;
    }

	@Override
	public int compareTo(Object o) {
		MatrixSearchTreeNode node =(MatrixSearchTreeNode)o;
		return this.pathCost+this.h-node.pathCost-node.h;
	}
	@Override
	public String toString()
	{
		return operator+" "+this.pathCost+"";
	}
}