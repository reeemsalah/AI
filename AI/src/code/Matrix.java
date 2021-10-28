package code;

import java.util.Arrays;

public class Matrix extends SearchProblem {
	public static Position telephone;
	public Matrix() {
		this.operators=Arrays.asList(NeoActions.values());
	}
	@Override
	/**
	 * String state is Neo's position ;
	   each hostage state: (dead:0, alive:1, or turned:2), at the telephone booth:1 or not:0, is being carried by Neo:1 or not:0	
	 */
	public boolean goalTest(String state) {
		String[]parsedState=state.split(";");
		int NeoX=Integer.parseInt(parsedState[0].split(",")[0]);
		int NeoY=Integer.parseInt(parsedState[0].split(",")[1]);
		if(NeoX!=telephone.x||NeoY!=telephone.y)//not at the telephone booth
			return false;
		String []hostages=parsedState[1].split(",");
		for(int i=0;i<hostages.length/3;i++) {
			char hostageState=hostages[i].charAt(0);
			char hostageAtTB=hostages[i+1].charAt(0);
			char hostageCarried=hostages[i+2].charAt(0);
			if(hostageState!='0'&&hostageAtTB=='0')
				return false;
			if(hostageAtTB=='1'&&hostageCarried=='1')
				return false;
		}
		return true;
	}

	@Override
	public int pathCost(Operator o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public static Position generatePosition(int rows,int columns,boolean[][]filled,String[][]gridView,String s){
		int x=-1;
		int y=-1;
		do {
			x=(int) (Math.random()*rows);
			y=(int) (Math.random()*columns);
		}
		while(filled[x][y]);
		filled[x][y]=true;
		if(gridView[x][y]!=null)
			gridView[x][y]+=s;
		else
			gridView[x][y]=s;
		return new Position(x,y);
	}

	public static String genGrid() {
		//grid rows and columns
		String grid="";
		int rows=(int) (Math.random()*11+5);
		int columns=(int) (Math.random()*11+5);
		boolean[][]filled=new boolean[rows][columns];//keeps track of the empty and populated cells
		String[][]gridView=new String[rows][columns];
		grid+=rows+","+columns+";"+"\n";
		//Max members Neo can carry at a time
		int c=(int) (Math.random()*4+1); 
		grid+=c+";"+"\n";
		//Neo's position
		Position neo=generatePosition(rows,columns,filled,gridView,"Neo");
		grid+=neo+";"+"\n";
		//telephone booth position
		telephone=generatePosition(rows,columns,filled,gridView,"TB"+" ");
		grid+=telephone+";"+"\n";
		//hostages positions
		int hostages=(int) (Math.random()*8+3);
		String hostagesStr="";
		for(int i=0;i<hostages;i++)
		{
			hostagesStr+=generatePosition(rows, columns, filled,gridView,"H"+(i)+" ");
			hostagesStr+=","+(int) (Math.random()*99+1);
			if(i<hostages-1)
				hostagesStr+=",";
		}
		//Pills positions
		int pills=(int) (Math.random()*hostages+1);
		String pillsStr="";
		for(int i=0;i<pills;i++)
		{
			pillsStr+=generatePosition(rows, columns, filled,gridView,"P"+(i)+" ");
			if(i<pills-1)
				pillsStr+=",";
			else
				pillsStr+=";";
		}
		//Pads positions
		//remaining cells are calculated by subtracting neo's cell, telephone booth, one agent, hostage and pills
		int remaining=(int)((rows*columns)-(1+1+1+hostages+pills))/2;
		int pads=(int) (Math.random()*remaining+1);
		String padsStr="";
		for(int i=0;i<pads;i++)
		{
			Position pad1=generatePosition(rows, columns, filled,gridView,"SP"+(i));
			Position pad2=generatePosition(rows, columns, filled,gridView,"FP"+(i));
			padsStr += pad1 +","+pad2+",";
			padsStr += pad2 +","+pad1;


			if(i<pads-1)
				padsStr+=",";
			else
				padsStr+=";";
		}
		remaining=(int)((rows*columns)-(1+1+hostages+pills+pads*2));
		int agents=(int) (Math.random()*remaining+1);
		String agentsStr="";
		for(int i=0;i<agents;i++)
		{
			agentsStr+=generatePosition(rows, columns, filled,gridView,"A"+i+" ");
			if(i<agents-1)
				agentsStr+=",";
			else
				agentsStr+=";";
		}
		grid+="Agents "+agents+": "+agentsStr+"\n"+"Pills "+pills+": "+pillsStr+"\n"+"Pads "+pads*2+": "+padsStr+"\n"+"Hostages "+hostages+": "+hostagesStr;
		for(int i =-1;i<columns;i++)
		{
			System.out.print(i+"     ");
		}
		System.out.println();
		for(int i=0;i<gridView.length;i++)
		{
			
			System.out.print(i+": ");
			for(int j=0;j<gridView[i].length;j++)
			{
				System.out.print(gridView[i][j]+" ");
			}
			System.out.println();
		}
		return(grid);
	}
	
	public static String solve(String grid, String strategy, boolean visualize) {
		return("Solve");
	}
	
	public static void main(String[] args) {
		//System.out.println(genGrid());
		//System.out.println(Arrays.asList(NeoActions.values()));
	}

}
