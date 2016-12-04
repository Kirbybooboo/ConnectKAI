import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.HashMap;

public class UltimateTicTacToeBotAI extends CKPlayer {
	
	private static int DEPTH = 0;
	private HashMap<BoardModel, Point> previousBestMoves;
	public final static int GOING_TO_WIN = 1000;
	public final static int GOING_TO_LOSE = 1000;
	public final static int MIGHT_WIN = 100;
	public final static int MIGHT_LOSE = 100;
	

	public UltimateTicTacToeBotAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "UltimateTicTacToeBotAI";
		previousBestMoves = new HashMap<BoardModel, Point>();
	}

	@Override
	public Point getMove(BoardModel state) {
//		System.out.println("Counting AI's opponent's ways to win: " + heuristic(state));
//		System.out.println("Counting AI's ways to win: " + opponentHeuristic(state));
		Point bestMove = new Point();
		bestMove = mmSearch(state, bestMove, Integer.MAX_VALUE);
		previousBestMoves.remove(state);
		return bestMove;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		long startTime = System.nanoTime();
		Point bestMove = new Point();
		while (startTime+((deadline)*1000000) > System.nanoTime())
		{
			bestMove = mmSearch(state, bestMove, startTime+((deadline)*1000000));
			previousBestMoves.remove(state);
			DEPTH++;
		}
		DEPTH = 0;
		return bestMove;
	}
	
	public Point mmSearch(BoardModel state, Point lastMove, long deadline) {
		Point bestMove = new Point();	
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
	    int depth = 0;
	    if (previousBestMoves.containsKey(state))
	    {
	    	Point p = previousBestMoves.get(state);
			BoardModel nextMove = state.placePiece(p, player);	//place piece as AI
			int minHeuristicValue = minValue(nextMove, depth, alpha, beta);					//Check AI's optimal move
//				System.out.println("minHeuristicValue: " + minHeuristicValue);
			if (alpha < minHeuristicValue) {					//If move value is better
//					System.out.println("maxValue: " + maxValue);								//It is now best move
			
//					System.out.println("x,y: " + x +" "+ y);
				alpha = minHeuristicValue;
				bestMove.x = previousBestMoves.get(state).x;
				bestMove.y = previousBestMoves.get(state).y;
			}
	    }
			
		for (int x = 0; x < state.getWidth(); x++) {			//go through board in order
			for (int y = 0; y < state.getHeight(); y++) {
				//If move already in hash table, continue;
//				System.out.println("x: " + x);
//				System.out.println("y: " + y);
				if (deadline < System.nanoTime())
					return lastMove;
				depth = 0;
				if (state.getSpace(x,y) == 0) {					//If empty make a temp move
					Point p = new Point(x,y);
					if (previousBestMoves.get(state) == p)
					{
						continue;
					}
					BoardModel nextMove = state.placePiece(p, player);	//place piece as AI
					int minHeuristicValue = minValue(nextMove, depth, alpha, beta);					//Check AI's optimal move
//					System.out.println("minHeuristicValue: " + minHeuristicValue);
					if (alpha < minHeuristicValue && state.getSpace(x,y) == 0) {					//If move value is better and space is empty
//						System.out.println("maxValue: " + maxValue);								//It is now best move
						
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
			int heuristicValue = heuristic(state);	//Maximize by AI's ways to win - Opponent's ways to win
			if (alpha < heuristicValue) {
				alpha = heuristicValue;
			}
			return heuristicValue;  
		}
	    if (previousBestMoves.containsKey(state))
	    {
	    	Point p = new Point(previousBestMoves.get(state));
	    	BoardModel nextMove = state.placePiece(p, player);	//place piece as AI
	    	int minHeuristicValue = minValue(nextMove, depth, alpha, beta);	//AI checks opponent's optimal move
			if (alpha < minHeuristicValue) {					//Get the max of the our ways to win from the Opponent's optimal moves
				alpha = minHeuristicValue;
				previousBestMoves.put(state, p);
			}
			if (alpha >= beta) {						//If alpha is greater than beta, prune
				previousBestMoves.put(state, p);
				return Integer.MAX_VALUE;
			}
	    }
		
		for (int x = 0; x < state.getWidth(); x++) {			//Go through entire board
			for (int y = 0; y < state.getHeight(); y++) {
				if (state.getSpace(x,y) == 0) {					//If empty make a temp move
					Point p = new Point(x,y);
					if (previousBestMoves.get(state) == p)
					{
						continue;
					}
					BoardModel nextMove = state.placePiece(p, player);	//place piece as AI
					int minHeuristicValue = minValue(nextMove, depth, alpha, beta);	//AI checks opponent's optimal move
					if (alpha < minHeuristicValue) {					//Get the max of the our ways to win from the Opponent's optimal moves
						alpha = minHeuristicValue;
						previousBestMoves.put(state, p);
					}
					if (alpha >= beta) {						//If alpha is greater than beta, prune
						previousBestMoves.put(state, p);
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
	    if (previousBestMoves.containsKey(state))
	    {
	    	Point p = new Point(previousBestMoves.get(state));
	    	BoardModel nextMove;
			if (player == 1)							 //Place piece as opponent
				nextMove = state.placePiece(p, (byte) 2);
			else
				nextMove = state.placePiece(p, (byte) 1);
//			System.out.println("---------Calling Max Value-----------");
//			System.out.println("X: " + x);
//			System.out.println("Y: " + y);
			int maxHeuristicValue = maxValue(nextMove, depth, alpha, beta);  //Opponent checks AI's optimal move
			if (beta > maxHeuristicValue) {					//Get the min of the opponent's ways to win from AI's optimal moves
				beta = maxHeuristicValue;
				previousBestMoves.put(state, p);
			}
			if (alpha >= beta) {						//If alpha is greater than beta, prune
				previousBestMoves.put(state, p);
				return Integer.MIN_VALUE;
			}
	    }
		
		for (int x = 0; x < state.getWidth(); x++) {			 //Go through entire board
			for (int y = 0; y < state.getHeight(); y++) {
				if (state.getSpace(x,y) == 0) {					 //If empty make a temp move
					Point p = new Point(x,y);
					if (previousBestMoves.get(state) == p)
					{
						continue;
					}
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
						previousBestMoves.put(state, p);
					}
					if (alpha >= beta) {						//If alpha is greater than beta, prune
						previousBestMoves.put(state, p);
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
			int countPieces = 0;
			int countOpponent = 0;
			for (x = 0; x < width; x++) {
				if (state.getSpace(x,y) == player || state.getSpace(x,y) == 0) {
					count++;
					if (state.getSpace(x, y) != 0) {
						countPieces++;
					}
				}
				else {
					count = 0;
					countPieces = 0;
				}
				if (count >= kLength)
				{
					potentialWins++;
				}
				//If can make k-1 in a row
				if (countPieces >= kLength-1 && x != width-1) {
					potentialWins += MIGHT_WIN;
					//If can win
					if (countPieces == kLength && x != width-1) {
						potentialWins += GOING_TO_WIN;
					}
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
				if (opponentPieces >= kLength-1 && x != width-1) {
					potentialWins -= MIGHT_LOSE;
					if (opponentPieces == kLength && x != width-1) {
						potentialWins -= GOING_TO_LOSE;
					}
				}
				
			}
		}
		//determine whether you can win from going in vertical direction.
		//System.out.println("Doing Vertical");
		for (x = 0; x < width; x++)
		{
			int opponentPieces = 0;
			int count = 0;
			int countPieces = 0;
			int countOpponent = 0;
			for (y = 0; y < height; y++) {
				if (state.getSpace(x,y) == player || state.getSpace(x,y) == 0) {
					count++;
					if (state.getSpace(x, y) != 0) {
						countPieces++;
					}
				}
				else {
					count = 0;
					countPieces = 0;
				}
				if (count >= kLength)
				{
					potentialWins++;
				}
				if (countPieces >= kLength-1 && y != height-1) {
					potentialWins += MIGHT_WIN;
					if (countPieces == kLength && y != height-1) {
						potentialWins += GOING_TO_WIN;
					}
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
				if (opponentPieces >= kLength-1 && y != height-1) {
					potentialWins -= MIGHT_LOSE;
					if (opponentPieces == kLength && y != height-1) {
						potentialWins -= GOING_TO_LOSE;
					}
				}
			}
		}
		//System.out.println("Doing Bottom-Left -> Top-Right y");
		//determine whether you can win from going in bottom-left -> top-right diagonal direction.
		for (y = 0; y <= height-kLength; y++) { // up vector
			int opponentPieces = 0;
			int count = 0;
			int countPieces = 0;
			int countOpponent = 0;
			int y2 = 0;
			x = 0;
//			System.out.println("width = " + width +" height = " + height);
			while (x < width && y+y2 < height) { //diagonal up-right vector
				if (state.getSpace(x,y+y2) == player || state.getSpace(x,y+y2) == 0) {	//Increment count if empty or player piece
					count++;
					if (state.getSpace(x, y+y2) != 0) {
						countPieces++;
					}
				}
				else {
					count = 0;
					countPieces = 0;
				}
				if (count >= kLength)
				{
					potentialWins++;
				}
				if (countPieces >= kLength-1 && x != width-1 && y+y2 != height-1) {
					potentialWins += MIGHT_WIN;
					if (countPieces == kLength && x != width-1 && y+y2 != height-1) {
						potentialWins += GOING_TO_WIN;
					}
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
				if (countOpponent >= kLength)			//Opponent's potential wins
				{
					potentialWins--;
				}
				if (opponentPieces >= kLength-1 && x != width-1 && y+y2 != height-1) {		//If opponent is about to win
					potentialWins -= MIGHT_LOSE;
					if (opponentPieces == kLength && x != width-1 && y+y2 != height-1) {
						potentialWins -= GOING_TO_LOSE;
					}
				}
				x++;
				y2++;
			}
		}
		//System.out.println("Doing Bottom-Left -> Top-Right x");
		for (x = 1; x <= width-(kLength-1);x++) { // right vector
			int opponentPieces = 0;
			int count = 0;
			int countPieces = 0;
			int countOpponent = 0;
			y = 0;
			int x2 = 0;
			while (x+x2 < width && y < height) { //diagonal up-right vector
				if (state.getSpace(x+x2,y) == player || state.getSpace(x+x2,y) == 0) {
					count++;
					if (state.getSpace(x+x2, y) != 0) {
						countPieces++;
					}
				}
				else {
					count = 0;
					countPieces = 0;
				}
				if (count >= kLength)
				{
					potentialWins++;
				}
				if (countPieces >= kLength-1 && x+x2 != width-1 && y != height-1) {
					potentialWins += MIGHT_WIN;
					if (countPieces == kLength && x+x2 != width-1 && y != height-1) {
						potentialWins += GOING_TO_WIN;
					}
				}
				if (state.getSpace(x+x2,y) != player) {
					countOpponent++;
					if (state.getSpace(x+x2, y) != 0) {
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
				if (opponentPieces >= kLength-1 && x+x2 != width-1 && y != height-1) {
					potentialWins -= MIGHT_LOSE;
					if (opponentPieces == kLength && x+x2 != width-1 && y != height-1) {
						potentialWins -= GOING_TO_LOSE;
					}
				}
				x2++;
				y++;
			}
		}
		
		//determine whether you can win from going in top-left -> bottom-right diagonal direction.
		//System.out.println("Doing Top-Left -> Bottom-Right y");
		for (y = height-1; y >= kLength-1; y--) { // down vector
			int opponentPieces = 0;
			int count = 0;
			int countPieces = 0;
			int countOpponent = 0;
			int y2 = 0;
			x = 0;
			while (x < width && y-y2 >= 0) { //diagonal down-right vector
				if (state.getSpace(x,y-y2) == player || state.getSpace(x,y-y2) == 0) {
					count++;
					if (state.getSpace(x, y-y2) != 0) {
						countPieces++;
					}
				}
				else {
					count = 0;
					countPieces = 0;
				}
				if (count >= kLength)
				{
					potentialWins++;
				}
				if (countPieces >= kLength-1 && x != width-1 && y-y2 != height-1) {
					potentialWins += MIGHT_WIN;
					if (countPieces == kLength && x != width-1 && y-y2 != height-1) {
						potentialWins += GOING_TO_WIN;
					}
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
				if (opponentPieces >= kLength-1 && x != width-1 && y-y2 != height-1) {
					potentialWins -= MIGHT_LOSE;
					if (opponentPieces == kLength && x != width-1 && y-y2 != height-1) {
						potentialWins -= GOING_TO_LOSE;
					}
				}
				x++;
				y2++;
			}
		}
//		System.out.println("Doing Top-Left -> Bottom-Right x");
		for (x = 1; x <= width-(kLength-1);x++) { // right vector
			int opponentPieces = 0;
			int count = 0;
			int countPieces = 0;
			int countOpponent = 0;
			y = height-1;
			int x2 = 0;
//			System.out.println("width = " + width +" height = " + height);
			while (x+x2 < width && y >= 0) { //diagonal down-right vector
				if (state.getSpace(x+x2,y) == player || state.getSpace(x+x2,y) == 0) {
					count++;
					if (state.getSpace(x+x2, y) != 0) {
						countPieces++;
					}
				}
				else {
					count = 0;
					countPieces = 0;
				}
				if (count >= kLength)
				{
					potentialWins++;
				}
				if (countPieces >= kLength-1 && x+x2 != width-1 && y != height-1) {
					potentialWins += MIGHT_WIN;
					if (countPieces == kLength && x+x2 != width-1 && y != height-1) {
						potentialWins += GOING_TO_WIN;
					}
				}
				if (state.getSpace(x+x2,y) != player) {
					countOpponent++;
					if (state.getSpace(x+x2, y) != 0) {
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
				if (opponentPieces >= kLength-1 && x+x2 != width-1 && y != height-1) {
					potentialWins -= MIGHT_LOSE;
					if (opponentPieces == kLength && x+x2 != width-1 && y != height-1) {
						potentialWins -= GOING_TO_LOSE;
					}
				}
				x2++;
				y--;
			}
		}		
//		System.out.println("potentialWins: " + potentialWins);
		return potentialWins;
	}
}
