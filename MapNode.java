/**
 * 
 */

/**
 * @author chris
 *
 */
public class MapNode implements Comparable {
	public int row;
	public int col;
	public int[] loc = new int[2];
	private float G;
	private float H;
	private MapNode parent;
	
	public MapNode(int row, int col) {
		this.row = row;
		this.col = col;
		this.loc[0]=row;
		this.loc[1]=col;
	}

	public MapNode(int row, int col,float g) {
		this.row = row;
		this.col = col;
		this.G = g;
		this.loc[0]=row;
		this.loc[1]=col;
	}

	public MapNode(int row, int col,float g,float h) {
		this(row,col,g);
		this.H = h;
		
	}
	
	public MapNode(int[] loc,float g,float h) {
		this(loc[0],loc[1],g);
		this.H = h;
		
	}
	
	public float getF() {
		return G + H;
	}

	/**
	 * @return the g
	 */
	public float getG() {
		return G;
	}


	/**
	 * @return the h
	 */
	public float getH() {
		return H;
	}

	/**
	 * @param h
	 *            the h to set
	 */
	public void setH(float h) {
		this.H = h;
	}

	/**
	 * @return the parent
	 */
	public MapNode getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(MapNode parent) {
		this.parent = parent;
	}

	@Override
	public int compareTo(Object arg0) {
		MapNode of = (MapNode) arg0; 
		
		//if(row==of.row && col==of.col) return 0;//it is the same node
		
		if (getF() < of.getF()) {
			return -1;
		} else if (getF() > of.getF()) {
			return 1;
		} else {
			return 0;
		}
	}

	public void setG(float f) {
		this.G=f;
		
	}

}
