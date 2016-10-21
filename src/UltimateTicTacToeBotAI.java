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
		System.out.println(state.getkLength());
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
}
