package code;

public class Position {
	int x;
	int y;
	public Position(int x,int y) {
		this.x=x;
		this.y=y;
	}
	public String toString() {
		return(this.x+","+this.y);
	}
	
	public boolean equals(Position pop) {
		return(this.x==pop.x&&this.y==pop.y);
	}

}
