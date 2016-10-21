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
		heuristic(state);
		int i = ThreadLocalRandom.current().nextInt(0,state.getWidth());
		int j = ThreadLocalRandom.current().nextInt(0,state.getHeight());
		if (state.getSpace(i, j) == 0)
		{
			return new Point(i,j);
		}
		return getMove(state);
	}

	@Override
	public Point getMove(BoardModel state, int deadline) {
		return getMove(state);
	}
	
	public int heuristic(BoardModel state) {
		int potentialWins = 0;
		int kLength = state.getkLength();
		int width = state.getWidth();
		int height = state.getHeight();
		int x = 0;
		int y = 0;
		
		//determine whether you can win from going in horizontal direction. Does not take into account of areas already won
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
				System.out.println("y = " + y);
				System.out.println("count = " + count);
				System.out.println("kLength = " + kLength);
				if (count >= kLength)
				{
					potentialWins++;
				}
			}
		}
		//determine whether you can win from going in vertical direction. Does not take into account of areas already won
		for (x = 0; x < width; x++)
		{
			int count = 0;
			for (y = 0; y < height; y++) {
				System.out.println("x = " + x);
				System.out.println("y = " + y);
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
		System.out.println(potentialWins);
		return potentialWins;
	}
}
