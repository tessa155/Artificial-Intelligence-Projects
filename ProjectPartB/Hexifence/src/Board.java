/** Board class for holding a 'snapshot' of a board
 *
 * @author Nihal Mirpuri (nmirpuri)
 * @author Tessa Song (songt)
 * @version 1.0
 * 
 * Attribution: 
 *  The basic frame of generatePosbMoves() function in this script
 *  was referred from
 *  www.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
 * 
 *  For shuffle array function
 *  stackoverflow.com/questions/1519736/random-shuffling-of-an-array
 *  
 */

import java.util.*;
import java.io.PrintStream;
import aiproj.hexifence.*;

public class Board {
	public static final int UNDO = -1;
	public int n; // The N value (either 2 or 3)
	public int size; // The size of the board (4*n-1)
	public Tile[][] board; // Represents the entire board
	private int possibleMoves = 0; // Number of possible moves at this state
	
	// Maximum number of hexagonal cells that can be captured by one move
	private int maxByOneMove = 0; 
	// Number of hexagonal cells available for capture by a single move
	private int avlbCaptures = 0; 

	public int blueHex = 0; // Number of hexagons captured by blue player
	public int redHex = 0; // Number of hexagons captured by red player
	
	
	/** constructor 
	 */
	public Board(int n){
		this.n = n;
		this.size = 4*n-1;
		buildBoard();
	}
	
	/** Build a board by filling it with either + or -
	 */
	public void buildBoard(){
		
		// create board object
		board = new Tile[size][size];
		
		// fill the board with either + or -
		for(int i = 0; i<size ; i++){
			for(int j = 0; j<size ; j++){
				Tile temp = new Tile();
				if(checkTile(i, j)){
					temp.setCharValue('+');
				}else{
					temp.setCharValue('-');
				}
				board[i][j] = temp;
			}
		}
	}
	
	
	/**
	 * Print this board
	 */
	public void printBoard(PrintStream output){
		for(int i = 0; i<size; i++){
			for(int j = 0; j<size; j++){
				output.print(board[i][j].getCharValue()+" ");
			}
			output.println();
		}
		output.println();
	}
	
	/**
	 * Given a tile point using i, j, returns true 
	 * if the tile coordinate is valid
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

	/** initialise capture value of all tiles as 0
	*/
	public void initialiseCaptureValue(){
		for(int i = 0 ;i<size ; i++){
			for(int j = 0;j<size; j++){
				board[i][j].setCaptureValue(0);
			}
		}

	}


	/** Determine captureValue of each tile on the board.
	 * Simultaneously uses the values to populate avlbCaptures
	 */
	public void determineCaptureValues(){
		// initialise capture values as 0 first
		initialiseCaptureValue();

		// pass the top left tile of each hexagonal cell
		// to check if this cell can be captured by single move
		for(int i=0; i<size; i+=2)
			for(int j=0; j<size ; j+=2)
				// increase avlbCaptures if this cell is available
				//for capture by single move 
				avlbCaptures += determineCaptureValue(i, j);
	}

	
	/** Determine if the given hexagon by x,y coordinate 
	 *  can be captured by single move or not
	 *  and increase the capture value of the available tile by 1
	 *  MaxByOneMove is updated every time this function is called
	 * @param i x coordinate of the hexagon
	 * @param j y coordinate of the hexagon
	 * @return  Returns 1 if hex can be captured, otherwise 0 (counter)
	 */
	public int determineCaptureValue(int i, int j){
		// number of plus char in the hex of the given Tile
		// there should be only one plus character
		// within this cell to capture it
		int numPlus = 0;
		
		// boundary checking boolean variable
		boolean isOutOfBound = false;
		
		// int Arrays which contain 
		// x,y coordinates of the 5 adjacent tiles 
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
			board[iIncrease][jIncrease].
			setCaptureValue(board[iIncrease][jIncrease].getCaptureValue()+1);
			
			// Use capture value to now determine the max
			// you can capture in one move
			if (board[iIncrease][jIncrease].getCaptureValue() > maxByOneMove)
				maxByOneMove = board[iIncrease][jIncrease].getCaptureValue();
			
			// Can capture hex
			return 1;
		}
		
