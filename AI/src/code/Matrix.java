package code;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;


public class Matrix extends SearchProblem {
	String grid;
	int nodesExp;
	HashSet<String>generatedStates;
	boolean existAgent;
	boolean existHostage;

	Matrix(String grid) {
		this.grid=grid;
		this.operators=Arrays.asList(NeoActions.values());
		this.initialState=this.grid+";";
		nodesExp=0;
		generatedStates = new HashSet<String>();
		existAgent=false;
		existHostage=false;
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
		////System.out.println(initialState);
		//Adding the initial state to generates states
		generatedStates.add(removeHostageDamage(this.initialState));
		
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
		int NeoR=Integer.parseInt(parsedState[2].split(",")[0]);
		int NeoC=Integer.parseInt(parsedState[2].split(",")[1]);
		int TeleX=Integer.parseInt(parsedState[3].split(",")[0]);
		int TeleY=Integer.parseInt(parsedState[3].split(",")[1]);
		String[] agentList=parsedState[4].split(",");
		Position[]agents=new Position[agentList.length/2];
		for(int i=0;i<agents.length;i++) {
			agents[i]=new Position(Integer.parseInt(agentList[i*2]),Integer.parseInt(agentList[i*2+1]));
			////System.out.println("agent "+i+" "+agents[i]);
		}
		String[] pillList=parsedState[5].split(",");	
		Position[]pills=new Position[pillList.length/2];
		for(int i=0;i<pills.length;i++) {
			pills[i]=new Position(Integer.parseInt(pillList[i*2]),Integer.parseInt(pillList[i*2+1]));
			////System.out.println("pill "+i+" "+pills[i]);
		}
		String[] padList=parsedState[6].split(",");	
		Position[]startPads=new Position[padList.length/4];
		Position[]endPads=new Position[padList.length/4];
		for(int i=0;i<startPads.length;i++) {
			startPads[i]=new Position(Integer.parseInt(padList[i*4]),Integer.parseInt(padList[i*4+1]));
			// ////System.out.println("start pad "+i+" "+startPads[i]);
			endPads[i]=new Position(Integer.parseInt(padList[i*4+2]),Integer.parseInt(padList[i*4+3]));
			// ////System.out.println("end pad "+i+" "+endPads[i]);
		}
		String[]hostageList=parsedState[7].split(",");
	    Position[]hostages=new Position[hostageList.length/3];
	    int[]hostagesDamage=new int[hostageList.length/3];
		for(int i=0;i<hostages.length;i++) {
			hostages[i]=new Position(Integer.parseInt(hostageList[i*3]),Integer.parseInt(hostageList[i*3+1]));
			////System.out.println("Hostage: "+i+" "+hostages[i]);
			hostagesDamage[i]=Integer.parseInt(hostageList[i*3+2]);
			////System.out.println("HostageDamage: "+i+" "+hostagesDamage[i]);
		}
		int[]hostagesState=new int [hostages.length];
		for(int i=0;i<parsedState[8].split(",").length;i++)
			hostagesState[i]=Integer.parseInt(parsedState[8].split(",")[i]);
		int NeoDamage=Integer.parseInt(parsedState[9]);
		//ended parsing state
		
		if(NeoR!=TeleX||NeoC!=TeleY)//not at the telephone booth
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
		switch((NeoActions)o)
		{
		case UP:
		case DOWN:
		case RIGHT:
		case LEFT:
		case FLY:
			return 5;
		case TAKEPILL:
			return 2;
			
		case DROP:
		case CARRY:
			return 1;
		case KILL:
			return 15;
			
		}
		return -1;
	}
	
