import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * 
 */

/**
 * @author chris
 *
 */
public class AstarPath {
	private ArrayList<MapNode> closed;
	private ArrayList<MapNode> open;
	private ArrayList<MapNode> route;
	
	private MileyGUI GUI;
	private boolean diagonal = true;
	private MapNode[][] nmap;
	private int[] goal;
	private int[] loc = new int[2];
	private int distance = 380;
	private boolean euclid = true;
	private int diag = 550;
	
	
	public AstarPath(MileyGUI G) {
		this.GUI = G;
		this.loc = GUI.getRobotLocation();
		
	}
	private void initNmap(){
		int[][] map = GUI.getMap(); //bottom left is 0,0
		goal = GUI.getGoal(); // sets goal coordinates
		nmap = new MapNode[map.length][map[0].length];
		for(int i=0; i<map.length; i++)
		{
		    for(int j=0; j<map[i].length; j++)
		    {
		    	if(map[i][j]!=1)
		    		nmap[i][j] = new MapNode(i,j);			//nulls for walls
		    }
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean pathStart(){
		initNmap();
		closed = new ArrayList<MapNode>();
		open = new ArrayList<MapNode>();		
		nmap[loc[0]][loc[1]].setG(0);
		open.add(nmap[loc[0]][loc[1]]);
		//GUI.setText("start" , loc[0], loc[1]);
		while(!open.isEmpty()){
			//add closest closed
			Collections.sort(open);
			MapNode curNode = open.remove(0);
			//add to closed
			closed.add(curNode);
			GUI.setText("C"+ curNode.getG(), curNode.row, curNode.col);
			//reached goal?
			if(curNode.loc[0]==goal[0]&&curNode.loc[1]==goal[1])return true;
				
			//add surrounding open G+1
			for(int i=curNode.row-1; i<=curNode.row+1; i++)
			{
			    for(int j=curNode.col-1; j<=curNode.col+1; j++)
			    {
				// bounds checking: in grid, not wall , not current node
				if(i>=0 && i<nmap.length && j>=0 && j<nmap[i].length && nmap[i][j]!=null)
					{
						//MapNode tmp = new MapNode(i,j,curNode.getG()+1);
						//tmp.setParent(curNode);
						//already in closed list 
						if(closed.contains(nmap[i][j])){
							continue;
						}
						if(open.contains(nmap[i][j])){
							//check to see if new g is lower
							//MapNode ltmp = open.get(open.indexOf(nmap[i][j]));						
							if(nmap[i][j].getG() > (curNode.getG()+1)){
								float f = nmap[i][j].getF();
								float g = nmap[i][j].getG();
								nmap[i][j].setG(curNode.getG()+1);
								if(nmap[i][j].getF()>f){
									//return g to old value
									nmap[i][j].setG(g);
								}else{
									nmap[i][j].setParent(curNode);
								}
							}
							
						}else{
							//new node
							if(euclid){
								nmap[i][j].setH(calcEuclid(nmap[i][j].loc,goal));
							}else
							{
								nmap[i][j].setH(calcManhatten(nmap[i][j].loc,goal));
							}
							nmap[i][j].setG(curNode.getG()+1);
							nmap[i][j].setParent(curNode);
							open.add(nmap[i][j]);
							GUI.setText("O"+nmap[i][j].getF() , nmap[i][j].row, nmap[i][j].col);
						}
					}
			    }
			    
			}
			
		}
		//no path found
		System.out.println("No path found");
		return false;
	}
	
	public void genPath(){
		route = new ArrayList<MapNode>();
		MapNode tmp = nmap[goal[0]][goal[1]];
		do{
			//System.out.println(tmp.loc[0]+ " "+tmp.loc[1]);
			route.add(0, tmp);
			tmp=tmp.getParent();
			
		}while(tmp.getParent() != null);		
		
	}
	
	public void movePath(){
		if(route.isEmpty())
			return;	
		loc = GUI.getRobotLocation();
		MapNode curNode = nmap[loc[0]][loc[1]]; 		
		while(curNode !=null&&!route.isEmpty()){
			String dir ="";
				// move up/down
			MapNode nextNode = route.remove(0);
			
			if (nextNode.loc[0] > curNode.loc[0])
				dir = "N";
			else if (nextNode.loc[0] < curNode.loc[0])
				dir = "S";
	
			// move left/right
			if (nextNode.loc[1] > curNode.loc[1])
				dir = dir.concat("E");
			else if (nextNode.loc[1] < curNode.loc[1])
				dir = dir.concat("W");
	
			moveReal(nextNode.loc[0], nextNode.loc[1], dir);
			
			//pause
			try {
				Thread.sleep(1000);
			    } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
			curNode = nextNode;
		}
	}
	// takes a string of a direction N, NE, SW, etc
    // returns the corresponding angle at which the robot to turn to
    public int calcAngleToTurn(String dir)
    {
    	int current_dir = GUI.getRobotAngle();
    	int goal_dir = 0;
    	switch (dir)
    	{
    		case "N":
    			goal_dir = 90;
    			break;
    		case "NE":
    			goal_dir = 45;
    			break;
    		case "E":
    			goal_dir = 0;
    			break;
    		case "SE":
    			goal_dir = 315;
    			break;
    		case "S":
    			goal_dir = 270;
    			break;
    		case "SW":
    			goal_dir = 225;
    			break;
    		case "W":
    			goal_dir = 180;
    			break;
    		case "NW":
    			goal_dir = 135;
    			break;
    	}
    	
    	int angle = current_dir - goal_dir;
    	if (angle > 180) angle = -(360 - angle);
    	
    	return angle;
    }
    
    
	 public void moveReal(int x,int y, String dir)
	    {
	    	
	    	int angle = calcAngleToTurn(dir);
	    	
	    	if(angle!=0){
	    		System.out.println("Angle to turn: " + angle);
		    	GUI.setRobotDirection(dir);
		    	if (GUI.MILEY_LIVE) GUI.myRobot.moveAngle(-angle);
		    	else{
			    	try {
						Thread.sleep(500);
					    } catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					    }
		    	}
	    	}
	    	int dist_to_move = (GUI.getRobotAngle()%90 == 0 ? distance : diag);
	    	System.out.println("Distance to travel: " + dist_to_move);
	    	
	    	if (GUI.MILEY_LIVE) GUI.myRobot.moveDistance(dist_to_move);
	    	GUI.moveRobot(x,  y,  dir);

	    	
	    }
	 
	private int calcManhatten(int[] loc,int[] goal) {
		
		return Math.abs(goal[0]-loc[0])+Math.abs(goal[1]-loc[1]);
	}
	
	private float calcEuclid(int[] loc, int[] goal) {
		return (float) Math.sqrt(Math.pow(goal[0]-loc[0],2) + Math.pow(goal[1]-loc[1],2));
	}

}
