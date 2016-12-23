package simulator;

import java.util.Map;

import game.Game;
import player.EvolutionPlayer;

/**
 * @author Kolisek
 */
class GameWorker implements Runnable
{

	private final EvolutionPlayer ep1;
	private final EvolutionPlayer ep2;
	private final Map<EvolutionPlayer, Integer> winMap;

	public GameWorker(EvolutionPlayer ep1, EvolutionPlayer ep2, Map<EvolutionPlayer, Integer> winMap)
	{
		this.ep1 = ep1;
		this.ep2 = ep2;
		this.winMap = winMap;
	}

	@Override
	public void run()
	{
		Game game = new Game(ep1, ep2);
		game.start();

		if (game.getReferee().isWinnerPlayer1())
		{
			winMap.putIfAbsent(ep1, 0);
			winMap.put(ep1, winMap.get(ep1) + 1);
		}
		else if (game.getReferee().isWinnerPlayer2())
		{
			winMap.putIfAbsent(ep2, 0);
			winMap.put(ep2, winMap.get(ep2) + 1);
		}
	}
}