	@Override
	public String stateSpace(String state, Operator o) {
		//parse the state
		String[]parsedState=state.split(";");
		String []nextParsedState=parsedState.clone();
		int rows=Integer.parseInt(parsedState[0].split(",")[0]);
		int columns=Integer.parseInt(parsedState[0].split(",")[1]);
		int c=Integer.parseInt(parsedState[1]);
		int NeoR=Integer.parseInt(parsedState[2].split(",")[0]);
		int NeoC=Integer.parseInt(parsedState[2].split(",")[1]);
		int TeleX=Integer.parseInt(parsedState[3].split(",")[0]);
		int TeleY=Integer.parseInt(parsedState[3].split(",")[1]);
		String[] agentList=parsedState[4].split(",");
		Position[]agents=new Position[agentList.length/2];
		for(int i=0;i<agents.length;i++) {
			agents[i]=new Position(Integer.parseInt(agentList[i*2]),Integer.parseInt(agentList[i*2+1]));
			//////System.out.println("agent "+i+" "+agents[i]);
		}
		String[] pillList=parsedState[5].split(",");	
		Position[]pills=new Position[pillList.length/2];
		for(int i=0;i<pills.length;i++) {
			pills[i]=new Position(Integer.parseInt(pillList[i*2]),Integer.parseInt(pillList[i*2+1]));
			//////System.out.println("pill "+i+" "+pills[i]);
		}
		String[] padList=parsedState[6].split(",");	
		Position[]startPads=new Position[padList.length/4];
		Position[]endPads=new Position[padList.length/4];
		for(int i=0;i<startPads.length;i++) {
			startPads[i]=new Position(Integer.parseInt(padList[i*4]),Integer.parseInt(padList[i*4+1]));
			//////System.out.println("start pad "+i+" "+startPads[i]);
			endPads[i]=new Position(Integer.parseInt(padList[i*4+2]),Integer.parseInt(padList[i*4+3]));
			//////System.out.println("end pad "+i+" "+endPads[i]);
		}
		String[]hostageList=parsedState[7].split(",");
	    Position[]hostages=new Position[hostageList.length/3];
	    int[]hostagesDamage=new int[hostageList.length/3];
		for(int i=0;i<hostages.length;i++) {
			hostages[i]=new Position(Integer.parseInt(hostageList[i*3]),Integer.parseInt(hostageList[i*3+1]));
			//////System.out.println("Hostage: "+i+" "+hostages[i]);
			hostagesDamage[i]=Integer.parseInt(hostageList[i*3+2]);
			//////System.out.println("HostageDamage: "+i+" "+hostagesDamage[i]);
		}
		String[]hostagesState=new String [hostages.length];
		for(int i=0;i<parsedState[8].split(",").length;i++)
			hostagesState[i]=parsedState[8].split(",")[i];
		int NeoDamage=Integer.parseInt(parsedState[9]);
		//ended parsing state
		existAgent=false;
		existHostage=false;
		boolean stateChanged = false;
		//If there is a hostage in Neo's position, Neo isn't allowed to do any action other than carry
		int hostageIndexEdgeCase = getIndex(NeoR, NeoC,hostages);
		
		if(hostageIndexEdgeCase >-1) {
			if( hostagesDamage[hostageIndexEdgeCase]>=98 && (NeoActions)o!= NeoActions.CARRY)
				return null;
			
		}
//		// effect of time 
//				for (int i=0;i<hostages.length;i++){
//					if(!(hostages[i].x == TeleX && hostages[i].y == TeleY && hostagesState[i].charAt(0) == '0') 
//						&& hostagesDamage[i]<100){
//						hostagesDamage[i] += 2;
//						if (hostagesDamage[i] > 99)
//							hostagesDamage[i] = 100;
//					}
//				}
		switch ((NeoActions)o) {
		case UP:
			if (NeoR > 0) {
				int agentIndex=getIndex(NeoR-1,NeoC,agents);
				int hostageIndex=getIndex(NeoR-1,NeoC,hostages);
				if(agentIndex==-1&&(hostageIndex==-1||hostagesDamage[hostageIndex]<98)){
					NeoR--;
					updateCarriedHostagesLocation(NeoR, NeoC, hostages, hostagesState);
					stateChanged = true;
				}
			}
			break;
		case DOWN:
			if (NeoR < rows-1) {
				int agentIndex=getIndex(NeoR+1,NeoC,agents);
				int hostageIndex=getIndex(NeoR+1,NeoC,hostages);
				if(agentIndex==-1&&(hostageIndex==-1||hostagesDamage[hostageIndex]<98)){
					NeoR++;
					updateCarriedHostagesLocation(NeoR, NeoC, hostages, hostagesState);
					stateChanged = true;
				}
			}
			break;
		case RIGHT:
			if (NeoC < columns-1)
			 {
				int agentIndex=getIndex(NeoR,NeoC+1,agents);
				int hostageIndex=getIndex(NeoR,NeoC+1,hostages);
				if(agentIndex==-1&&(hostageIndex==-1||hostagesDamage[hostageIndex]<98)){
					NeoC++;
					updateCarriedHostagesLocation(NeoR, NeoC, hostages, hostagesState);
					stateChanged = true;
				}
			}
			break;
		case LEFT:
			if (NeoC > 0)
			{
				int agentIndex=getIndex(NeoR,NeoC-1,agents);
				int hostageIndex=getIndex(NeoR,NeoC-1,hostages);
				if(agentIndex==-1&&(hostageIndex==-1||hostagesDamage[hostageIndex]<98)){
					NeoC--;
					updateCarriedHostagesLocation(NeoR, NeoC, hostages, hostagesState);
					stateChanged = true;
				}
			}
			break;
		case FLY:
			int endPadIndex = getIndex(NeoR,NeoC,startPads);
			if (endPadIndex >-1) {
				NeoR = endPads[endPadIndex].x;
				NeoC = endPads[endPadIndex].y;
				updateCarriedHostagesLocation(NeoR, NeoC, hostages, hostagesState);
				stateChanged = true;
			}
			break;
		case TAKEPILL:
			int pillIndex = getIndex(NeoR, NeoC,pills);
			if (pillIndex>-1) {
				NeoDamage-=20;
				if(NeoDamage<0)
					NeoDamage=0;
				for(int i=0;i<hostagesDamage.length;i++)
					if(hostagesDamage[i]<100) {
						hostagesDamage[i]-=22;
						if(hostagesDamage[i]<0)
							hostagesDamage[i]=0;
					}
				pills[pillIndex]=null;
				stateChanged = true;
			}
			break;

		case KILL:
			
			int upAgentIndex=getIndex(NeoR,NeoC-1,agents);
			int downAgentIndex=getIndex(NeoR,NeoC+1,agents);
			int leftAgentIndex=getIndex(NeoR-1,NeoC,agents);
			int rightAgentIndex=getIndex(NeoR+1,NeoC,agents);
			boolean killed = false;
			if(upAgentIndex>-1 && NeoDamage<80) {
				
				killed = true;
				existAgent=true;
				agents[upAgentIndex]=null;
			}
			else {
				int hostageIndex=getIndex(NeoR,NeoC-1,hostages);
				if(hostageIndex>-1&&hostagesDamage[hostageIndex]>99&&hostagesState[hostageIndex].charAt(0)!='2'
					&&(TeleX!=NeoR||TeleY!=NeoC-1)&& NeoDamage<80) {
					hostagesState[hostageIndex]="2";
					killed = true;
					existHostage=true;
				}
			}
			if(downAgentIndex>-1&& NeoDamage<80) {
				killed = true;
				existAgent=true;
				agents[downAgentIndex]=null;
			}
			else {
				int hostageIndex=getIndex(NeoR,NeoC+1,hostages);
				if(hostageIndex>-1&&hostagesDamage[hostageIndex]>99&&hostagesState[hostageIndex].charAt(0)!='2'
					&&(TeleX!=NeoR||TeleY!=NeoC+1)&& NeoDamage<80) {
					hostagesState[hostageIndex]="2";
					killed = true;
					existHostage=true;
				}
			}
			if(leftAgentIndex>-1&& NeoDamage<80) {
				killed = true;
				existAgent=true;
				agents[leftAgentIndex]=null;
			}
			else {
				int hostageIndex=getIndex(NeoR-1,NeoC,hostages);
				if(hostageIndex>-1&&hostagesDamage[hostageIndex]>99&&hostagesState[hostageIndex].charAt(0)!='2'&&(TeleX!=NeoR-1||TeleY!=NeoC)&& NeoDamage<80) {
					hostagesState[hostageIndex]="2";
					killed = true;
					existHostage=true;
				}
			}
			if(rightAgentIndex>-1&& NeoDamage<80) {
				killed = true;
				existAgent=true;
				agents[rightAgentIndex]=null;
			}
			else {
				int hostageIndex=getIndex(NeoR+1,NeoC,hostages);
				if( NeoDamage<80&&hostageIndex>-1&&hostagesDamage[hostageIndex]>99&&hostagesState[hostageIndex].charAt(0)!='2'&&(TeleX!=NeoR+1||TeleY!=NeoC)) {
					hostagesState[hostageIndex]="2";
					killed = true;
					existHostage=true;
				}
			}
			if (killed)
				NeoDamage += 20;
			stateChanged = killed;
			break;

		case CARRY:
			int hostageIndex = getIndex(NeoR, NeoC,hostages);
			int currentC=0;
			for(int i=0;i<hostagesState.length;i++)
				if(hostagesState[i].charAt(0)=='1')
					currentC+=1;
			if (currentC < c && hostageIndex > -1 && hostagesDamage[hostageIndex] < 100&&(NeoR!=TeleX||NeoC!=TeleY)
				&&hostagesState[hostageIndex].charAt(0)!='1') {
				hostagesState[hostageIndex]="1";
				stateChanged = true;
			}
			break;
		case DROP:
			if (NeoR == TeleX && NeoC == TeleY) {
				//find a hostage to drop
				for(int i=0;i<hostagesState.length;i++) {
					if (hostagesState[i].charAt(0) =='1') {
						hostagesState[i] = "0";
						stateChanged = true;
					}
				}
			}
			break;
		}
		// effect of time 
		for (int i=0;i<hostages.length;i++){
			if(!(hostages[i].x == TeleX && hostages[i].y == TeleY && hostagesState[i].charAt(0) == '0') 
				&& hostagesDamage[i]<100){
				hostagesDamage[i] += 2;
				if (hostagesDamage[i] > 99)
					hostagesDamage[i] = 100;
			}
		}
		//newState=(newState); after changes from action in additon to automatic changes each time 
		//updating Neo's position and damage
		if (!stateChanged)
			return null;
		
		nextParsedState[2]=NeoR+","+NeoC;
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
	private static String removeHostageDamage(String state)
	{
		String[]parsedState=state.split(";");
	
		String[]hostageList=parsedState[7].split(",");
	    String[]hostages=new String[hostageList.length/3];
		for(int i=0;i<hostages.length;i++) {
			hostages[i]=hostageList[i*3]+","+hostageList[i*3+1];
		}
		parsedState[7]=String.join(",", hostages);
		return String.join(";", parsedState);
	}
	
	
	private void updateCarriedHostagesLocation(int NeoR, int NeoC, Position[] hostages, String[] hostagesState) {
		for (int i = 0;i<hostages.length;i++){
			if (hostagesState[i].charAt(0) == '1'){
				hostages[i].x = NeoR;
				hostages[i].y = NeoC;
			}
		}
	}
	
	private int getIndex(int NeoR, int NeoC,Position[]positions) {
		for (int i=0;i<positions.length;i++) {
			if(positions[i].x == NeoR && positions[i].y == NeoC)
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

	public MatrixSearchTreeNode expand(MatrixSearchTreeNode node, Operator o){
        if (!allowExpandAction(node.operator, o))
            return null;
        String nextState = stateSpace(node.state, o);
        if (nextState == null||this.generatedStates.contains(removeHostageDamage(nextState)))
			return null;
        this.generatedStates.add(removeHostageDamage(nextState));
        int addedCost = pathCost(o);
        if(((NeoActions)o) == NeoActions.KILL)
        {
//        	if(existHostage && existAgent)
//        		addedCost=14;
//        	else if(existHostage)
//        			addedCost=6;
        	String deathsKills = getDeathsKills(node.state, nextState);
        	String [] deathsKillsArray= deathsKills.split(";");
        	int hostagesN=Integer.parseInt(deathsKillsArray[0]);
        	int killsN =Integer.parseInt(deathsKillsArray[1]);
        	int agentsN = killsN-hostagesN;
        	if(hostagesN>0 && agentsN>0)
        		addedCost=14;
        	else if(hostagesN>0)
        		addedCost=10;
        	
        	
        }
        MatrixSearchTreeNode mn=new MatrixSearchTreeNode(nextState, node, o, node.depth+1, node.pathCost+addedCost);
    
		return mn;
        
    }

    private static boolean allowExpandAction(Operator parent, Operator child){
        if (parent == NeoActions.FLY && child == NeoActions.FLY)
            return false;
        if (parent == NeoActions.UP && child == NeoActions.DOWN)
            return false;
        if (parent == NeoActions.DOWN && child == NeoActions.UP)
            return false;
        if (parent == NeoActions.LEFT && child == NeoActions.RIGHT)
            return false;
        if (parent == NeoActions.RIGHT && child == NeoActions.LEFT)
            return false;
        return true;
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
//			////System.out.print(i+"     ");
//		}
//		////System.out.println();
//		for(int i=0;i<gridView.length;i++)
//		{
//			
//			////System.out.print(i+": ");
//			for(int j=0;j<gridView[i].length;j++)
//			{
//				////System.out.print(gridView[i][j]+" ");
//			}
//			////System.out.println();
//		}
		return(grid);
	}
	
	public static String solve(String grid, String strategy, boolean visualize) {
		Matrix m = new Matrix(grid);
		String initialState=grid+";";
		//adding a carry flag to each hostage
		int hostageCount=grid.split(";")[7].split(",").length/3;
		for(int i=0;i<hostageCount;i++) {
			initialState+="0";
			if(i<hostageCount-1)
				initialState+=",";
			else
				initialState+=";";
		}
		//adding Neo's Damage
		initialState+="0";//Neo's damage
		MatrixSearchTreeNode solNode = null;
		MatrixSearchTreeNode root = new MatrixSearchTreeNode(initialState, null, null, 0, 0);
		switch (strategy) {
			case "BF":
				solNode = BFS(root, m);
				break;
			case "DF":
				solNode=DFS(root,m);
				break;
			case "ID":
				solNode=IDS(root,m);				
				break;
			case "UC":
				solNode=UCS(root,m);
				
				break;
			case "GR1":
				
				break;
			case "GR2":
				
				break;
			case "AS1":
				
				break;
			case "AS2":
				
				break;
		}
		if(solNode==null)
			return "No Solution";

		String solve=getSolutionSequence(solNode);
		solve+=getDeathsKills(initialState,solNode.state);
		solve+=m.nodesExp+"";
		return(solve);
	}
	
	private static MatrixSearchTreeNode UCS(MatrixSearchTreeNode root, Matrix m) {
		PriorityQueue<MatrixSearchTreeNode> queue = new PriorityQueue<MatrixSearchTreeNode>();
		List<NeoActions> operators = Arrays.asList(NeoActions.values());
		queue.add(root);
		do{
			MatrixSearchTreeNode node = queue.remove();
//			System.out.println(node);
			m.nodesExp++;
			if(m.goalTest(node.state))
				return node;
			for(Operator o : operators){
				MatrixSearchTreeNode newNode = m.expand(node, o);
				if (newNode != null)
					queue.add(newNode);
			}
//			printQueue(queue);

			//System.out.println(m.nodesExp);
		}
		while(!queue.isEmpty());
		return null;
		
	}
	private static String getDeathsKills(String initialState, String state) {
		String s="";
		String[]initAgents=initialState.split(";")[4].split(",");
		String[]agents=state.split(";")[4].split(",");
		int kills=(initAgents.length/2-agents.length/2);
		String [] hostageState=state.split(";")[8].split(",");
		for(int i=0;i<hostageState.length;i++)
			if(hostageState[i].charAt(0)=='2')
				kills++;
		String[]hostages=state.split(";")[7].split(",");
		int deaths=0;
		for(int i=2;i<hostages.length;i+=3){
			if(Integer.parseInt(hostages[i])>99)
				deaths++;
		}
		s+=deaths+";"+kills+";";
		return s;
	}
	private static String getSolutionSequence(MatrixSearchTreeNode solNode) {
		MatrixSearchTreeNode curNode=solNode;
		System.out.println(curNode.state);
		String path=((NeoActions)curNode.operator).name().toLowerCase()+";";
		curNode=(MatrixSearchTreeNode) curNode.parent;
		while(curNode!=null) {
			System.out.println(curNode.state);
			if(curNode.parent!=null)
				if((NeoActions)curNode.operator == NeoActions.TAKEPILL)
					path="takePill"+","+path;
				else
					path=((NeoActions)curNode.operator).name().toLowerCase()+","+path;
			curNode=(MatrixSearchTreeNode) curNode.parent;
			
		}
		return path;
	}
	private static MatrixSearchTreeNode BFS(MatrixSearchTreeNode root, Matrix m) {
		LinkedList<MatrixSearchTreeNode> queue = new LinkedList<MatrixSearchTreeNode>();
		List<NeoActions> operators = Arrays.asList(NeoActions.values());
		queue.add(root);
		do{
			MatrixSearchTreeNode node = queue.removeFirst();
			m.nodesExp++;
			if(m.goalTest(node.state))
				return node;
			for(Operator o : operators){
				MatrixSearchTreeNode newNode = m.expand(node, o);
				if (newNode != null)
					queue.addLast(newNode);
			}
		}
		while(!queue.isEmpty());
		return null;
	}
	private static MatrixSearchTreeNode DFS(MatrixSearchTreeNode root, Matrix m) {
		Stack<MatrixSearchTreeNode> stack = new Stack<MatrixSearchTreeNode>();
		List<NeoActions> operators = Arrays.asList(NeoActions.values());
		stack.push(root);
		do{
			MatrixSearchTreeNode node = stack.pop();
			m.nodesExp++;
			if(m.goalTest(node.state))
				return node;
			for(Operator o : operators){
				MatrixSearchTreeNode newNode = m.expand(node, o);
				if (newNode != null)
					stack.push(newNode);
			}
		}
		while(!stack.isEmpty());
		return null;
	}
	
	private static MatrixSearchTreeNode IDS(MatrixSearchTreeNode root, Matrix m) {
		for(int l=0;l<Integer.MAX_VALUE;l++) {
			m.generatedStates=new HashSet<String>();
			m.generatedStates.add(removeHostageDamage(m.initialState));
			Stack<MatrixSearchTreeNode> stack = new Stack<MatrixSearchTreeNode>();
			List<NeoActions> operators = Arrays.asList(NeoActions.values());
			stack.push(root);
			MatrixSearchTreeNode node;
			do{
				node = stack.pop();
				m.nodesExp++;
				if(m.goalTest(node.state))
					return node;
				for(Operator o : operators){
					MatrixSearchTreeNode newNode = m.expand(node, o);
					if (newNode != null)
						stack.push(newNode);
				}
				//System.out.println(m.nodesExp);
			}
			while(!stack.isEmpty()&&node.depth<=l);
		}
		return null;
	}
	private static void printQueue(PriorityQueue q)
	{
		Object[] arr =q.toArray();
	
		for(int i=0;i<arr.length;i++)
		{
			System.out.print((MatrixSearchTreeNode)arr[i]+" ");
		}
		System.out.println();
	}
	public static void main(String[] args) {
		String g=genGrid();
		String grid0 = "5,5;2;4,3;2,1;2,0,0,4,0,3,0,1;3,1,3,2;4,4,3,3,3,3,4,4;4,0,17,1,2,54,0,0,46,4,1,22";
		String gSmall="3,3;2;0,0;2,2;1,1;1,0;0,2,2,0,2,0,0,2;1,2,20";
		String grid3 = "6,6;2;2,4;2,2;0,4,1,4,3,0,4,2;0,1,1,3;4,4,3,1,3,1,4,4;0,0,94,1,2,38,4,1,76,4,0,80";
		String example="5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80";
		String grid7 = "7,7;4;3,3;0,2;0,1,1,0,1,1,1,2,2,0,2,2,2,4,2,6,1,4;5,5,5,0;5,1,2,5,2,5,5,1;0,0,98,3,2,98,4,4,98,0,3,98,0,4,98,0,5,98,5,4,98";
		String grid1 = "5,5;1;1,4;1,0;0,4;0,0,2,2;3,4,4,2,4,2,3,4;0,2,32,0,1,38";
		String grid10 = "6,6;1;2,2;2,4;0,1,1,0,3,0,4,1,4,3,3,4,1,4,0,3,1,5;0,2;1,3,4,2,4,2,1,3;0,5,90,1,2,92,4,4,2,5,5,1,1,1,98";

		//System.out.println(example);
		//System.out.println("---------------------------------------------------------------");
		System.out.println(solve(grid10,"BF",false));
		//////System.out.println(m.stateSpace(m.initialState,NeoActions.TAKE_PILL));
		//////System.out.println();
		//////System.out.println("13,11;3;11,8;7,6;9,7,9,1,10,10,12,1;11,8,1,1;7,10,4,3,4,3,7,10,1,3,3,5,3,5,1,3,6,3,6,5,6,5,6,3,0,9,12,8,12,8,0,9,9,4,2,8,2,8,9,4,9,9,6,6,6,6,9,9,3,6,4,0,4,0,3,6,11,9,4,4,4,4,11,9,12,0,12,4,12,4,12,0,3,4,2,3,2,3,3,4,10,4,0,0,0,0,10,4,12,6,8,9,8,9,12,6,5,0,9,5,9,5,5,0,5,2,10,0,10,0,5,2,9,8,6,0,6,0,9,8,8,2,3,0,3,0,8,2,1,2,6,7,6,7,1,2,6,8,10,9,10,9,6,8,1,6,1,5,1,5,1,6,11,5,2,2,2,2,11,5,4,10,5,6,5,6,4,10,8,6,7,8,7,8,8,6,7,5,5,5,5,5,7,5,7,1,9,6,9,6,7,1,3,3,11,3,11,3,3,3,8,3,6,1,6,1,8,3,5,10,0,10,0,10,5,10,0,2,11,4,11,4,0,2,8,0,2,6,2,6,8,0,4,7,0,1,0,1,4,7,9,10,12,10,12,10,9,10,11,6,8,10,8,10,11,6,11,1,4,1,4,1,11,1,8,8,1,7,1,7,8,8,1,8,7,3,7,3,1,8,12,3,7,2,7,2,12,3,11,2,2,4,2,4,11,2,5,3,10,3,10,3,5,3,3,9,2,7,2,7,3,9,9,0,10,7,10,7,9,0,8,4,10,6,10,6,8,4,3,2,1,0,1,0,3,2,11,0,8,7,8,7,11,0,10,1,7,4,7,4,10,1,8,1,4,8,4,8,8,1,5,4,12,2,12,2,5,4,11,7,1,10,1,10,11,7,1,9,12,7,12,7,1,9,2,9,2,0,2,0,2,9,7,7,3,10,3,10,7,7,12,5,5,1,5,1,12,5,0,7,4,6,4,6,0,7,0,3,4,5,4,5,0,3,6,2,10,8,10,8,6,2,9,3,3,1,3,1,9,3,1,4,3,8,3,8,1,4,0,5,8,5,8,5,0,5;0,4,59,4,2,36,11,7,100;0,0,0;0");
		//////System.out.println(m.stateSpace("13,11;3;11,8;7,6;9,7,9,1,10,10,12,1;11,8,1,1;7,10,4,3,4,3,7,10,1,3,3,5,3,5,1,3,6,3,6,5,6,5,6,3,0,9,12,8,12,8,0,9,9,4,2,8,2,8,9,4,9,9,6,6,6,6,9,9,3,6,4,0,4,0,3,6,11,9,4,4,4,4,11,9,12,0,12,4,12,4,12,0,3,4,2,3,2,3,3,4,10,4,0,0,0,0,10,4,12,6,8,9,8,9,12,6,5,0,9,5,9,5,5,0,5,2,10,0,10,0,5,2,9,8,6,0,6,0,9,8,8,2,3,0,3,0,8,2,1,2,6,7,6,7,1,2,6,8,10,9,10,9,6,8,1,6,1,5,1,5,1,6,11,5,2,2,2,2,11,5,4,10,5,6,5,6,4,10,8,6,7,8,7,8,8,6,7,5,5,5,5,5,7,5,7,1,9,6,9,6,7,1,3,3,11,3,11,3,3,3,8,3,6,1,6,1,8,3,5,10,0,10,0,10,5,10,0,2,11,4,11,4,0,2,8,0,2,6,2,6,8,0,4,7,0,1,0,1,4,7,9,10,12,10,12,10,9,10,11,6,8,10,8,10,11,6,11,1,4,1,4,1,11,1,8,8,1,7,1,7,8,8,1,8,7,3,7,3,1,8,12,3,7,2,7,2,12,3,11,2,2,4,2,4,11,2,5,3,10,3,10,3,5,3,3,9,2,7,2,7,3,9,9,0,10,7,10,7,9,0,8,4,10,6,10,6,8,4,3,2,1,0,1,0,3,2,11,0,8,7,8,7,11,0,10,1,7,4,7,4,10,1,8,1,4,8,4,8,8,1,5,4,12,2,12,2,5,4,11,7,1,10,1,10,11,7,1,9,12,7,12,7,1,9,2,9,2,0,2,0,2,9,7,7,3,10,3,10,7,7,12,5,5,1,5,1,12,5,0,7,4,6,4,6,0,7,0,3,4,5,4,5,0,3,6,2,10,8,10,8,6,2,9,3,3,1,3,1,9,3,1,4,3,8,3,8,1,4,0,5,8,5,8,5,0,5;0,4,59,4,2,36,11,7,100;0,0,0;0", NeoActions.KILL_UP));
	}
	
}
