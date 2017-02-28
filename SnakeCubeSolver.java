/*
*	Alice Gibbons
* 	Nov. 4, 2015
*	
*	Run Like: java SnakeCubeSolver SSESESESEEEESESEEESEESEEESS
* 	where S represents a straight connector piece and E represents an Elbow connector piece in the Snake
*/

import java.util.ArrayList;
import java.util.List;

class SnakeCubeSolver {
	public static final int CUBE_SIZE = 3;			//3 X 3 cube
	public static final int SNAKE_SIZE = 27;
	public static int NUM_SOLNS = 0;
	public static final CubePoint CUBE_DIMEN = new CubePoint(CUBE_SIZE, CUBE_SIZE, CUBE_SIZE);	 	//dimensions of cube


	public static void main(String args[]){
		if(args.length < 1){
			System.out.println("Usage: java SnakeCubeSolver <string of E and S characters corresponding to a 27 cube snake>");
			return;
		}

		String inputSnake = args[0];
		if(inputSnake.length() != SNAKE_SIZE || !inputSnake.matches("[ES]*")){
			System.out.println("Length of input snake must be 27 cubes long and entirely made up of the characters E and S");
			return;
		}

		boolean[][][] cube = new boolean[CUBE_SIZE][CUBE_SIZE][CUBE_SIZE];		//boolean cube to keep track of where snake travels
		List<CubePoint> currentPath = new ArrayList<CubePoint>();				//current path that snake is taking in cube
		List<String> directionList = new ArrayList<String>();
		CubePoint startingLocation;
		int numSolns = 0;
		for (int i = 1; i <= CUBE_SIZE; i++){	
			//check all possible non-isomorphic starting locations
			startingLocation = new CubePoint(i, 1, 1);
			if(i == CUBE_SIZE){
				startingLocation = new CubePoint(2, 2, 1);
			}

			recursiveSnakeCube(currentPath, startingLocation, 1, false, cube, inputSnake, directionList);	//fix 'x' direction to get rid of rotationally isomorphic solns
		}

		System.out.println("No Solution Found");
	}

	public static void recursiveSnakeCube(List<CubePoint> currentPath, CubePoint location, int prevDir, boolean firstElbow, boolean[][][] cube, String inputSnake, List<String> directionList){
		//check to see if all snake cubes have been placed (finished)
		if(inputSnake.length() == 0){
			printSolution(currentPath, directionList);
			return;
		}

		//check to see if cube in direction is out of bounds
		if(location.x < 1 || location.y < 1 || location.z < 1) return;
		if(location.x > CUBE_DIMEN.x || location.y > CUBE_DIMEN.y || location.z > CUBE_DIMEN.z) return;

		// check to see if cube already contains a snake cube in location
		if(cube[location.x - 1][location.y - 1][location.z - 1] == true){			//subtract 1 to deal with 0 indexing
			return;
		}

		//begin recursive step here
		cube[location.x - 1][location.y - 1][location.z - 1] = true;		//set cube to taken (-1 to account for 1 indexing)
		currentPath.add(location);											//add current location to solution path
		addDirection(prevDir, directionList);

		String nextStr = inputSnake.substring(1);
		char curCh = inputSnake.charAt(0);
		CubePoint newLoc;
		if(curCh == 'E') {					//elbow cube 
			for(int i = 1; i <= 6; i++){	//try all 4 possible elbow possible options
				int parity = i%2;
				if(i == prevDir || ( parity == 1 && (i+1) == prevDir) || (parity == 0 && (i-1)==prevDir)){	//continue to next move if straight directions
						continue;
				}

				if(!firstElbow){			//to rid of isomorphic solns we fix first placement of elbow cube
					firstElbow = true;
					newLoc = getElbowMove(location, i);
					recursiveSnakeCube(currentPath, newLoc, i, firstElbow, cube, nextStr, directionList);
					newLoc = getElbowMove(location, i+1);
					recursiveSnakeCube(currentPath, newLoc, i+1, firstElbow, cube, nextStr, directionList);
					break;
				} else {
					newLoc = getElbowMove(location, i);
					recursiveSnakeCube(currentPath, newLoc, i, firstElbow, cube, nextStr, directionList);
				}
			}

		} else {							//straight cube
			newLoc = getElbowMove(location, prevDir);
			recursiveSnakeCube(currentPath, newLoc, prevDir, firstElbow, cube, nextStr, directionList);
		}

		//backtracking
		cube[location.x - 1][location.y - 1][location.z - 1] = false;		//remove snake from cube
		currentPath.remove(location);		
		directionList.remove(directionList.size() - 1);						//add direction to output list
	}

	/*
	* Gets a point in the cube at a given location
	*/
	public static CubePoint getElbowMove(CubePoint currentLoc, int move){
		CubePoint newLoc = new CubePoint(currentLoc);
		switch(move){
			case 1: newLoc.x++; break;
			case 2: newLoc.x--; break;
			case 3: newLoc.y++; break;
			case 4: newLoc.y--; break;
			case 5: newLoc.z++; break;
			case 6: newLoc.z--; break;
		}
		return newLoc;
	}

	/*
	* Gets the direction traveled in the cube from a given move
	*/
	public static void addDirection(int move, List<String> directionList){
		switch(move){
			case 1: directionList.add("R"); break;
			case 2: directionList.add("L"); break;
			case 3: directionList.add("U"); break;
			case 4: directionList.add("D"); break;
			case 5: directionList.add("I"); break;
			case 6: directionList.add("O"); break;
		}
	}

	/*
	* Prints the moves necessary in each direction to solve the input snake
	*/
	public static void printSolution(List<CubePoint> path, List<String> directionList){
		NUM_SOLNS++;
		String coords1 = "Solution " + NUM_SOLNS + ": ";
		for(int i = 0; i < directionList.size(); i++){
			if(i != 0) coords1 += ",";
			coords1 += directionList.get(i);
		}
		System.out.println(coords1);
	}

}

/*
 * Single cube point in an n X n X n cube
 */
class CubePoint {
		public int x;
		public int y;
		public int z;
		
		public CubePoint(int x, int y, int z){
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public CubePoint(CubePoint c){
			x = c.x;
			y = c.y;
			z = c.z;
		}
		
		public String toString(){
			return "(" + x + "," + y + "," + z + ")";
		}
	}
	