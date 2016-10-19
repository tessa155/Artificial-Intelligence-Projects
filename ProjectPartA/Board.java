/** Board class for holding a 'snapshot' of a board
 *
 * @author Nihal Mirpuri (nmirpuri)
 * @author Tessa Song (songt)
 * @version 1.0
 */

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Board {
	private int n; // The N value (either 2 or 3)
	private int size; // The size of the board (4*n-1)
	private Tile[][] board; // Represents the entire board
	private int possibleMoves = 0; // Number of possible moves at this state
	private int maxByOneMove = 0;  // Maximum number of hexagonal cells that can be captured by one move (0, 1, 2)
	private int avlbCaptures = 0; // Number of hexagonal cells available for capture by a single move
	
	/** constructor 
	 */
	public Board(){
		buildBoard();
	}
	
	/** Build a board by scanning the given input
	 *  and filling the board.
	 *  Count possibleMoves concurrently.
	 *  After that, call determineCaptureValues() to determine 
	 *  the captureValue of each tile and calculate maxBayOneMove 
	 *  and avlbCaptures.
	 */
	public void buildBoard(){
		Scanner scan = new Scanner(System.in);
		
		try {
			n = scan.nextInt();
			size = 4*n-1;

			// Go to the next line
			scan.nextLine();

			board = new Tile[size][size];
			for(int i = 0; i < size; i++) {
				String line = scan.nextLine();
				
				// Check line length is accurate
				if(line.length() != (size*2-1))
					// If not, check if the last char in the line is a space
					if(!(line.length() == (size*2) && line.charAt(size*2-1)==' '))
						throw new IllegalArgumentException();
				
				for(int j = 0; j < size; j++) {
					board[i][j] = new Tile();
					
					char value = line.charAt(j*2);

					// any not allowed letters?(R,B,-,+)
					if (value != 'R' && value != 'B' && value != '-' && value != '+')
						throw new IllegalArgumentException();
					
					board[i][j].setCharValue(value);
					
					// Populate the total @possibleMoves int
					if (value == '+')
						possibleMoves++;
				}
			}
			
			// If there are left over lines, throw an error!
			if(scan.hasNext())
				throw new IllegalArgumentException();
		} catch (IllegalArgumentException|NoSuchElementException e) {
			System.err.println("SYNTAX ERROR: Exiting!");
			System.exit(1);
		}
		
		scan.close();
		
		determineCaptureValues();
	}
	
	
	/**
	 * Print this board
	 */
	public void printBoard(){
		System.out.println(n);
		for(int i = 0; i<size; i++){
			for(int j = 0; j<size; j++){
				System.out.print(board[i][j].getCharValue()+" ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	
	/**
	 * Given a tile point using i, j, returns true if the tile coordinate is valid
	 * Valid tiles are tiles that can have +, R or G on it.
	 * 
	 * Hexagon centers and boundaries therefore return false
	 */
	public Boolean checkTile(int i, int j){
		
		// Check for array boundary
		if (i >= size || j >= size)
			return false;
		
		// Check for hexagon boundary
		// Note: Center of hexagons only occur on odd i, j
		if (i%2==1 && j%2==1)
			return false;
		
		// Check for game boundary
		// If j < 2n-1, then for tile to be valid i <= (2n-1)+j
		// If j > 2n-1, then for tile to be valid i >= j-(2n-1)
		// If j = 2n-1, then tile is valid
		int boundary = 2*n-1;
		if(!((j < boundary && i <= boundary+j)
				||(j > boundary && i >= j-boundary)
				||(j == boundary)))
			return false;
		
		return true;
	}

	
	/** Determine captureValue of each tile on the board.
	 * Simultaneously uses the values to populate avlbCaptures
	 */
	public void determineCaptureValues(){
		// pass the top left tile of each hexagonal cell
		// to check if this cell can be captured by single move
		for(int i=0; i<size; i+=2)
			for(int j=0; j<size && checkTile(i, j); j+=2)
				// increase avlbCaptures if this cell is available for capture by single move 
				avlbCaptures += determineCaptureValue(i, j);
	}
	
	
	
	/** Determine if the given hexagon by x,y coordinate can be captured by single move or not
	 *  and increase the capture value of the available tile by 1
	 *  MaxByOneMove is updated every time this function is called
	 * @param i x coordinate of the hexagon
	 * @param j y coordinate of the hexagon
	 * @return  Returns 1 if hex can be captured, otherwise 0 (used for counter)
	 */
	public int determineCaptureValue(int i, int j){
		// number or plus character in the corresponding hex cell to the given Tile
		// there should be only one plus character within this cell to capture it
		int numPlus = 0;
		
		// boundary checking boolean variable
		boolean isOutOfBound = false;
		
		// int Arrays which contain x,y coordinates of the 5 adjacent tiles 
		int[] iValues = {i, i+1, i, i+2, i+1, i+2};
		int[] jValues = {j, j, j+1, j+1, j+2, j+2};
		
		// x,y coordinate of a tile whose capture value will increase by 1
		// if this cell can be captured by one move
		int iIncrease = 0, jIncrease = 0;
		
		
		for(int k=0; k<jValues.length && numPlus<=1; k++){
			// check if this tile is within the available boundary or not
			if(!checkTile(iValues[k], jValues[k])){
				isOutOfBound = true;
				break;
			}

			// find out the tile whose char value is '+'
			if(board[iValues[k]][jValues[k]].getCharValue() == '+'){
				iIncrease = iValues[k];
				jIncrease = jValues[k];
				numPlus++;
			}
		}
		
		// if there is only one plus '+' character and within boundary 
		// increase the capture value of the '+' tile object
		if (numPlus == 1 && !isOutOfBound){
			board[iIncrease][jIncrease].setCaptureValue(board[iIncrease][jIncrease].getCaptureValue()+1);
			
			// Use capture value to now determine the max you can capture in one move
			if (board[iIncrease][jIncrease].getCaptureValue() > maxByOneMove)
				maxByOneMove = board[iIncrease][jIncrease].getCaptureValue();
			
			// Can capture hex
			return 1;
		}
		
		// Can't capture hex
		return 0;
	}
	
	
	/** Return the possible moves
	 * @return the number of possible Moves
	 */
	public int getPossibleMoves(){
		return possibleMoves;
	}
	
	
	/** Return available cells for capture
	 * @return Number of hexagonal cells available for capture by a single move
	 */
	public int getAvailableCaptures(){
		return avlbCaptures;
	}
	
	
	/** Return maximum number of cells which can be captured by one move
	 * @return maximum number of hexagonal cells which can be captured by one move
	 */
	public int getMaxByOneMove(){
		return maxByOneMove;
	}
}



