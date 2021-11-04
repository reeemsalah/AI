package code;

import java.util.Arrays;

public class Matrix extends SearchProblem {
	String grid;

	Matrix(String grid) {
		this.grid=grid;
		this.operators=Arrays.asList(NeoActions.values());
		this.initialState=this.grid+";";
		//adding a carry flag to each hostage
		int hostageCount=grid.split(";")[7].split(",").length/3;
		for(int i=0;i<hostageCount;i++) {
			this.initialState+="0";
			if(i<hostageCount-1)
				this.initialState+=",";
			else
				this.initialState+=";";
		}
		//adding Neo's Damage
		this.initialState+="0";//Neo's damage
		System.out.println(initialState);
	}
	@Override
	/**
	 * 	state is grid;each hostage state: turned and killed:2 carried:1 or not:0; Neo's Damage
	 */
	public boolean goalTest(String state) {
		//parsing state
		String[]parsedState=state.split(";");
		int rows=Integer.parseInt(parsedState[0].split(",")[0]);
		int columns=Integer.parseInt(parsedState[0].split(",")[1]);
		int c=Integer.parseInt(parsedState[1]);
		int NeoX=Integer.parseInt(parsedState[2].split(",")[0]);
		int NeoY=Integer.parseInt(parsedState[2].split(",")[1]);
		int TeleX=Integer.parseInt(parsedState[3].split(",")[0]);
		int TeleY=Integer.parseInt(parsedState[3].split(",")[1]);
		String[] agentList=parsedState[4].split(",");
		Position[]agents=new Position[agentList.length/2];
		for(int i=0;i<agents.length;i++) {
			agents[i]=new Position(Integer.parseInt(agentList[i*2]),Integer.parseInt(agentList[i*2+1]));
			System.out.println("agent "+i+" "+agents[i]);
		}
		String[] pillList=parsedState[5].split(",");	
		Position[]pills=new Position[pillList.length/2];
		for(int i=0;i<pills.length;i++) {
			pills[i]=new Position(Integer.parseInt(pillList[i*2]),Integer.parseInt(pillList[i*2+1]));
			System.out.println("pill "+i+" "+pills[i]);
		}
		String[] padList=parsedState[6].split(",");	
		Position[]startPads=new Position[padList.length/4];
		Position[]endPads=new Position[padList.length/4];
		for(int i=0;i<startPads.length;i++) {
			startPads[i]=new Position(Integer.parseInt(padList[i*4]),Integer.parseInt(padList[i*4+1]));
			System.out.println("start pad "+i+" "+startPads[i]);
			endPads[i]=new Position(Integer.parseInt(padList[i*4+2]),Integer.parseInt(padList[i*4+3]));
			System.out.println("end pad "+i+" "+endPads[i]);
		}
		String[]hostageList=parsedState[7].split(",");
	    Position[]hostages=new Position[hostageList.length/3];
	    int[]hostagesDamage=new int[hostageList.length/3];
		for(int i=0;i<hostages.length;i++) {
			hostages[i]=new Position(Integer.parseInt(hostageList[i*3]),Integer.parseInt(hostageList[i*3+1]));
			System.out.println("Hostage: "+i+" "+hostages[i]);
			hostagesDamage[i]=Integer.parseInt(hostageList[i*3+2]);
			System.out.println("HostageDamage: "+i+" "+hostagesDamage[i]);
		}
		int[]hostagesState=new int [hostages.length];
		for(int i=0;i<parsedState[8].split(",").length;i++)
			hostagesState[i]=Integer.parseInt(parsedState[8].split(",")[i]);
		int NeoDamage=Integer.parseInt(parsedState[9]);
		//ended parsing state
		if(NeoX!=TeleX||NeoY!=TeleY)//not at the telephone booth
			return false;
		for(int i=0;i<hostages.length;i++) {
			if(hostagesState[i]!=2&& (hostages[i].x!=TeleX||hostages[i].y!=TeleY))
				return false;
			if((hostages[i].x==TeleX&&hostages[i].y==TeleY)&&hostagesState[i]!=0)
				return false;
		}
		return true;
	}

