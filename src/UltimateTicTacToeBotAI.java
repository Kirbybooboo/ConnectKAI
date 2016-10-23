import connectK.CKPlayer;
import connectK.BoardModel;
import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;

public class UltimateTicTacToeBotAI extends CKPlayer {

	public UltimateTicTacToeBotAI(byte player, BoardModel state) {
		super(player, state);
		teamName = "UltimateTicTacToeBotAI";
	}

	@Override
	public Point getMove(BoardModel state) {
//		BoardModel clonedState = state.clone();
		Point bestMove = new Point();
		bestMove = mmSearch(state);
		System.out.println("bestMove: " + bestMove);
		return bestMove;
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
	
	public Point mmSearch(BoardModel state) {
		Point bestMove = new Point();
		int maxValue = -2000;
		for (int x = 0; x < state.getWidth(); x++) {
			for (int y = 0; y < state.getHeight(); y++) {
				int depth = 0;
				int minHeuristicValue = minValue(state, depth);
				if (maxValue < minHeuristicValue) {
					System.out.println("minHeuristicValue: " + minHeuristicValue);
					System.out.println("x,y: " + x +" "+ y);
					maxValue = minHeuristicValue;
					bestMove.x = x;
					bestMove.y = y;
				}
			}
		}
		System.out.println("Whoohoo");
		return bestMove;
	}
	
	public int maxValue(BoardModel state, int depth) {
		depth++;
		System.out.println("depth:" + depth);
		if (state.winner() != -1 || depth > 5) {
			System.out.println("Max Value: " + (heuristic(state) - opponentHeuristic(state)));
			return heuristic(state) - opponentHeuristic(state);
		}
		int maxValue = -2000;
		for (int x = 0; x < state.getWidth(); x++) {
			for (int y = 0; y < state.getHeight(); y++) {
				if (state.getSpace(x,y) == 0) {
					Point p = new Point(x,y);
					BoardModel nextMove = state.clone();
					nextMove.placePiece(p, player);
					int minHeuristicValue = minValue(nextMove, depth);
					if (maxValue < minHeuristicValue) {
						maxValue = minHeuristicValue;
					}
				}
			}
		}
		return maxValue;
	}
	
	public int minValue(BoardModel state, int depth) {
		depth++;
		if (state.winner() != -1 || depth > 5) {
			System.out.println("Min Value: " + (opponentHeuristic(state) - heuristic(state)));
			return opponentHeuristic(state) - heuristic(state);
		}
		int minValue = 2000;
		System.out.println("Are you even here?");
		for (int x = 0; x < state.getWidth(); x++) {
			for (int y = 0; y < state.getHeight(); y++) {
				if (state.getSpace(x,y) == 0) {
					Point p = new Point(x,y);
					BoardModel nextMove = state.clone();
					nextMove.placePiece(p, player);
					int maxHeuristicValue = maxValue(nextMove, depth);
					if (minValue < maxHeuristicValue) {
						minValue = maxHeuristicValue;
					}
				}
			}
		}
		return minValue;
	}
	
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
			int count = 0;
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
			}
		}
		//determine whether you can win from going in vertical direction.
		//System.out.println("Doing Vertical");
		for (x = 0; x < width; x++)
		{
			int count = 0;
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
			}
		}
		//System.out.println("Doing Bottom-Left -> Top-Right y");
		//determine whether you can win from going in bottom-left -> top-right diagonal direction.
		for (y = 0; y <= height-kLength; y++) { // up vector
			int count = 0;
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
				x++;
				y2++;
			}
		}
		//System.out.println("Doing Bottom-Left -> Top-Right x");
		for (x = 1; x <= width-(kLength-1);x++) { // right vector
			int count = 0;
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
				x2++;
				y2++;
			}
		}
		
		//determine whether you can win from going in top-left -> bottom-right diagonal direction.
		//System.out.println("Doing Top-Left -> Bottom-Right y");
		for (y = height-1; y >= kLength-1; y--) { // down vector
			int count = 0;
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
				x++;
				y2++;
			}
		}
