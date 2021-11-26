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
		int [] thisDeathsKills = Matrix.getTotalDeathsKills(this.state);
		int [] nodeDeathsKills = Matrix.getTotalDeathsKills(node.state);
		if (thisDeathsKills[0]==nodeDeathsKills[0])
			return thisDeathsKills[1]-nodeDeathsKills[1];
		return thisDeathsKills[0]-nodeDeathsKills[0];
	}
	@Override
	public String toString()
	{
		return operator+" "+this.pathCost+"";
	}
}