	@Override
	public int pathCost(Operator o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String stateSpace(String state, Operator o) {
		//parse the state
		String[]parsedState=state.split(";");
		String []nextParsedState=parsedState.clone();
		int rows=Integer.parseInt(parsedState[0].split(",")[0]);
		int columns=Integer.parseInt(parsedState[0].split(",")[1]);
		int c=Integer.parseInt(parsedState[1]);
		int NeoX=Integer.parseInt(parsedState[2].split(",")[0]);
		int NeoY=Integer.parseInt(parsedState[2].split(",")[1]);
		int TeleX=Integer.parseInt(parsedState[3].split(",")[0]);
		int TeleY=Integer.parseInt(parsedState[3].split(",")[1]);
		String[] agentList=parsedState[4].split(",");
		Position[]agents=new Position[agentList.length/2];
		for(int i=0;i<agents.length;i++) {
			agents[i]=new Position(Integer.parseInt(agentList[i*2]),Integer.parseInt(agentList[i*2+1]));
			//System.out.println("agent "+i+" "+agents[i]);
		}
		String[] pillList=parsedState[5].split(",");	
		Position[]pills=new Position[pillList.length/2];
		for(int i=0;i<pills.length;i++) {
			pills[i]=new Position(Integer.parseInt(pillList[i*2]),Integer.parseInt(pillList[i*2+1]));
			//System.out.println("pill "+i+" "+pills[i]);
		}
		String[] padList=parsedState[6].split(",");	
		Position[]startPads=new Position[padList.length/4];
		Position[]endPads=new Position[padList.length/4];
		for(int i=0;i<startPads.length;i++) {
			startPads[i]=new Position(Integer.parseInt(padList[i*4]),Integer.parseInt(padList[i*4+1]));
			//System.out.println("start pad "+i+" "+startPads[i]);
			endPads[i]=new Position(Integer.parseInt(padList[i*4+2]),Integer.parseInt(padList[i*4+3]));
			//System.out.println("end pad "+i+" "+endPads[i]);
		}
		String[]hostageList=parsedState[7].split(",");
	    Position[]hostages=new Position[hostageList.length/3];
	    int[]hostagesDamage=new int[hostageList.length/3];
		for(int i=0;i<hostages.length;i++) {
			hostages[i]=new Position(Integer.parseInt(hostageList[i*3]),Integer.parseInt(hostageList[i*3+1]));
			//System.out.println("Hostage: "+i+" "+hostages[i]);
			hostagesDamage[i]=Integer.parseInt(hostageList[i*3+2]);
			//System.out.println("HostageDamage: "+i+" "+hostagesDamage[i]);
		}
		String[]hostagesState=new String [hostages.length];
		for(int i=0;i<parsedState[8].split(",").length;i++)
			hostagesState[i]=parsedState[8].split(",")[i];
		int NeoDamage=Integer.parseInt(parsedState[9]);
		//ended parsing state
		
		switch ((NeoActions)o) {
		case UP:
			if (NeoY > 0) {
				int agentIndex=getIndex(NeoX,NeoY-1,agents);
				int hostageIndex=getIndex(NeoX,NeoY-1,hostages);
				if(agentIndex==-1&&(hostageIndex==-1||hostagesDamage[hostageIndex]<98))
					NeoY--;
			}
			break;
		case DOWN:
			if (NeoY < rows-1) {
				int agentIndex=getIndex(NeoX,NeoY+1,agents);
				int hostageIndex=getIndex(NeoX,NeoY+1,hostages);
				if(agentIndex==-1&&(hostageIndex==-1||hostagesDamage[hostageIndex]<98))
					NeoY++;
			}
			break;
		case RIGHT:
			if (NeoX < columns-1)
			 {
				int agentIndex=getIndex(NeoX+1,NeoY,agents);
				int hostageIndex=getIndex(NeoX+1,NeoY,hostages);
				if(agentIndex==-1&&(hostageIndex==-1||hostagesDamage[hostageIndex]<98))
					NeoX++;
			}
			break;
		case LEFT:
			if (NeoX > 0)
			{
				int agentIndex=getIndex(NeoX-1,NeoY,agents);
				int hostageIndex=getIndex(NeoX-1,NeoY,hostages);
				if(agentIndex==-1&&(hostageIndex==-1||hostagesDamage[hostageIndex]<98))
					NeoX--;
			}
			break;
		case FLY:
			int endPadIndex = getIndex(NeoX,NeoY,startPads);
			if (endPadIndex >-1) {
				NeoX = endPads[endPadIndex].x;
				NeoY = endPads[endPadIndex].y;
			}
			break;
		case TAKE_PILL:
			int pillIndex = getIndex(NeoX, NeoY,pills);
			if (pillIndex>-1) {
				NeoDamage-=20;
				if(NeoDamage<0)
					NeoDamage=0;
				for(int i=0;i<hostagesDamage.length;i++)
					if(hostagesDamage[i]<100) {
						hostagesDamage[i]-=20;
						if(hostagesDamage[i]<0)
							hostagesDamage[i]=0;
					}
				pills[pillIndex]=null;
			}
			break;
		case KILL_UP:
			int upAgentIndex=getIndex(NeoX,NeoY-1,agents);
			if(upAgentIndex>-1) {
				NeoDamage+=20;
				agents[upAgentIndex]=null;
			}
			else {
				int hostageIndex=getIndex(NeoX,NeoY-1,hostages);
				if(hostageIndex>-1&&hostagesDamage[hostageIndex]>99&&hostagesState[hostageIndex].charAt(0)!='2'&&(TeleX!=NeoX||TeleY!=NeoY-1)) {
					hostagesState[hostageIndex]="2";
					NeoDamage+=20;
				}
			}

			break;
			
		case KILL_DOWN:
			int downAgentIndex=getIndex(NeoX,NeoY+1,agents);
			if(downAgentIndex>-1) {
				NeoDamage+=20;
				agents[downAgentIndex]=null;
			}
			else {
				int hostageIndex=getIndex(NeoX,NeoY+1,hostages);
				if(hostageIndex>-1&&hostagesDamage[hostageIndex]>99&&hostagesState[hostageIndex].charAt(0)!='2'&&(TeleX!=NeoX||TeleY!=NeoY+1)) {
					hostagesState[hostageIndex]="2";
					NeoDamage+=20;
				}
			}
			break;
		case KILL_LEFT:
			int leftAgentIndex=getIndex(NeoX-1,NeoY,agents);
			if(leftAgentIndex>-1) {
				NeoDamage+=20;
				agents[leftAgentIndex]=null;
			}
			else {
				int hostageIndex=getIndex(NeoX-1,NeoY,hostages);
				if(hostageIndex>-1&&hostagesDamage[hostageIndex]>99&&hostagesState[hostageIndex].charAt(0)!='2'&&(TeleX!=NeoX-1||TeleY!=NeoY)) {
					hostagesState[hostageIndex]="2";
					NeoDamage+=20;
				}
			}
	
			break;
		case KILL_RIGHT:
			int rightAgentIndex=getIndex(NeoX+1,NeoY,agents);
			if(rightAgentIndex>-1) {
				NeoDamage+=20;
				agents[rightAgentIndex]=null;
			}
			else {
				int hostageIndex=getIndex(NeoX+1,NeoY,hostages);
				if(hostageIndex>-1&&hostagesDamage[hostageIndex]>99&&hostagesState[hostageIndex].charAt(0)!='2'&&(TeleX!=NeoX+1||TeleY!=NeoY)) {
					hostagesState[hostageIndex]="2";
					NeoDamage+=20;
				}
			}
			break;
		case CARRY:
			int hostageIndex = getIndex(NeoX, NeoY,hostages);
			int currentC=0;
			for(int i=0;i<hostagesState.length;i++)
				if(hostagesState[i].charAt(0)=='1')
					currentC+=1;
			if (currentC < c && hostageIndex > -1 && hostagesDamage[hostageIndex] < 100&&(NeoX!=TeleX||NeoY!=TeleY)&&hostagesState[hostageIndex].charAt(0)!='1') {
				hostagesState[hostageIndex]="1";
			}
			//TODO any carried hostage should have Neo's position at all times
			break;
		case DROP:
			if (NeoX == TeleX && NeoY == TeleY) {
				//find a hostage to drop
				for(int i=0;i<hostagesState.length;i++) {
					if (hostagesState[i].charAt(0) =='1') {
						hostagesState[i] = "0";
						//TODO ask if we can drop all hostages at once
						break;
					}
				}
			}
			break;
		}
		//TODO the effect of passing a time tick
		//newState=tick(newState); after changes from action in additon to automatic changes each time tick
		//updating Neo's position and damage
		nextParsedState[2]=NeoX+","+NeoY;
		nextParsedState[9]=NeoDamage+"";
		//updating hostages
		String[]hostagesJoiner=new String[hostages.length*3] ;
		for(int i=0;i<hostages.length;i++) {
			hostagesJoiner[i*3]=hostages[i].x+"";
			hostagesJoiner[i*3+1]=hostages[i].y+"";
			hostagesJoiner[i*3+2]=hostagesDamage[i]+"";
		}
		nextParsedState[7]=String.join(",", hostagesJoiner);
		//updating pills
		nextParsedState[5]="";
		for(int i=0;i<pills.length;i++) {
			if(pills[i]!=null) {
				nextParsedState[5]+=pills[i].x+","+pills[i].y;
				nextParsedState[5]+=(i<pills.length-1)?",":"";
			}
		}
		//updating hostage states
		nextParsedState[8]=String.join(",", hostagesState);
		//updating agents
		nextParsedState[4]="";
		for(int i=0;i<agents.length;i++) {
			if(agents[i]!=null) {
				nextParsedState[4]+=agents[i].x+","+agents[i].y;
				nextParsedState[4]+=(i<agents.length-1)?",":"";
			}
		}
		return String.join(";", nextParsedState);
	}
	
	
	public void tick() {
		
	}

	private void carryHostage(int hostageIndex) {
		// TODO Auto-generated method stub
		
	}
//	private int findHosatge(int neoX, int neoY) {
//		for (int i=0;i<hostages.length;i++) {
//			if(hostages[i].x == neoX && hostages[i].y == neoY)
//				return i;
//		}
//		return -1;
//	}

	
	private int getIndex(int neoX, int neoY,Position[]positions) {
		for (int i=0;i<positions.length;i++) {
			if(positions[i].x == neoX && positions[i].y == neoY)
				return i;
		}
		return -1;
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
		grid+=rows+","+columns+";";
		//Max members Neo can carry at a time
		int c=(int) (Math.random()*4+1); 
		grid+=c+";";
		//Neo's position
		Position neo=generatePosition(rows,columns,filled,gridView,"Neo");
		grid+=neo+";";
		//telephone booth position
		Position telephone=generatePosition(rows,columns,filled,gridView,"TB"+" ");
		grid+=telephone+";";
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
		grid+=agentsStr+pillsStr+padsStr+hostagesStr;
//		visualization
//		for(int i =-1;i<columns;i++)
//		{
//			System.out.print(i+"     ");
//		}
//		System.out.println();
//		for(int i=0;i<gridView.length;i++)
//		{
//			
//			System.out.print(i+": ");
//			for(int j=0;j<gridView[i].length;j++)
//			{
//				System.out.print(gridView[i][j]+" ");
//			}
//			System.out.println();
//		}
		return(grid);
	}
	
	public static String solve(String grid, String strategy, boolean visualize) {
		return("Solve");
	}
	
	public static void main(String[] args) {
		String g=genGrid();
		//System.out.println(g);
		Matrix m=new Matrix(g);
		//System.out.println(m.stateSpace(m.initialState,NeoActions.TAKE_PILL));
		System.out.println();
		//System.out.println("13,11;3;11,8;7,6;9,7,9,1,10,10,12,1;11,8,1,1;7,10,4,3,4,3,7,10,1,3,3,5,3,5,1,3,6,3,6,5,6,5,6,3,0,9,12,8,12,8,0,9,9,4,2,8,2,8,9,4,9,9,6,6,6,6,9,9,3,6,4,0,4,0,3,6,11,9,4,4,4,4,11,9,12,0,12,4,12,4,12,0,3,4,2,3,2,3,3,4,10,4,0,0,0,0,10,4,12,6,8,9,8,9,12,6,5,0,9,5,9,5,5,0,5,2,10,0,10,0,5,2,9,8,6,0,6,0,9,8,8,2,3,0,3,0,8,2,1,2,6,7,6,7,1,2,6,8,10,9,10,9,6,8,1,6,1,5,1,5,1,6,11,5,2,2,2,2,11,5,4,10,5,6,5,6,4,10,8,6,7,8,7,8,8,6,7,5,5,5,5,5,7,5,7,1,9,6,9,6,7,1,3,3,11,3,11,3,3,3,8,3,6,1,6,1,8,3,5,10,0,10,0,10,5,10,0,2,11,4,11,4,0,2,8,0,2,6,2,6,8,0,4,7,0,1,0,1,4,7,9,10,12,10,12,10,9,10,11,6,8,10,8,10,11,6,11,1,4,1,4,1,11,1,8,8,1,7,1,7,8,8,1,8,7,3,7,3,1,8,12,3,7,2,7,2,12,3,11,2,2,4,2,4,11,2,5,3,10,3,10,3,5,3,3,9,2,7,2,7,3,9,9,0,10,7,10,7,9,0,8,4,10,6,10,6,8,4,3,2,1,0,1,0,3,2,11,0,8,7,8,7,11,0,10,1,7,4,7,4,10,1,8,1,4,8,4,8,8,1,5,4,12,2,12,2,5,4,11,7,1,10,1,10,11,7,1,9,12,7,12,7,1,9,2,9,2,0,2,0,2,9,7,7,3,10,3,10,7,7,12,5,5,1,5,1,12,5,0,7,4,6,4,6,0,7,0,3,4,5,4,5,0,3,6,2,10,8,10,8,6,2,9,3,3,1,3,1,9,3,1,4,3,8,3,8,1,4,0,5,8,5,8,5,0,5;0,4,59,4,2,36,11,7,100;0,0,0;0");
		//System.out.println(m.stateSpace("13,11;3;11,8;7,6;9,7,9,1,10,10,12,1;11,8,1,1;7,10,4,3,4,3,7,10,1,3,3,5,3,5,1,3,6,3,6,5,6,5,6,3,0,9,12,8,12,8,0,9,9,4,2,8,2,8,9,4,9,9,6,6,6,6,9,9,3,6,4,0,4,0,3,6,11,9,4,4,4,4,11,9,12,0,12,4,12,4,12,0,3,4,2,3,2,3,3,4,10,4,0,0,0,0,10,4,12,6,8,9,8,9,12,6,5,0,9,5,9,5,5,0,5,2,10,0,10,0,5,2,9,8,6,0,6,0,9,8,8,2,3,0,3,0,8,2,1,2,6,7,6,7,1,2,6,8,10,9,10,9,6,8,1,6,1,5,1,5,1,6,11,5,2,2,2,2,11,5,4,10,5,6,5,6,4,10,8,6,7,8,7,8,8,6,7,5,5,5,5,5,7,5,7,1,9,6,9,6,7,1,3,3,11,3,11,3,3,3,8,3,6,1,6,1,8,3,5,10,0,10,0,10,5,10,0,2,11,4,11,4,0,2,8,0,2,6,2,6,8,0,4,7,0,1,0,1,4,7,9,10,12,10,12,10,9,10,11,6,8,10,8,10,11,6,11,1,4,1,4,1,11,1,8,8,1,7,1,7,8,8,1,8,7,3,7,3,1,8,12,3,7,2,7,2,12,3,11,2,2,4,2,4,11,2,5,3,10,3,10,3,5,3,3,9,2,7,2,7,3,9,9,0,10,7,10,7,9,0,8,4,10,6,10,6,8,4,3,2,1,0,1,0,3,2,11,0,8,7,8,7,11,0,10,1,7,4,7,4,10,1,8,1,4,8,4,8,8,1,5,4,12,2,12,2,5,4,11,7,1,10,1,10,11,7,1,9,12,7,12,7,1,9,2,9,2,0,2,0,2,9,7,7,3,10,3,10,7,7,12,5,5,1,5,1,12,5,0,7,4,6,4,6,0,7,0,3,4,5,4,5,0,3,6,2,10,8,10,8,6,2,9,3,3,1,3,1,9,3,1,4,3,8,3,8,1,4,0,5,8,5,8,5,0,5;0,4,59,4,2,36,11,7,100;0,0,0;0", NeoActions.KILL_UP));
	}
	
}
