/** This game agent makes a move by getting x-y coordinate from 
 * a user.
 *
 * @author Nihal Mirpuri (nmirpuri)
 * @author Tessa Song (songt)
 * @version 1.0
 * 
 * Attribution: 
 *  The basic frame of minimax function in this script was referred from
 *  https://www.ntu.edu.sg/home/ehchua/programming/java/JavaGame_TicTacToe_AI.html
 *  
 */


import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;
import aiproj.hexifence.*;

public class SongtNmirpuriManual implements Player, Piece {
	
	public Board gameBoard; // the board to put pieces on
	public int piece; // either BLUE(1) or RED(2) 
	
	
	// represent the state of this board
	// this should be updated on every newly-made move
	public int boardState = Piece.EMPTY; 

	@Override
	public int init(int n, int p) {
		try{
			gameBoard = new Board(n);
			this.piece = p;
		}catch (Exception e){
			return -1;
		}
		return 0;

	}

	@Override
	public Move makeMove() {
		// create Move object
		Move move = new Move();
		move.P = piece;

		Scanner scan = new Scanner(System.in);

		System.out.print("Enter the row: ");
		int row = scan.nextInt();
		System.out.print("Enter the col: ");
		int col = scan.nextInt();

		move.Row = row;
		move.Col = col;

		gameBoard.setBoard(move);
		
		// return the Move object so that the opponent can update their board config
		return move;
	}

	@Override
	public int opponentMove(Move m) {
		// if the opponent's move is illegal, return INVALID
		if (gameBoard.board[m.Row][m.Col].getCharValue() != '+'){
			boardState = Piece.INVALID;
			return boardState;
		}

		return gameBoard.setBoard(m);		
	}

	
	@Override
	public int getWinner() {
		
		// if the opponent makes illegal move
		if (boardState == Piece.INVALID)
			return boardState;
		
		// if the game has not finished
		if(gameBoard.getPossibleMoves() > 0)
			return Piece.EMPTY;

		// if red wins
		if(gameBoard.redHex > gameBoard.blueHex)
			return Piece.RED;
		// if blue wins
		else if(gameBoard.redHex < gameBoard.blueHex)
			return Piece.BLUE;
		// if draw
		else
			return Piece.DEAD;
		
	}

	
	@Override
	public void printBoard(PrintStream output) {
		// call printBoard function of Board object
		gameBoard.printBoard(output);
		
	}

}
