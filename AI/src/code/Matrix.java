package code;

// import java.lang.management.ManagementFactory;
// import java.lang.management.MemoryMXBean;
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
	int rows;
	int columns;
	int c;
	int TeleX;
	int TeleY;
	HashSet<String>generatedStates;
	Position[] startPads;
	Position[] endPads;
	static String stat_initialState;
	static int weight=((int)Math.pow(2, 10));
	Matrix(String grid) {
		this.grid=grid;
		this.operators=Arrays.asList(NeoActions.values());
		this.initialState=this.grid+";";
		String[]parsedState=grid.split(";");
		nodesExp=0;
		this.rows=Integer.parseInt(parsedState[0].split(",")[0]);
		this.columns=Integer.parseInt(parsedState[0].split(",")[1]);
		this.c = Integer.parseInt(parsedState[1]);
		this.TeleX=Integer.parseInt(parsedState[3].split(",")[0]);
		this.TeleY=Integer.parseInt(parsedState[3].split(",")[1]);
		String[] padList=parsedState[6].split(",");	
		startPads = new Position[padList.length/4];
		endPads = new Position[padList.length/4];
		for(int i=0;i<startPads.length;i++) {
			startPads[i]=new Position(Integer.parseInt(padList[i*4]),Integer.parseInt(padList[i*4+1]));
			endPads[i]=new Position(Integer.parseInt(padList[i*4+2]),Integer.parseInt(padList[i*4+3]));
					
		}
		generatedStates = new HashSet<String>();
	
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
		this.initialState+="0;";//Neo's damage
		this.initialState+="0";//current C
		//Adding the initial state to generates states
		stat_initialState = this.initialState;
		generatedStates.add(adjustState(this.initialState));
		
	}
	@Override
	/**
	 * 	state is grid;each hostage state: turned and killed:2 carried:1 or not:0; Neo's Damage
	 * returns true if the input string represents a goal state
	 */
	public boolean goalTest(String state) {
		
		//parsing state
		String[]parsedState=state.split(";");
		String Neo=parsedState[2];
		String Tele=parsedState[3];
		String[]hostageList=parsedState[7].split(",");
		int[]hostagesState=new int [hostageList.length/3];
		for(int i=0;i<parsedState[8].split(",").length;i++)
			hostagesState[i]=Integer.parseInt(parsedState[8].split(",")[i]);
		//ended parsing state
		
		if(!Neo.equals(Tele))//not at the telephone booth
			return false;
		for(int i=0;i<hostagesState.length;i++) {
			if(hostagesState[i]!=2&& (!(hostageList[i*3]+","+hostageList[i*3+1]).equals(Neo)))
				return false;
			if((hostageList[i*3]+","+hostageList[i*3+1]).equals(Neo)&&hostagesState[i]!=0)
				return false;
		}
		return true;
	}

	@Override
	/**
	 * given a state that belongs to some node and the depth of that node, it returns the past cost
	 * of that node according to deaths, kills and depth of the node (priority is according to
	 * this order).
	 */
	public int pathCost(String state, int depth) {
		int [] thisDeathsKills = Matrix.getTotalDeathsKills(state);
		return thisDeathsKills[0]*rows*columns*(weight+1) + thisDeathsKills[1]*weight+depth;
	}
	
	@Override
	/**
	 * Given a state and an operator o, it return the next state if the operator is applicale on that 
	 * state and null otherwise.
	 */
	public String stateSpace(String state, Operator o) {
		//parse the state
		String[]parsedState=state.split(";");
		String []nextParsedState=parsedState.clone();
	
		int NeoR=Integer.parseInt(parsedState[2].split(",")[0]);
		int NeoC=Integer.parseInt(parsedState[2].split(",")[1]);

		int currentC=Integer.parseInt(parsedState[10]);
		Position[]agents=null;
		Position[]pills=null;
	
		String[]hostageList=parsedState[7].split(",");
	    Position[]hostages=new Position[hostageList.length/3];
	    int[]hostagesDamage=new int[hostageList.length/3];
		for(int i=0;i<hostages.length;i++) {
			hostages[i]=new Position(Integer.parseInt(hostageList[i*3]),Integer.parseInt(hostageList[i*3+1]));
			hostagesDamage[i]=Integer.parseInt(hostageList[i*3+2]);
		}
		String[]hostagesState=new String [hostages.length];
		for(int i=0;i<parsedState[8].split(",").length;i++)
			hostagesState[i]=parsedState[8].split(",")[i];
		int NeoDamage=Integer.parseInt(parsedState[9]);
		//ended parsing state
		boolean stateChanged = false;
		//If there is a hostage in Neo's position, Neo isn't allowed to do any action other than carry
		int hostageIndexEdgeCase = getIndex(NeoR, NeoC,hostages,null);
		
		if(hostageIndexEdgeCase >-1) {
			if( hostagesDamage[hostageIndexEdgeCase]>=98 && hostagesState[hostageIndexEdgeCase].charAt(0)!='2' &&(NeoActions)o!= NeoActions.CARRY)
				return null;
			
		}

		switch ((NeoActions)o) {
		case UP:
			if (NeoR > 0) {
				String[] agentList=parsedState[4].split(",");
				agents=new Position[agentList.length/2];
				for(int i=0;i<agents.length;i++) {
					agents[i]=new Position(Integer.parseInt(agentList[i*2]),Integer.parseInt(agentList[i*2+1]));
				}
				int agentIndex=getIndex(NeoR-1,NeoC,agents,null);
				int hostageIndex=getIndex(NeoR-1,NeoC,hostages,null);

				if(agentIndex==-1&&(hostageIndex==-1||hostagesDamage[hostageIndex]<98||hostagesState[hostageIndex].charAt(0) == '2')){
					NeoR--;
					updateCarriedHostagesLocation(NeoR, NeoC, hostages, hostagesState);
					stateChanged = true;
				}
			}
			break;
		case DOWN:
			if (NeoR < rows-1) {
				String[] agentList=parsedState[4].split(",");
				agents=new Position[agentList.length/2];
				for(int i=0;i<agents.length;i++) {
					agents[i]=new Position(Integer.parseInt(agentList[i*2]),Integer.parseInt(agentList[i*2+1]));
				}
				int agentIndex=getIndex(NeoR+1,NeoC,agents,null);
				int hostageIndex=getIndex(NeoR+1,NeoC,hostages,null);
				if(agentIndex==-1&&(hostageIndex==-1||hostagesDamage[hostageIndex]<98||hostagesState[hostageIndex].charAt(0) == '2')){
					NeoR++;
					updateCarriedHostagesLocation(NeoR, NeoC, hostages, hostagesState);
					stateChanged = true;
				}
			}
			break;
		case RIGHT:
			if (NeoC < columns-1)
			 {
				String[] agentList=parsedState[4].split(",");
				agents=new Position[agentList.length/2];
				for(int i=0;i<agents.length;i++) {
					agents[i]=new Position(Integer.parseInt(agentList[i*2]),Integer.parseInt(agentList[i*2+1]));
				}
				int agentIndex=getIndex(NeoR,NeoC+1,agents,null);
				int hostageIndex=getIndex(NeoR,NeoC+1,hostages,null);
				if(agentIndex==-1&&(hostageIndex==-1||hostagesDamage[hostageIndex]<98||hostagesState[hostageIndex].charAt(0) == '2')){
					NeoC++;
					updateCarriedHostagesLocation(NeoR, NeoC, hostages, hostagesState);
					stateChanged = true;
				}
			}
			break;
		case LEFT:
			if (NeoC > 0)
			{
				String[] agentList=parsedState[4].split(",");
				agents=new Position[agentList.length/2];
				for(int i=0;i<agents.length;i++) {
					agents[i]=new Position(Integer.parseInt(agentList[i*2]),Integer.parseInt(agentList[i*2+1]));
					////////System.out.println("agent "+i+" "+agents[i]);
				}
				int agentIndex=getIndex(NeoR,NeoC-1,agents,null);
				int hostageIndex=getIndex(NeoR,NeoC-1,hostages,null);
				if(agentIndex==-1&&(hostageIndex==-1||hostagesDamage[hostageIndex]<98||hostagesState[hostageIndex].charAt(0) == '2')){
					NeoC--;
					updateCarriedHostagesLocation(NeoR, NeoC, hostages, hostagesState);
					stateChanged = true;
				}
			}
			break;
		case FLY:
			
			int endPadIndex = getIndex(NeoR,NeoC,startPads,null);
			if (endPadIndex >-1) {
				NeoR = endPads[endPadIndex].x;
				NeoC = endPads[endPadIndex].y;
				updateCarriedHostagesLocation(NeoR, NeoC, hostages, hostagesState);
				stateChanged = true;
			}
			break;
		case TAKEPILL:
			String[] pillList=parsedState[5].split(",");	
			pills=new Position[pillList.length/2];
			for(int i=0;i<pills.length;i++) {
				pills[i]=new Position(Integer.parseInt(pillList[i*2]),Integer.parseInt(pillList[i*2+1]));
			}
			int pillIndex = getIndex(NeoR, NeoC,pills,null);
			if (pillIndex>-1) {
				NeoDamage-=20;
				if(NeoDamage<0)
					NeoDamage=0;
				for(int i=0;i<hostagesDamage.length;i++)
					if(!(hostages[i].x == TeleX && hostages[i].y == TeleY && hostagesState[i].charAt(0) == '0') 
							&& hostagesDamage[i]<100) {
						hostagesDamage[i]-=22;
						if(hostagesDamage[i]<0)
							hostagesDamage[i]=0;
					}
				pills[pillIndex]=null;
				stateChanged = true;
			}
			break;

		case KILL:
			String[] agentList=parsedState[4].split(",");
			agents=new Position[agentList.length/2];
			for(int i=0;i<agents.length;i++) {
				agents[i]=new Position(Integer.parseInt(agentList[i*2]),Integer.parseInt(agentList[i*2+1]));
			}
			int upAgentIndex=getIndex(NeoR-1,NeoC,agents,null);
			int downAgentIndex=getIndex(NeoR+1,NeoC,agents,null);
			int leftAgentIndex=getIndex(NeoR,NeoC-1,agents,null);
			int rightAgentIndex=getIndex(NeoR,NeoC+1,agents,null);
			boolean killed = false;
			//UP
			if(upAgentIndex>-1 && NeoDamage<80) {
				
				killed = true;
				agents[upAgentIndex]=null;
			}
			else {
				int hostageIndex=getIndex(NeoR-1,NeoC,hostages,null);
				if(hostageIndex>-1&&hostagesDamage[hostageIndex]>99&&hostagesState[hostageIndex].charAt(0)!='2'
					&&(TeleX!=NeoR-1||TeleY!=NeoC)&& NeoDamage<80) {
					hostagesState[hostageIndex]="2";
					killed = true;
				}
			}
			//DOWN
			if(downAgentIndex>-1&& NeoDamage<80) {
				killed = true;
				agents[downAgentIndex]=null;
			}
			else {
				int hostageIndex=getIndex(NeoR+1,NeoC,hostages,null);
				if(hostageIndex>-1&&hostagesDamage[hostageIndex]>99&&hostagesState[hostageIndex].charAt(0)!='2'
					&&(TeleX!=NeoR+1||TeleY!=NeoC)&& NeoDamage<80) {
					hostagesState[hostageIndex]="2";
					killed = true;
				}
			}
			//LEFT
			if(leftAgentIndex>-1&& NeoDamage<80) {
				killed = true;
				agents[leftAgentIndex]=null;
			}
			else {
				int hostageIndex=getIndex(NeoR,NeoC-1,hostages,null);
				if(hostageIndex>-1&&hostagesDamage[hostageIndex]>99&&hostagesState[hostageIndex].charAt(0)!='2'&&(TeleX!=NeoR||TeleY!=NeoC-1)&& NeoDamage<80) {
					hostagesState[hostageIndex]="2";
					killed = true;
				}
			}
			//RIGHT
			if(rightAgentIndex>-1&& NeoDamage<80) {
				killed = true;
				agents[rightAgentIndex]=null;
			}
			else {
				int hostageIndex=getIndex(NeoR,NeoC+1,hostages,null);
				if( NeoDamage<80&&hostageIndex>-1&&hostagesDamage[hostageIndex]>99&&hostagesState[hostageIndex].charAt(0)!='2'&&(TeleX!=NeoR||TeleY!=NeoC+1)) {
					hostagesState[hostageIndex]="2";
					killed = true;
				}
			}
			if (killed)
				NeoDamage += 20;
			stateChanged = killed;
			break;

		case CARRY:
			int hostageIndex = getIndex(NeoR, NeoC,hostages,hostagesState);

			if (currentC < c && hostageIndex > -1 && hostagesDamage[hostageIndex] < 100&&(NeoR!=TeleX||NeoC!=TeleY)
				&&hostagesState[hostageIndex].charAt(0)!='1') {
				hostagesState[hostageIndex]="1";
				currentC+=1;
				stateChanged = true;
			}
			break;
		case DROP:
			if (NeoR == TeleX && NeoC == TeleY) {
				//find a hostage to drop
				for(int i=0;i<hostagesState.length;i++) {
					if (hostagesState[i].charAt(0) =='1') {
						hostagesState[i] = "0";
						currentC-=1;
						stateChanged = true;
					}
				}
			}
			break;
		}
		if (!stateChanged)
			return null;
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
		nextParsedState[2]=NeoR+","+NeoC;
		nextParsedState[9]=NeoDamage+"";
		nextParsedState[10]=currentC+"";
		//updating hostages
		String[]hostagesJoiner=new String[hostages.length*3] ;
		for(int i=0;i<hostages.length;i++) {
			hostagesJoiner[i*3]=hostages[i].x+"";
			hostagesJoiner[i*3+1]=hostages[i].y+"";
			hostagesJoiner[i*3+2]=hostagesDamage[i]+"";
		}
		nextParsedState[7]=String.join(",", hostagesJoiner);
		//updating pills
		if (pills!=null){
			nextParsedState[5]="";
			for(int i=0;i<pills.length;i++) {
				if(pills[i]!=null) {
					nextParsedState[5]+=pills[i].x+","+pills[i].y+",";
				}
			}
			if(nextParsedState[5].length()>0)
				nextParsedState[5]=nextParsedState[5].substring(0,nextParsedState[5].length()-1);
		}

		//updating hostage states
		nextParsedState[8]=String.join(",", hostagesState);
		//updating agents
		if (agents!=null){
			nextParsedState[4]="";
			for(int i=0;i<agents.length;i++) {
				if(agents[i]!=null) {
					nextParsedState[4]+=agents[i].x+","+agents[i].y+",";
				}
			}
			if(nextParsedState[4].length()>0)
				nextParsedState[4]=nextParsedState[4].substring(0,nextParsedState[4].length()-1);
		}

		return String.join(";", nextParsedState);
	}
	/**
	 * 
	 * @param state to be inserted in the repeated states hash map
	 * @return state adjusted and removed the m, n , Telephone booth location, pads location
	 */
	private static String adjustState(String state)
	{
		String[]parsedState=state.split(";");
		parsedState[0] = "";//m,n
		parsedState[1] = "";//c
		parsedState[3] = "";//TB
		parsedState[6] = "";//pads
	
		return String.join(";", parsedState);
	}
	/**
	 * 
	 * @param NeoR x-coordinate of Neo
	 * @param NeoC y-coordinate of Neo
	 * @param hostages array of hostages location
	 * @param hostagesState array of hostages states (0--> not carried, 1--> carried, 2--> killed).
	 * Update the carried hostages location to match Neo's
	 */
	
	private void updateCarriedHostagesLocation(int NeoR, int NeoC, Position[] hostages, String[] hostagesState) {
		for (int i = 0;i<hostages.length;i++){
			if (hostagesState[i].charAt(0) == '1'){
				hostages[i].x = NeoR;
				hostages[i].y = NeoC;
			}
		}
	}
	/**
	 * 
	 * @param NeoR x-coordinate of Neo
	 * @param NeoC y-coordinate of Neo
	 * @param positions array of positions of the entity to find if it exists in the same location as Neo's
	 * @param hostageState if this input isn't null, check for hostages that aren't carried
	 * @return index of the entity in positions
	 */
	private int getIndex(int NeoR, int NeoC,Position[]positions,String[]hostageState) {
		for (int i=0;i<positions.length;i++) {
			if(positions[i].x == NeoR && positions[i].y == NeoC) {
				if(hostageState ==null )
					return i;
				else
				{
					if(hostageState[i].charAt(0)=='0')
						return i;
				}
			}
				
		}
		return -1;
	}
	/**
	 * 
	 * @param rows number of matrix rows
	 * @param columns number of matrix columns
	 * @param filled array of booleans that represent if the cell is occupied or not
	 * @param gridView array of Strings represents the grid and the content of each cell.
	 * @param s the entity to be put gridView
	 * @return a valid position for entity s 
	 */
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
	/**
	 * 
	 * @param node to be expanded
	 * @param o operator to be applied 
	 * @param strategy the search startegy
	 * @return
	 */
	public MatrixSearchTreeNode expand(MatrixSearchTreeNode node, Operator o, String strategy){
        if (!allowExpandAction(node.operator, o))
            return null;
        String nextState = stateSpace(node.state, o);
        if (nextState == null||this.generatedStates.contains(adjustState(nextState)))
			return null;
        this.generatedStates.add(adjustState(nextState));
		MatrixSearchTreeNode mn=null;
		switch (strategy) {
			case "BF":
			case "DF":
			case "ID":
				mn = new MatrixSearchTreeNode(nextState, node, o, node.depth+1, 0,0);			
				break;
			case "UC":
				mn =new MatrixSearchTreeNode(nextState, node, o, node.depth+1, pathCost(nextState,node.depth+1),0);
				break;
			case "GR1":
			mn = new MatrixSearchTreeNode(nextState, node, o, node.depth+1, 0,heuristic1(nextState));			
				break;
			case "GR2":
			mn = new MatrixSearchTreeNode(nextState, node, o, node.depth+1, 0,heuristic2(nextState));							
				break;
			case "AS1":
			mn = new MatrixSearchTreeNode(nextState, node, o, node.depth+1, pathCost(nextState,node.depth+1),heuristic1(nextState));			
				break;
			case "AS2":
			mn = new MatrixSearchTreeNode(nextState, node, o, node.depth+1, pathCost(nextState,node.depth+1),heuristic2(nextState));							
				break;
		}
		
		return mn;
        
    }
	/**
	 * 
	 * @param state
	 * @return heuristic value of state depending on number of estimated deaths, kills and minimum distance between
	 * Neo and the telephone booth.
	 */
	private  int heuristic1(String state) {
		String[]parsedState=state.split(";");
		int NeoR=Integer.parseInt(parsedState[2].split(",")[0]);
		int NeoC=Integer.parseInt(parsedState[2].split(",")[1]);
		int TeleX=Integer.parseInt(parsedState[3].split(",")[0]);
		int TeleY=Integer.parseInt(parsedState[3].split(",")[1]);
		String[]hostageList=parsedState[7].split(",");
	    Position[]hostages=new Position[hostageList.length/3];
	    int[]hostagesDamage=new int[hostageList.length/3];
		for(int i=0;i<hostages.length;i++) {
			hostages[i]=new Position(Integer.parseInt(hostageList[i*3]),Integer.parseInt(hostageList[i*3+1]));
			hostagesDamage[i]=Integer.parseInt(hostageList[i*3+2]);
		}
		int h=0;
		String[]hostagesState=new String [hostages.length];
		for(int i=0;i<parsedState[8].split(",").length;i++) {
			hostagesState[i]=parsedState[8].split(",")[i];
			if( (hostages[i].x!=TeleX||hostages[i].y!=TeleY) && (hostages[i].x!=NeoR||hostages[i].y!=NeoC) && (hostagesDamage[i]>99  && hostagesState[i].charAt(0)!='2') )
			{
				h+=(rows*columns)*(weight+1)+weight;
			}
			if( (hostages[i].x!=TeleX||hostages[i].y!=TeleY) && (hostages[i].x!=NeoR||hostages[i].y!=NeoC) && (hostagesState[i].charAt(0)!='2'))
			{
				h+=getMinDist(NeoR,NeoC,hostages[i]);
			}
		}
		h+=getMinDist(NeoR,NeoC,new Position(TeleX,TeleY));
		
		return h;
	}

	/**
	 * 
	 * @param state
	 * @return heuristic value of state depending on number of hostage outside of the telephone booth and Neo's distance to the
	 * telephone booth.
	 */
	private  int heuristic2(String state) {
		int h = 0;
		String[]parsedState=state.split(";");
		int NeoR=Integer.parseInt(parsedState[2].split(",")[0]);
		int NeoC=Integer.parseInt(parsedState[2].split(",")[1]);
		int TeleX=Integer.parseInt(parsedState[3].split(",")[0]);
		int TeleY=Integer.parseInt(parsedState[3].split(",")[1]);
		String[]hostageList=parsedState[7].split(",");
	    Position[]hostages=new Position[hostageList.length/3];
	    int[]hostagesDamage=new int[hostageList.length/3];
		for(int i=0;i<hostages.length;i++) {
			hostages[i]=new Position(Integer.parseInt(hostageList[i*3]),Integer.parseInt(hostageList[i*3+1]));
			hostagesDamage[i]=Integer.parseInt(hostageList[i*3+2]);
		}
		String[]hostagesState=new String [hostages.length];
		
		for(int i=0;i<parsedState[8].split(",").length;i++) {
			hostagesState[i]=parsedState[8].split(",")[i];
			if((hostages[i].x!=TeleX||hostages[i].y!=TeleY) && hostagesState[i].charAt(0)=='0')
			{
				h+=weight;
			}
		}
		h+=getMinDist(NeoR,NeoC,new Position(TeleX,TeleY));
		return h;
	}
	/**
	 * 
	 * @param neoR Neo's x--coordinate
	 * @param neoC Neos' y-coordinate
	 * @param hostage location of the hostage
	 * @return the minimum distance to this hostage using pads
	 */

	private  int getMinDist(int neoR, int neoC, Position hostage) {
		int dist1=Math.abs( neoR-hostage.x)+Math.abs(neoC-hostage.y);
		int min=dist1;
		for(int i=0;i<startPads.length;i++)
		{
			int startPadX=startPads[i].x;
			int startPadY=startPads[i].y;
			int endPadX=endPads[i].x;
			int endPadY=endPads[i].y;
			int distTemp1 = Math.abs( neoR-startPadX)+Math.abs(neoC-startPadY);
			int distTemp2 = Math.abs( hostage.x-endPadX)+Math.abs(hostage.y-endPadY);
			int totalDist= distTemp1+distTemp2;
			if(totalDist<min)
				min=totalDist;
		}
		
		return min;
	}
	/**'
	 * 
	 * @param parent node
	 * @param child node
	 * @return true or false if this isn't a reverse action of the parent. Reverse actions are (right then left,
	 * left then right, up then down, down then up and fly then fly).
	 */

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
	/**
	 * 
	 * @return a random grid
	 */

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

		return(grid);
	}
	/**
	 * 
	 * @param grid to be solved
	 * @param strategy chosen
	 * @param visualize boolean whether to print the representation of each step of not.
	 * @return a sequence of actions in addition to number of kills;deaths;expanded nodes if one exists, 
	 * No Solution otherwise.
	 */
	
	public static String solve(String grid, String strategy, boolean visualize) {
		Matrix m = new Matrix(grid);
		MatrixSearchTreeNode solNode = null;
		MatrixSearchTreeNode root ;
		switch (strategy) {
			case "BF":
			root = new MatrixSearchTreeNode(m.initialState, null, null, 0, 0,0);
				solNode = BFS(root, m);
				break;
			case "DF":
			root = new MatrixSearchTreeNode(m.initialState, null, null, 0, 0,0);
				solNode=DFS(root,m);
				break;
			case "ID":
			root = new MatrixSearchTreeNode(m.initialState, null, null, 0, 0,0);
				solNode=IDS(root,m);				
				break;
			case "UC":
			root = new MatrixSearchTreeNode(m.initialState, null, null, 0, 0,0);
				solNode=UCS(root,m);
				break;
			case "GR1":
			root = new MatrixSearchTreeNode(m.initialState, null, null, 0, 0,m.heuristic1(m.initialState));
			solNode=GR1(root,m);
				break;
			case "GR2":
			root = new MatrixSearchTreeNode(m.initialState, null, null, 0, 0,m.heuristic2(m.initialState));
			solNode=GR2(root,m);
				break;
			case "AS1":
			root = new MatrixSearchTreeNode(m.initialState, null, null, 0, 0,m.heuristic1(m.initialState));
			solNode=AS1(root,m);
				break;
			case "AS2":
			root = new MatrixSearchTreeNode(m.initialState, null, null, 0, 0,m.heuristic2(m.initialState));
			solNode=AS2(root,m);
				break;
		}
		if(solNode==null)
			return "No Solution";

		String solve=m.getSolutionSequence(solNode, visualize);
		solve+=getDeathsKills(m.initialState,solNode.state);
		solve+=m.nodesExp+"";
		return(solve);
	}
	/**
	 * 
	 * @param root
	 * @param m
	 * @return the solution node using A* strategy using heuristic 2
	 */
	private static MatrixSearchTreeNode AS2(MatrixSearchTreeNode root, Matrix m) {
		PriorityQueue<MatrixSearchTreeNode> queue = new PriorityQueue<MatrixSearchTreeNode>();
		List<NeoActions> operators = Arrays.asList(NeoActions.values());
		queue.add(root);
		do{
			MatrixSearchTreeNode node = queue.remove();
			m.nodesExp++;
			if(m.goalTest(node.state))
				return node;
			for(Operator o : operators){
				MatrixSearchTreeNode newNode = m.expand(node, o,"AS2");
				if (newNode != null)
					queue.add(newNode);
			}
		}
		while(!queue.isEmpty());
		return null;
	}
	/**
	 * 
	 * @param root
	 * @param m
	 * @return the solution node using greedy strategy using heuristic 2
	 */
	private static MatrixSearchTreeNode GR2(MatrixSearchTreeNode root, Matrix m) {
		PriorityQueue<MatrixSearchTreeNode> queue = new PriorityQueue<MatrixSearchTreeNode>();
		List<NeoActions> operators = Arrays.asList(NeoActions.values());
		queue.add(root);
		do{
			MatrixSearchTreeNode node = queue.remove();
			m.nodesExp++;
			if(m.goalTest(node.state))
				return node;
			for(Operator o : operators){
				MatrixSearchTreeNode newNode = m.expand(node, o,"GR2");
				if (newNode != null)
					queue.add(newNode);
			}
		}
		while(!queue.isEmpty());
		return null;
	}
	/**
	 * 
	 * @param root
	 * @param m
	 * @return the solution node using A* strategy using heuristic 1
	 */
	private static MatrixSearchTreeNode AS1(MatrixSearchTreeNode root, Matrix m) {
		PriorityQueue<MatrixSearchTreeNode> queue = new PriorityQueue<MatrixSearchTreeNode>();
		List<NeoActions> operators = Arrays.asList(NeoActions.values());
		queue.add(root);
		do{
			MatrixSearchTreeNode node = queue.remove();
			m.nodesExp++;
			if(m.goalTest(node.state))
				return node;
			for(Operator o : operators){
				MatrixSearchTreeNode newNode = m.expand(node, o,"AS1");
				if (newNode != null)
					queue.add(newNode);
			}
		}
		while(!queue.isEmpty());
		return null;
	}
	/**
	 * 
	 * @param root
	 * @param m
	 * @return the solution node using greedy strategy using heuristic 1
	 */
	private static MatrixSearchTreeNode GR1(MatrixSearchTreeNode root, Matrix m) {
		PriorityQueue<MatrixSearchTreeNode> queue = new PriorityQueue<MatrixSearchTreeNode>();
		List<NeoActions> operators = Arrays.asList(NeoActions.values());
		queue.add(root);
		do{
			MatrixSearchTreeNode node = queue.remove();
			m.nodesExp++;
			if(m.goalTest(node.state))
				return node;
			for(Operator o : operators){
				MatrixSearchTreeNode newNode = m.expand(node, o,"GR1");
				if (newNode != null)
					queue.add(newNode);
			}
		}
		while(!queue.isEmpty());
		return null;
	}
	/**
	 * 
	 * @param root
	 * @param m
	 * @return the solution node using uniform cost search strategy
	 */
	private static MatrixSearchTreeNode UCS(MatrixSearchTreeNode root, Matrix m) {
		PriorityQueue<MatrixSearchTreeNode> queue = new PriorityQueue<MatrixSearchTreeNode>();
		List<NeoActions> operators = Arrays.asList(NeoActions.values());
		queue.add(root);
		do{
			MatrixSearchTreeNode node = queue.remove();
			m.nodesExp++;
			if(m.goalTest(node.state))
				return node;
			for(Operator o : operators){
				MatrixSearchTreeNode newNode = m.expand(node, o,"UC");
				if (newNode != null)
					queue.add(newNode);
			}
		}
		while(!queue.isEmpty());
		return null;
		
	}
	/**
	 * 
	 * @param initialState
	 * @param state
	 * @return a string represents deaths;kills;
	 */
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
	/**
	 * 
	 * @param initialState
	 * @param state to be compared to intialState
	 * @return an array represents the deaths at index 0 and kills at index 1.
	 */
	public static int[] getTotalDeathsKills(String state) {
		String[]initAgents=stat_initialState.split(";")[4].split(",");
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
		int[] result = new int[2];
		result[0] = deaths;
		result[1] = kills;
		return result;
	}
	/**
	 * 
	 * @param solNode
	 * @param visualize
	 * @return given a solution node returns the solution sequence by back tracking
	 */

	private String getSolutionSequence(MatrixSearchTreeNode solNode, boolean visualize) {
		MatrixSearchTreeNode curNode=solNode;
		String path="";
		while(curNode!=null) {
			if (visualize)
				visualizeState(curNode.state); 
			if(curNode.parent!=null)
				if((NeoActions)curNode.operator == NeoActions.TAKEPILL)
					path="takePill"+","+path;
				else
					path=((NeoActions)curNode.operator).name().toLowerCase()+","+path;
			curNode=(MatrixSearchTreeNode) curNode.parent;
		}
		return path.substring(0,path.length()-1)+";";
	}
	/**
	 * 
	 * @param state
	 * prints the visualization of this state
	 */
	private void visualizeState(String state) {
		//parse the state
		String[][] gridView = new String[rows][columns];
		String[]parsedState=state.split(";");
		int NeoR=Integer.parseInt(parsedState[2].split(",")[0]);
		int NeoC=Integer.parseInt(parsedState[2].split(",")[1]);
		
		

		String[] agentList=parsedState[4].split(",");
		Position[] agents=new Position[agentList.length/2];
		for(int i=0;i<agents.length;i++) {
			agents[i]=new Position(Integer.parseInt(agentList[i*2]),Integer.parseInt(agentList[i*2+1]));
			gridView[agents[i].x][agents[i].y] = "|    A    |";
		}
		String[] pillList=parsedState[5].split(",");	
		Position[] pills=new Position[pillList.length/2];
		for(int i=0;i<pills.length;i++) {
			pills[i]=new Position(Integer.parseInt(pillList[i*2]),Integer.parseInt(pillList[i*2+1]));
			gridView[pills[i].x][pills[i].y] = "|    P    |";
		}
		String[]hostageList=parsedState[7].split(",");
	    Position[]hostages=new Position[hostageList.length/3];
	    int[]hostagesDamage=new int[hostageList.length/3];String[]hostagesState=new String [hostages.length];
		for(int i=0;i<parsedState[8].split(",").length;i++)
			hostagesState[i]=parsedState[8].split(",")[i];
	    
		for(int i=0;i<hostages.length;i++) {
			hostages[i]=new Position(Integer.parseInt(hostageList[i*3]),Integer.parseInt(hostageList[i*3+1]));
			hostagesDamage[i]=Integer.parseInt(hostageList[i*3+2]);
			if(hostagesState[i].charAt(0)=='2')
				gridView[hostages[i].x][hostages[i].y] = "|XXXH("+hostagesDamage[i]+")XX|";
			else
				gridView[hostages[i].x][hostages[i].y] = "|   H("+hostagesDamage[i]+")  |";

		}
		
		for(int i=0;i<startPads.length;i=i+2) {
			gridView[startPads[i].x][startPads[i].y] = "|Pad ("+startPads[i+1].x+","+startPads[i+1].y+")|";
			gridView[startPads[i+1].x][startPads[i+1].y] = "|Pad ("+startPads[i].x+","+startPads[i].y+")|";
			
		}
		gridView[NeoR][NeoC] = "|   Neo   |";
		gridView[TeleX][TeleY] = "|    TB   |";
		int width = columns*11+4;
		for(int i =-1;i<columns;i++)
		{	
			if (i==-1)
				System.out.print("   ");
			else
				System.out.print("     "+i+"     ");
		}
		System.out.println();
		for(int i=0;i<rows;i++)
		{
			System.out.print(i+": ");
			for(int j=0;j<columns;j++)
			{	
				if (gridView[i][j]==null)
					System.out.print("|         |");
				else
					System.out.print(gridView[i][j]);
			}
			System.out.println();
			
			for (int k=0;k<width;k++)
				System.out.print("-");
			System.out.println();
		}
		System.out.println();
		for (int k=0;k<width;k++)
			System.out.print("*");
		System.out.println();
		for (int k=0;k<width;k++)
			System.out.print("*");
		System.out.println();
		System.out.println();
	}
	/**
	 * 
	 * @param root
	 * @param m
	 * @return the solution node using breadth first search
	 */
	private static MatrixSearchTreeNode BFS(MatrixSearchTreeNode root, Matrix m) {
		Queue<MatrixSearchTreeNode> queue = new LinkedList<MatrixSearchTreeNode>();
		List<NeoActions> operators = Arrays.asList(NeoActions.values());
		queue.add(root);
		do{
			MatrixSearchTreeNode node = queue.remove();
			m.nodesExp++;
			if(m.goalTest(node.state))
				return node;
			for(Operator o : operators){
				MatrixSearchTreeNode newNode = m.expand(node, o,"BF");
				if (newNode != null) {
					queue.add(newNode);
				}
			}
		}
		while(!queue.isEmpty() );
		return null;
	}
	/**
	 * 
	 * @param root
	 * @param m
	 * @return the solution node using depth first search
	 */
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
				MatrixSearchTreeNode newNode = m.expand(node, o,"DF");
				if (newNode != null) {
					stack.push(newNode);
				}
			}
		}
		while(!stack.isEmpty());
		return null;
	}
	/**
	 * 
	 * @param root
	 * @param m
	 * @return the solution node using iterative deepening search
	 */
	private static MatrixSearchTreeNode IDS(MatrixSearchTreeNode root, Matrix m) {
		for(int l=0;l<Integer.MAX_VALUE;l++) {
			m.generatedStates=new HashSet<String>();
			m.generatedStates.add(adjustState(m.initialState));
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
					MatrixSearchTreeNode newNode = m.expand(node, o,"ID");
					if (newNode != null)
						stack.push(newNode);
				}
			}
			while(!stack.isEmpty()&&node.depth<=l);
		}
		return null;
	}
	
 	public static void main(String[] args) {
 		String g=genGrid();
	
 		String grid0 = "5,5;2;3,4;1,2;0,3,1,4;2,3;4,4,0,2,0,2,4,4;2,2,91,2,4,62";
 		String grid1 = "5,5;1;1,4;1,0;0,4;0,0,2,2;3,4,4,2,4,2,3,4;0,2,32,0,1,38";
 		String grid2 = "5,5;2;3,2;0,1;4,1;0,3;1,2,4,2,4,2,1,2,0,4,3,0,3,0,0,4;1,1,77,3,4,34";
 		String grid3 = "5,5;1;0,4;4,4;0,3,1,4,2,1,3,0,4,1;4,0;2,4,3,4,3,4,2,4;0,2,98,1,2,98,2,2,98,3,2,98,4,2,98,2,0,1";
 		String grid4 = "5,5;1;0,4;4,4;0,3,1,4,2,1,3,0,4,1;4,0;2,4,3,4,3,4,2,4;0,2,98,1,2,98,2,2,98,3,2,98,4,2,98,2,0,98,1,0,98";
 		String grid5 = "5,5;2;0,4;3,4;3,1,1,1;2,3;3,0,0,1,0,1,3,0;4,2,54,4,0,85,1,0,43";
 		String grid6 = "5,5;2;3,0;4,3;2,1,2,2,3,1,0,0,1,1,4,2,3,3,1,3,0,1;2,4,3,2,3,4,0,4;4,4,4,0,4,0,4,4;1,4,57,2,0,46";
 		String grid7 = "5,5;3;1,3;4,0;0,1,3,2,4,3,2,4,0,4;3,4,3,0,4,2;1,4,1,2,1,2,1,4,0,3,1,0,1,0,0,3;4,4,45,3,3,12,0,2,88";
 		String grid8 = "5,5;2;4,3;2,1;2,0,0,4,0,3,0,1;3,1,3,2;4,4,3,3,3,3,4,4;4,0,17,1,2,54,0,0,46,4,1,22";
 		String grid9 = "5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80";
 		String grid10 = "5,5;4;1,1;4,1;2,4,0,4,3,2,3,0,4,2,0,1,1,3,2,1;4,0,4,4,1,0;2,0,0,2,0,2,2,0;0,0,62,4,3,45,3,3,39,2,3,40";
 		System.out.println(solve(grid0,"DF",false));
 		System.out.println(solve(grid1,"DF",false));
 		System.out.println(solve(grid2,"DF",false));
 		System.out.println(solve(grid3,"DF",false));
 		System.out.println(solve(grid4,"DF",false));
 		System.out.println(solve(grid5,"DF",false));
 		System.out.println(solve(grid6,"DF",false));
 		System.out.println(solve(grid7,"DF",false));
 		System.out.println(solve(grid8,"DF",false));
 		System.out.println(solve(grid9,"DF",false));
 		System.out.println(solve(grid10,"DF",false));

// 		System.out.println(solve(grid10,"AS1",true));
// 		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

// 		System.out.println(String.format("Initial memory: %.2f GB",(double)memoryMXBean.getHeapMemoryUsage().getInit() /1073741824));

// 		System.out.println(String.format("Used heap memory: %.2f GB", (double)memoryMXBean.getHeapMemoryUsage().getUsed() /1073741824));

// 		System.out.println(String.format("Max heap memory: %.2f GB", (double)memoryMXBean.getHeapMemoryUsage().getMax() /1073741824));

// 		System.out.println(String.format("Committed memory: %.2f GB", (double)memoryMXBean.getHeapMemoryUsage().getCommitted() /1073741824));

	
 }
}
