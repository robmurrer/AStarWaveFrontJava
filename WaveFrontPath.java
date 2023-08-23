import javax.swing.JOptionPane;

import RobotClient.MapGUI;
import RobotClient.CreateClient;
/**
 * 
 */

/**
 * @author chris
 *
 */



public class WaveFrontPath {
    private MileyGUI GUI;
    private boolean diagonal=true;
    private int[][] map;
    private int[] goal; 
    private int[] loc = new int[2];
    private int distance = 380;
    private int diag = 550;
    
    private CreateClient myRobot;
    
    
    public WaveFrontPath()
    {
    	if (GUI.MILEY_LIVE)
    	{
    	    myRobot = new CreateClient("Miley","10.0.0.10");
    	}
    }
    
    public WaveFrontPath(MileyGUI inGUI)
    {
    	this.GUI=inGUI;
    	if (GUI.MILEY_LIVE)
    	{
    	    myRobot = new CreateClient("Miley","10.0.0.10");
    	}
    }
    
    // build wavefront values
    // x = num row
    // y = num col
    // start = goal value 
    public void checkBox(int x, int y,int start) 
    {
	// set unexplored and optimize for better path
	if(map[x][y] == 0 || map[x][y] > start) map[x][y]=start;

        // update value in GUI
	GUI.setText(String.valueOf(map[x][y]), x, y,false);

	//set all surrounding squares
	for(int i=x-1; i<=x+1; i++)
	{
	    for(int j=y-1; j<=y+1; j++)
	    {
		// bounds checking
		if(i>=0 && i<map.length && j>=0 && j<map[i].length)
			{
			    // set all uninitialized values to start+1
			    if(map[i][j]==0)
			    {				
			    	map[i][j]=start+1;
			    }
			}
	    }
	    
	}

	//recursive check on all surrounding squares
	for(int i=x-1; i<=x+1; i++)
	{
	    for(int j=y-1; j<=y+1; j++)
	    {
		if(i>=0 && i<map.length && j>=0 && j<map[i].length)
		{
		    // if unoptimized recurse
		    if(map[i][j]>=start+1)
		    {				
			checkBox(i,j,start+1);
		    }
		}
	    }
	}
    }


    // Load GUI and load val 
    public void mapInit()
    {
		map = GUI.getMap(); //bottom left is 0,0
		goal = GUI.getGoal(); // sets goal coordinates
		for(int i=0; i<map.length; i++)
		{
		    for(int j=0; j<map[i].length; j++)
		    {
			GUI.setText(String.valueOf(map[i][j]), i, j);			
		    }
		}
    }

    // Load GUI and load val 
    public void mapReset()
    {
		map = GUI.getMap(); //bottom left is 0,0
		goal = GUI.getGoal(); // sets goal coordinates
		for(int i=0; i<map.length; i++)
		{
		    for(int j=0; j<map[i].length; j++)
		    {
			GUI.setText("", i, j);
			if(map[i][j]>1)map[i][j]=0;
		    }
		}
    }

    // todo: creates instruction set for robot to get to goal
    public void makePath()
    {
		loc = GUI.getRobotLocation();
    }


    // logs map to console for debugging
    public void printMap()
    {
	for(int i=map.length-1; i>=0; i--)
	{
	    for(int j=0; j<map[i].length; j++)
	    {
		System.out.print(map[i][j]+ " ");			
	    }
	    System.out.println();
	}
    }


    // moves robot along best path determined by wavefront
    public boolean moveRobot()
    {   	
	boolean atGoal=false;
	loc = GUI.getRobotLocation();
	//check to see if start==goal
	if(map[loc[0]][loc[1]]==2){
		atGoal=true;
	}

	//set lowest to our current value
	int[] lowest= new int[2];  
	lowest[0]=loc[0];
	lowest[1]=loc[1];

        // loop until at goal
	while(!atGoal)
	{
		if (!GUI.MILEY_LIVE)
		{
		    try {
			Thread.sleep(1000);
		    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		}
		System.out.println("----");
            // look for lowest cell value to go next
	    for(int i=loc[0]-1; i<=loc[0]+1; i++)
	    {
		for(int j=loc[1]-1; j<=loc[1]+1; j++)
		{
		    if(i>=0 && i<map.length && j>=0 && j<map[i].length)
			if(map[i][j] <= map[lowest[0]][lowest[1]]&& map[i][j] > 1 )
			{	
				
				if((map[i][j] == map[lowest[0]][lowest[1]]) && (lowest[0] !=loc[0] && lowest[1] != loc[1])){
				//if we get here lowest has been set at least once	but it is an angle
					lowest[0]=i;
					lowest[1]=j;
					
				}
				else if((map[i][j] == map[lowest[0]][lowest[1]]))
				{
					//we already have an equal value pick that is not diagonal
				}
				else
				{
					lowest[0]=i;
					lowest[1]=j;
					
				}
			}
		}
	    }

            // at this point lowest is the last lowest cell
	    //check if no route
	    if(map[lowest[0]][lowest[1]]<=1){    	
	    	return false;
	    }
	    String dir ="";

	    // move up/down
	    if(lowest[0]>loc[0]) dir="N";
	    else if(lowest[0]<loc[0]) dir="S";

            // move left/right
	    if(lowest[1]>loc[1]) dir = dir.concat("E");
	    else if(lowest[1]<loc[1]) dir = dir.concat("W");
	    
	    moveReal(lowest[0], lowest[1], dir);
	    

            // check if at goal and set flag to break loop
	    if(map[lowest[0]][lowest[1]]==2) atGoal=true;
	    
	    // update robot location
	    loc=GUI.getRobotLocation();
	}
	return true;
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
	    	if (GUI.MILEY_LIVE) myRobot.moveAngle(-angle);
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
    	
    	if (GUI.MILEY_LIVE) myRobot.moveDistance(dist_to_move);
    	GUI.moveRobot(x,  y,  dir);

    	
    }

    public static void main(String args[]) {
	WaveFrontPath path = new WaveFrontPath();
	path.GUI = new MileyGUI("maze.txt");	
	path.mapInit();
	path.checkBox(path.goal[0], path.goal[1], 2);
	path.GUI.refresh();
	path.printMap();	
	
//	if (MILEY_LIVE)
//	{
//		path.myRobot.waitForPlay();
//		path.myRobot.initialize();
//	}
	path.moveRobot();
    }
}
