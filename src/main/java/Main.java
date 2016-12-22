import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Kolisek
 */
public class Main
{

	public static final Integer PLAYER_COUNT = 10;

	public static void main(String[] args)
	{
		List<EvolutionPlayer> population = generateFirstPopulation();

		Map<EvolutionPlayer, Integer> winStats = battleAllVsAll(population);

		population = mutatePopulation(winStats);
		winStats = battleAllVsAll(population);

		population = mutatePopulation(winStats);
		winStats = battleAllVsAll(population);

		population = mutatePopulation(winStats);
		winStats = battleAllVsAll(population);

		population = mutatePopulation(winStats);
		winStats = battleAllVsAll(population);

	}

	private static List<EvolutionPlayer> mutatePopulation(Map<EvolutionPlayer, Integer> winMap)
	{
		List<EvolutionPlayer> mutatedPlayers = new ArrayList<>();

		winMap.forEach((p, winCount) ->
		{
			for (int i = 0; i < winCount; i++)
			{
				mutatedPlayers.add(p.mutate(p.getName() + i));
			}
		});

		return mutatedPlayers;
	}

	private static List<EvolutionPlayer> generateFirstPopulation()
	{
		List<EvolutionPlayer> players = new ArrayList<>();

		for (int i = 0; i < PLAYER_COUNT; i++)
		{
			players.add(new EvolutionPlayer("p" + i));
		}

		return players;
	}

	private static Map<EvolutionPlayer, Integer> battleAllVsAll(List<EvolutionPlayer> players)
	{
		Map<EvolutionPlayer, Integer> winMap = new ConcurrentHashMap<>();
		ExecutorService executor = Executors.newFixedThreadPool(5);

		for (int i = 0; i < PLAYER_COUNT; i++)
		{
			for (int j = i; j < PLAYER_COUNT; j++)
			{
				if (i != j)
				{
					executor.execute(new GameWorker(players.get(i), players.get(j), winMap));
				}
			}
		}

		executor.shutdown();

		while (!executor.isTerminated())
		{
		}

		LinkedHashMap<EvolutionPlayer, Integer> sortedWinMap = winMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(e1, e2) -> e1,
						LinkedHashMap::new
				));

		System.out.println("");
		System.out.println("--=========== RESULTS ===========---");
		sortedWinMap.forEach((p, win) ->
		{
			System.out.println(String.format("player: %s win %s number of genes: %s", p.getName(), win, p.getNumberOfGenes()));
		});

		return sortedWinMap;
	}

	private static class GameWorker implements Runnable
	{

		private final EvolutionPlayer ep1;
		private final EvolutionPlayer ep2;
		private final Map<EvolutionPlayer, Integer> winMap;

		private GameWorker(EvolutionPlayer ep1, EvolutionPlayer ep2, Map<EvolutionPlayer, Integer> winMap)
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

}