//		System.out.println("Doing Top-Left -> Bottom-Right x");
		for (x = 1; x <= width-(kLength-1);x++) { // right vector
			int count = 0;
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
				x2++;
				y2++;
			}
		}		
//		System.out.println(potentialWins);
		return potentialWins;
	}
	
	public int opponentHeuristic(BoardModel state) {
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
			int count = 0;
			for (x = 0; x < width; x++) {
				if (state.getSpace(x,y) != player || state.getSpace(x,y) == 0) {
					count++;
				}
				else {
					count = 0;
				}
				if (count >= kLength)
				{
					potentialWins++;
				}
			}
		}
		//determine whether you can win from going in vertical direction.
		//System.out.println("Doing Vertical");
		for (x = 0; x < width; x++)
		{
			int count = 0;
			for (y = 0; y < height; y++) {
				if (state.getSpace(x,y) != player || state.getSpace(x,y) == 0) {
					count++;
				}
				else {
					count = 0;
				}
				if (count >= kLength) {
					potentialWins++;
				}
			}
		}
		//System.out.println("Doing Bottom-Left -> Top-Right y");
		//determine whether you can win from going in bottom-left -> top-right diagonal direction.
		for (y = 0; y <= height-kLength; y++) { // up vector
			int count = 0;
			int y2 = 0;
			x = 0;
//			System.out.println("width = " + width +" height = " + height);
			while (x < width && y+y2 < height) { //diagonal up-right vector
//				System.out.println("x = " + x);
//				System.out.println("y+y2 = " + (y+y2));
				if (state.getSpace(x,y+y2) != player || state.getSpace(x,y+y2) == 0) {
					count++;
				}
				else {
					count = 0;
				}
				if (count >= kLength) {
					potentialWins++;
				}
				x++;
				y2++;
			}
		}
		//System.out.println("Doing Bottom-Left -> Top-Right x");
		for (x = 1; x <= width-(kLength-1);x++) { // right vector
			int count = 0;
			int y2 = 0;
			int x2 = 0;
//			System.out.println("width = " + width +" height = " + height);
			while (x2 < width && y+y2 < height) { //diagonal up-right vector
//				System.out.println("x = " + x2);
//				System.out.println("y+y2 = " + (y+y2));
				if (state.getSpace(x2,y+y2) != player || state.getSpace(x2,y+y2) == 0) {
					count++;
				}
				else {
					count = 0;
				}
				if (count >= kLength) {
					potentialWins++;
				}
				x2++;
				y2++;
			}
		}
		
		//determine whether you can win from going in top-left -> bottom-right diagonal direction.
		//System.out.println("Doing Top-Left -> Bottom-Right y");
		for (y = height-1; y >= kLength-1; y--) { // down vector
			int count = 0;
			int y2 = 0;
			x = 0;
//			System.out.println("width = " + width +" height = " + height);
			while (x < width && y-y2 >= 0) { //diagonal down-right vector
//				System.out.println("x = " + x);
//				System.out.println("y-y2 = " + (y-y2));
				if (state.getSpace(x,y-y2) != player || state.getSpace(x,y-y2) == 0) {
					count++;
				}
				else {
					count = 0;
				}
				if (count >= kLength) {
					potentialWins++;
				}
				x++;
				y2++;
			}
		}
//		System.out.println("Doing Top-Left -> Bottom-Right x");
		for (x = 1; x <= width-(kLength-1);x++) { // right vector
			int count = 0;
			int y2 = 0;
			int x2 = 0;
//			System.out.println("width = " + width +" height = " + height);
			while (x2 < width && y-y2 >= 0) { //diagonal down-right vector
//				System.out.println("x = " + x2);
//				System.out.println("y-y2 = " + (y-y2));
				if (state.getSpace(x2,y-y2) != player || state.getSpace(x2,y-y2) == 0) {
					count++;
				}
				else {
					count = 0;
				}
				if (count >= kLength) {
					potentialWins++;
				}
				x2++;
				y2++;
			}
		}		
//		System.out.println(potentialWins);
		return potentialWins;
	}
}