		// Can't capture hex
		return 0;
	}
	
	/**
	 * Undo the given move from the board
	 * @param move
	 */
	public void undoMove(Move move){
		// set the character as '+'
		board[move.Row][move.Col].setCharValue('+');
		
		// reset maxByOneMove
		maxByOneMove = 0;
		
		// update capture values
		determineCaptureValues();
		
		// get the updated capture value
		int hexCapted = board[move.Row][move.Col].getCaptureValue();
		int counter = hexCapted;
		
		// retrieve the hexs that captured before by this move
		if (hexCapted > 0){
			outerloop:
			for(int i = 0; i<size; i+=2){
				for(int j = 0; j<size; j+=2){
					if(checkTile(i,j) && isCaptured(i, j, move.P) == UNDO){
						char ch = board[i+1][j+1].getCharValue();
		
						// decreased the number of captured cell
						if(ch == 'b')
							blueHex--;
						else
							redHex--;
						
						// update character as '-'
						board[i+1][j+1].setCharValue('-');
						counter--;
					}
					if(counter <= 0)
						break outerloop;	
				}
			}
		}
		
		// update possible moves
		getPossibleMoves();
		
	}
	
	/** Update this board by applying the newly-made move
	 *  If there is any hexagon captured by this move, put either r or b 
	 *  at the centre tile 
	 * @param Move move
	 * @return 1 if there is any hexagon captured by this move
	 * 			 otherwise, return 0 
	 */
	public int setBoard(Move move){
		int hexCapted; // number of hexagons captured by this move
		int counter;

		// make move
		if(move.P == Piece.BLUE)
			board[move.Row][move.Col].setCharValue('B');
		else
			board[move.Row][move.Col].setCharValue('R');
		
		// check how many hexagons are captured by this move
		hexCapted = board[move.Row][move.Col].getCaptureValue();
		counter = hexCapted;

		// put either b or r at the centre of the hexagons captured
		if (hexCapted > 0) {
			outerloop:
			for(int i = 0; i<size; i+=2){
				for(int j= 0; j<size; j+=2){
					// decrease counter by 1 if this cell has been captured
					if(checkTile(i,j)){
						counter -= isCaptured(i, j, move.P);
					}
					// if there is no cell to capture 
					// for this move anymore, break
					if (counter == 0)
						break outerloop;
				}
			}
		}
		
		// reset maxByOneMove
		maxByOneMove = 0;
	
		// update capture values of tiles
		determineCaptureValues();
		
		// update possible moves
		getPossibleMoves();
		
		// return value
		if (hexCapted > 0)
			return 1;
		else
			return 0;
					

	}
	
	
	/**
	 * Check if this hexagon has been captured
	 * If captured, put either 'r' or 'b' at the centre tile 
	 * @return 1 if captured
	 * 		   otherwise, 0
	 * If the cell is not supposed to be captured, i.e there is more than one '+'
	 * but the centre char is not '-'
	 * return UNDO. This is used for undoMove()
	 * 
	 */
	public int isCaptured(int i, int j, int p){
		int captured = 1;
		
		// int Arrays which contain x,y coordinates of the 5 adjacent tiles 
		int[] iValues = {i, i+1, i, i+2, i+1, i+2};
		int[] jValues = {j, j, j+1, j+1, j+2, j+2};
		

		// check if the 6 tiles are all valid, otherwise return rightaway
		for(int k = 0; k < jValues.length; k++){ 
			if( !checkTile(iValues[k], jValues[k]) ){
				captured = 0;
				return captured;
			}
		}

		// check if this cell has been captured
		for(int k = 0; k < jValues.length; k++){ 
			if( board[iValues[k]][jValues[k]].getCharValue() == '+' ){
				captured = 0;
				break;

			}
		}
		
		// used for undoMove()
		if(captured == 0 && board[i+1][j+1].getCharValue() != '-'){
			return UNDO;
		}
		
		// if this has been already captured before,
		if(captured == 1 && board[i+1][j+1].getCharValue() != '-' ){
			captured = 0;
		}
		
		// if captured, set character of the centre tile as either 'b' or 'r'
		if(captured == 1){
			if(p == Piece.BLUE){
				board[i+1][j+1].setCharValue('b');
				blueHex++;
			}
			else if(p == Piece.RED){
				board[i+1][j+1].setCharValue('r');
				redHex++;
			}
		}

		return captured;
	}
	
	
	/**
	 * return all possible moves from the current state
	 * @return array of Moves
	 */
	public List<Move> generatePosbMoves(int p){
		// allocate List
		List<Move> posbMoves = new ArrayList<Move>(); 
		 
	    // If gameover, i.e., no possible move
	    if (getPossibleMoves() == 0) {
	         return posbMoves;   // return empty list
	    }
	    
	    // get all possible moves('+')
		int idx = 0;
		outerloop:
		for(int i = 0; i<size; i++){
			for(int j = 0; j<size; j++){
				if(checkTile(i,j) && board[i][j].getCharValue() == '+'){
					Move move = new Move();
					move.Row = i;
					move.Col = j;
					move.P = p;
					posbMoves.add(move);
					idx++;
				}
				
				// if there is no more possible move to add, break
				if(idx >= getPossibleMoves()){
					break outerloop;
				}
				
			}
		}
		
		// shuffle array 
		// so that the agent won't just take the first possible move
		// when most of the moves have the same evaluation value
		shuffleArray(posbMoves);
		
		return posbMoves;
		
	}
	
	/** Shuffle a given Move array
	 * 
	 * @param posbMoves array of possible moves
	 */
	private void shuffleArray(List<Move> posbMoves) {
    	int index;
    	Move temp;
    	Random random = new Random();
    	for (int i = posbMoves.size() - 1; i > 0; i--){
        	index = random.nextInt(i + 1);
        	temp = posbMoves.get(index);
        	posbMoves.set(index, posbMoves.get(i));
        	posbMoves.set(i, temp);
    	}
	}
	
	/** Return the possible moves
	 * @return the number of possible Moves
	 */
	public int getPossibleMoves(){
		possibleMoves = 0;
		for(int i = 0; i<size; i++){
			for(int j = 0; j<size; j++){
				if(checkTile(i, j))
					if (board[i][j].getCharValue() == '+')
						possibleMoves++;
			}
		}

		return possibleMoves;
	}
	
	/** Return available cells for capture
	 * @return Number of hex cells available for capture by a single move
	 */
	public int getAvailableCaptures(){
		return avlbCaptures;
	}
	
	/** Return maximum number of cells which can be captured by one move
	 * @return maximum number of hexs which can be captured by one move
	 */
	public int getMaxByOneMove(){
		return maxByOneMove;
	}
	
	/** Return maximum streak possible by one player on the current board
	 * @return maximum number of streaks
	 */
	public int getMaxStreak(int depthCap, int score){
		// No available captures - Streak ended
		if(avlbCaptures == 0) return 0;
		
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				if(checkTile(i, j)) {
					// Is there a hex available for capture?
					int captureValue = board[i][j].getCaptureValue();
					if(captureValue > 0) {
						// Make the move, get score, then undo move
						Move tempMove = new Move();
						tempMove.P = Piece.BLUE;
						tempMove.Row = i;
						tempMove.Col = j;
						setBoard(tempMove);
						// Recursively get the rest of the streak
						if(score < depthCap)
							score = 
							getMaxStreak(depthCap, score) + captureValue;
						undoMove(tempMove);
						if(score >= depthCap) return score;
					}
				}
			}
		}
		return score;
	}
}


