import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import game.Game;
import player.EvolutionPlayer;

/**
 * @author Kolisek
 */
public class Main
{

	public static final Integer PLAYER_COUNT = 10;

	public static void main(String[] args)
	{
		List<EvolutionPlayer> population = generateFirstPopulation();
		Map<EvolutionPlayer, Integer> winStats = battleAllVsAll(population, 0);

		evolve(winStats, 1);

	}

	private static Map<EvolutionPlayer, Integer> evolve(Map<EvolutionPlayer, Integer> winStats,
														int generation)
	{
		List<EvolutionPlayer> population = mutatePopulation(winStats);
		return evolve(battleAllVsAll(population, generation), generation + 1);
	}

	private static List<EvolutionPlayer> mutatePopulation(Map<EvolutionPlayer, Integer> winMap)
	{
		final List<EvolutionPlayer> mutatedPlayers = new ArrayList<>();

		final int sumOfWins = winMap.values().stream()
				.mapToInt(i -> i)
				.sum();

		winMap.forEach((p, winCount) ->
		{
			int numberOfChildren = Math.round(((float)winCount / sumOfWins) * PLAYER_COUNT);

			for (int i = 0; i < numberOfChildren; i++)
			{
				mutatedPlayers.add(p.mutate(p.getName()));
			}
		});

		List<EvolutionPlayer> orderedPlayers = new ArrayList<>(winMap.keySet());
		Collections.reverse(orderedPlayers);

		EvolutionPlayer bestPlayer = orderedPlayers.get(0);

		int playerToAdd = 10 - mutatedPlayers.size();

		if (mutatedPlayers.size() < 10)
		{
			for (int i = 0; i < playerToAdd ; i++)
			{
				mutatedPlayers.add(bestPlayer.mutate(bestPlayer.getName()));
			}
		}

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

	private static Map<EvolutionPlayer, Integer> battleAllVsAll(List<EvolutionPlayer> players,
																int generation)
	{
		Map<EvolutionPlayer, Integer> winMap = new ConcurrentHashMap<>();
		ExecutorService executor = Executors.newFixedThreadPool(5);

		for (int i = 0; i < players.size(); i++)
		{
			for (int j = i; j < players.size(); j++)
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

		// add random one when nobody wins
		if (winMap.isEmpty())
		{
			winMap.put(players.get(0), 10);
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
		System.out.println(String.format("--=========== GENERATION %s ===========---", generation));
		sortedWinMap.forEach((p, win) ->
		{
			System.out.println(String.format("player: %s win %s number of genes: %s", p.getName(), win, p.getNumberOfGenes()));
		});
		System.out.println("");
		System.out.println("");

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
