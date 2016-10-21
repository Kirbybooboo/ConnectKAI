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
		int i = ThreadLocalRandom.current().nextInt(0,state.getWidth());
		int j = ThreadLocalRandom.current().nextInt(0,state.getHeight());
		if (state.getSpace(i, j) == 0)
		{
			System.out.println(state.toString());
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
			for (int i = 0; i < width; i++) {
				int count = 0;
				if (x+i == player || x+i == 0) {
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
		//determine whether you can win from going in vertical direction. Does not take into account of areas already won
		for (x = 0; x < width; x++)
		{
			for (int j = 0; j < height; j++) {
				int count = 0;
				if (y+j == player || y+j == 0) {
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
		
		return potentialWins;
	}
}
