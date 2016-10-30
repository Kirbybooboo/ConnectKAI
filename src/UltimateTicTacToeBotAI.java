import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;

public class UltimateTicTacToeBotAI extends CKPlayer {
	
	private static final int DEPTH = 4;
	

	public UltimateTicTacToeBotAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "UltimateTicTacToeBotAI";
	}

	@Override
	public Point getMove(BoardModel state) {
//		System.out.println("Counting AI's opponent's ways to win: " + heuristic(state));
//		System.out.println("Counting AI's ways to win: " + opponentHeuristic(state));
		Point bestMove = new Point();
		bestMove = mmSearch(state);
		return bestMove;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
	
	public Point mmSearch(BoardModel state) {
		Point bestMove = new Point();	
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		for (int x = 0; x < state.getWidth(); x++) {
			for (int y = 0; y < state.getHeight(); y++) {
//				System.out.println("x: " + x);
//				System.out.println("y: " + y);
				int depth = 0;
				if (state.getSpace(x,y) == 0) {					//If empty make a temp move
					Point p = new Point(x,y);
					BoardModel nextMove = state.placePiece(p, player);	//place piece as AI
					int minHeuristicValue = minValue(nextMove, depth, alpha, beta);						//Check AI's optimal move
//					System.out.println("minHeuristicValue: " + minHeuristicValue);
					if (alpha < minHeuristicValue && state.getSpace(x,y) == 0) {
//						System.out.println("maxValue: " + maxValue);
						
//						System.out.println("x,y: " + x +" "+ y);
						alpha = minHeuristicValue;
						bestMove.x = x;
						bestMove.y = y;
					}
				}
			}
		}
		return bestMove;
	}
	
	public int maxValue(BoardModel state, int depth, int alpha, int beta) {
		depth++;
//		System.out.println("depth:" + depth);
		if (state.winner() != -1 || depth >= DEPTH) { // at leaf node
//			System.out.println("Max Value: " + (heuristic(state)));
			int i = heuristic(state);	//Maximize by AI's ways to win - Opponent's ways to win
			if (alpha < i) {
				alpha = i;
			}
			return i;  
		}
		for (int x = 0; x < state.getWidth(); x++) {			//Go through entire board
			for (int y = 0; y < state.getHeight(); y++) {
				if (state.getSpace(x,y) == 0) {					//If empty make a temp move
					Point p = new Point(x,y);
					BoardModel nextMove = state.placePiece(p, player);	//place piece as AI
					int minHeuristicValue = minValue(nextMove, depth, alpha, beta);	//AI checks opponent's optimal move
					if (alpha < minHeuristicValue) {					//Get the max of the our ways to win from the Opponent's optimal moves
						alpha = minHeuristicValue;
					}
					if (alpha >= beta) {
						return Integer.MAX_VALUE;
					}
				}
			}
		}
		return alpha;
	}
	
	public int minValue(BoardModel state, int depth, int alpha, int beta) {
		depth++;
//		System.out.println("depth:" + depth);
		if (state.winner() != -1 || depth >= DEPTH) {
			return heuristic(state);  //AI's ways to win - Opponent's ways to win (Want to minimize opponent's ways to win later using this data)
		}
		for (int x = 0; x < state.getWidth(); x++) {			 //Go through entire board
			for (int y = 0; y < state.getHeight(); y++) {
				if (state.getSpace(x,y) == 0) {					 //If empty make a temp move
					Point p = new Point(x,y);
					BoardModel nextMove;
					if (player == 1)							 //Place piece as opponent
						nextMove = state.placePiece(p, (byte) 2);
					else
						nextMove = state.placePiece(p, (byte) 1);
//					System.out.println("---------Calling Max Value-----------");
//					System.out.println("X: " + x);
//					System.out.println("Y: " + y);
					int maxHeuristicValue = maxValue(nextMove, depth, alpha, beta);  //Opponent checks AI's optimal move
					if (beta > maxHeuristicValue) {					//Get the min of the opponent's ways to win from AI's optimal moves
						beta = maxHeuristicValue;
					}
					if (alpha >= beta) {
						return Integer.MIN_VALUE;
					}
				}
			}
		}
		return beta;
	}
	
	//counts ways that AI can win - Opponent can win
	public int heuristic(BoardModel state) {
		int potentialWins = 0;
		int kLength = state.getkLength();
		int width = state.getWidth();
		int height = state.getHeight();
		int x = 0;
		int y = 0;
		
		//determine whether you can win from going in horizontal direction.
		//System.out.println("Doing Horizontal");
		for (y = 0; y < height; y++)
		{
			int opponentPieces = 0;
			int count = 0;
			int countOpponent = 0;
			for (x = 0; x < width; x++) {
				if (state.getSpace(x,y) == player || state.getSpace(x,y) == 0) {
					count++;
				}
				else {
					count = 0;
				}
				if (count >= kLength)
				{
					potentialWins++;
				}
				if (state.getSpace(x,y) != player) {
					countOpponent++;
					if (state.getSpace(x, y) != 0) {
						opponentPieces++;
					}
				}
				else {
					countOpponent = 0;
					opponentPieces = 0;
				}
				if (countOpponent >= kLength)
				{
					potentialWins--;
				}
				if (opponentPieces == kLength-1) {
					potentialWins -= 10000;
				}
				
			}
		}
		//determine whether you can win from going in vertical direction.
		//System.out.println("Doing Vertical");
		for (x = 0; x < width; x++)
		{
			int opponentPieces = 0;
			int count = 0;
			int countOpponent = 0;
			for (y = 0; y < height; y++) {
				if (state.getSpace(x,y) == player || state.getSpace(x,y) == 0) {
					count++;
				}
				else {
					count = 0;
				}
				if (count >= kLength) {
					potentialWins++;
				}
				if (state.getSpace(x,y) != player) {
					countOpponent++;
					if (state.getSpace(x, y) != 0) {
						opponentPieces++;
					}
				}
				else {
					countOpponent = 0;
					opponentPieces = 0;
				}
				if (countOpponent >= kLength)
				{
					potentialWins--;
				}
				if (opponentPieces == kLength-1) {
					potentialWins -= 10000;
				}
			}
		}
		//System.out.println("Doing Bottom-Left -> Top-Right y");
		//determine whether you can win from going in bottom-left -> top-right diagonal direction.
		for (y = 0; y <= height-kLength; y++) { // up vector
			int opponentPieces = 0;
			int count = 0;
			int countOpponent = 0;
			int y2 = 0;
			x = 0;
//			System.out.println("width = " + width +" height = " + height);
			while (x < width && y+y2 < height) { //diagonal up-right vector
//				System.out.println("x = " + x);
//				System.out.println("y+y2 = " + (y+y2));
				if (state.getSpace(x,y+y2) == player || state.getSpace(x,y+y2) == 0) {
					count++;
				}
				else {
					count = 0;
				}
				if (count >= kLength) {
					potentialWins++;
				}
				if (state.getSpace(x,y+y2) != player) {
					countOpponent++;
					if (state.getSpace(x, y+y2) != 0) {
						opponentPieces++;
					}
				}
				else {
					countOpponent = 0;
					opponentPieces = 0;
				}
				if (countOpponent >= kLength)
				{
					potentialWins--;
				}
				if (opponentPieces == kLength-1) {
					System.out.println("We Gonna Loooooooooooosssssssssssssseeeeeeeeee");
					potentialWins -= 10000;
				}
				x++;
				y2++;
			}
		}
		//System.out.println("Doing Bottom-Left -> Top-Right x");
		for (x = 1; x <= width-(kLength-1);x++) { // right vector
			int opponentPieces = 0;
			int count = 0;
			int countOpponent = 0;
			int y2 = 0;
			int x2 = 0;
//			System.out.println("width = " + width +" height = " + height);
			while (x2 < width && y+y2 < height) { //diagonal up-right vector
//				System.out.println("x = " + x2);
//				System.out.println("y+y2 = " + (y+y2));
				if (state.getSpace(x2,y+y2) == player || state.getSpace(x2,y+y2) == 0) {
					count++;
				}
				else {
					count = 0;
				}
				if (count >= kLength) {
					potentialWins++;
				}
				if (state.getSpace(x2,y+y2) != player) {
					countOpponent++;
					if (state.getSpace(x2, y+y2) != 0) {
						opponentPieces++;
					}
				}
				else {
					countOpponent = 0;
					opponentPieces = 0;
				}
				if (countOpponent >= kLength)
				{
					potentialWins--;
				}
				if (opponentPieces == kLength-1) {
					System.out.println("We Gonna Loooooooooooosssssssssssssseeeeeeeeee");
					potentialWins -= 10000;
				}
				x2++;
				y2++;
			}
		}
		
		//determine whether you can win from going in top-left -> bottom-right diagonal direction.
		//System.out.println("Doing Top-Left -> Bottom-Right y");
		for (y = height-1; y >= kLength-1; y--) { // down vector
			int opponentPieces = 0;
			int count = 0;
			int countOpponent = 0;
			int y2 = 0;
			x = 0;
//			System.out.println("width = " + width +" height = " + height);
			while (x < width && y-y2 >= 0) { //diagonal down-right vector
//				System.out.println("x = " + x);
//				System.out.println("y-y2 = " + (y-y2));
				if (state.getSpace(x,y-y2) == player || state.getSpace(x,y-y2) == 0) {
					count++;
				}
				else {
					count = 0;
				}
				if (count >= kLength) {
					potentialWins++;
				}
				if (state.getSpace(x,y-y2) != player) {
					countOpponent++;
					if (state.getSpace(x, y-y2) != 0) {
						opponentPieces++;
					}
				}
				else {
					countOpponent = 0;
					opponentPieces = 0;
				}
				if (countOpponent >= kLength)
				{
					potentialWins--;
				}
				if (opponentPieces == kLength-1) {
					System.out.println("We Gonna Loooooooooooosssssssssssssseeeeeeeeee");
					potentialWins -= 10000;
				}
				x++;
				y2++;
			}
		}
//		System.out.println("Doing Top-Left -> Bottom-Right x");
		for (x = 1; x <= width-(kLength-1);x++) { // right vector
			int opponentPieces = 0;
			int count = 0;
			int countOpponent = 0;
			int y2 = 0;
			int x2 = 0;
//			System.out.println("width = " + width +" height = " + height);
			while (x2 < width && y-y2 >= 0) { //diagonal down-right vector
//				System.out.println("x = " + x2);
//				System.out.println("y-y2 = " + (y-y2));
				if (state.getSpace(x2,y-y2) == player || state.getSpace(x2,y-y2) == 0) {
					count++;
				}
				else {
					count = 0;
				}
				if (count >= kLength) {
					potentialWins++;
				}
				if (state.getSpace(x2,y-y2) != player) {
					countOpponent++;
					if (state.getSpace(x2, y-y2) != 0) {
						opponentPieces++;
					}
				}
				else {
					countOpponent = 0;
					opponentPieces = 0;
				}
				if (countOpponent >= kLength)
				{
					potentialWins--;
				}
				if (opponentPieces == kLength-1) {
					System.out.println("We Gonna Loooooooooooosssssssssssssseeeeeeeeee");
					potentialWins -= 10000;
				}
				x2++;
				y2++;
			}
		}		
//		System.out.println("potentialWins: " + potentialWins);
		return potentialWins;
	}
}
