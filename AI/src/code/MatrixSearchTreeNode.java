package code;

public class MatrixSearchTreeNode extends SearchTreeNode{
    public MatrixSearchTreeNode(String state, SearchTreeNode parent,
        Operator operator, int depth, int pathCost){
            this.state = state;
            this.parent = parent;
            this.operator = operator;
            this.depth = depth;
            this.pathCost = pathCost;
    }
}