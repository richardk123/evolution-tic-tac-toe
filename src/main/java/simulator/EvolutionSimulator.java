package simulator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import player.EvolutionPlayer;
import util.XmlUtils;

/**
 * @author Kolisek
 */
public class EvolutionSimulator implements Runnable
{
	public static final Integer PLAYER_COUNT = 20;
	private AtomicInteger seq = new AtomicInteger();

	@Override
	public void run()
	{
		// generate first population
		List<EvolutionPlayer> population = generatePopulation(10, 0);

		// lets evolution begin
		evolve(population, 1);
	}

	private Map<EvolutionPlayer, Integer> evolve(@Nonnull final List<EvolutionPlayer> population,
												int generation)
	{
		// fight all vs all
		Map<EvolutionPlayer, Integer> winStats = battleAllVsAll(population, generation);

		List<EvolutionPlayer> mutatedPopulation = mutatePopulation(population, winStats, generation);

		return evolve(mutatedPopulation, generation + 1);
	}


	private List<EvolutionPlayer> mutatePopulation(@Nonnull final List<EvolutionPlayer> population,
												   @Nonnull final Map<EvolutionPlayer, Integer> winMap,
												   int generation)
	{
		final List<EvolutionPlayer> mutatedPlayers = new ArrayList<>();

		// if nobody wins, add one random
		if (winMap.isEmpty())
		{
			List<EvolutionPlayer> newGeneration = new ArrayList<>();
			newGeneration.addAll(population);
			// remove last individual from end
			newGeneration.remove(newGeneration.size() - 1);
			// generate new individual, append at the end
			newGeneration.addAll(generatePopulation(1, generation));

			// mutate them
			return newGeneration.stream()
					.map(p -> p.mutate(generatePlayerName(generation)))
					.collect(Collectors.toList());
		}

		final int sumOfWins = winMap.values().stream()
				.mapToInt(i -> i)
				.sum();

		winMap.forEach((p, winCount) ->
		{
			int numberOfChildren = Math.round(((float)winCount / sumOfWins) * PLAYER_COUNT);

			for (int i = 0; i < numberOfChildren; i++)
			{
				mutatedPlayers.add(p.mutate(generatePlayerName(generation)));
			}
		});

		List<EvolutionPlayer> orderedPlayers = new ArrayList<>(winMap.keySet());
		Collections.reverse(orderedPlayers);

		EvolutionPlayer bestPlayer = orderedPlayers.get(0);

		// persist as xml
		XmlUtils.savePlayer(bestPlayer);

		int playerToAdd = PLAYER_COUNT - mutatedPlayers.size();

		if (mutatedPlayers.size() < PLAYER_COUNT)
		{
			for (int i = 0; i < playerToAdd ; i++)
			{
				mutatedPlayers.add(bestPlayer.mutate(generatePlayerName(generation)));
			}
		}

		return mutatedPlayers;
	}

	private String generatePlayerName(int generation)
	{
		return "p#" + generation + "." + seq.incrementAndGet();
	}

	private List<EvolutionPlayer> generatePopulation(int count, int generation)
	{
		List<EvolutionPlayer> players = new ArrayList<>();

		for (int i = 0; i < count; i++)
		{
			players.add(new EvolutionPlayer(generatePlayerName(generation)));
		}

		return players;
	}

	private static Map<EvolutionPlayer, Integer> battleAllVsAll(List<EvolutionPlayer> players,
																int generation)
	{
		Map<EvolutionPlayer, Integer> winMap = new ConcurrentHashMap<>();
		ExecutorService executor = Executors.newFixedThreadPool(4);

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

		LinkedHashMap<EvolutionPlayer, Integer> sortedWinMap = winMap.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(e1, e2) -> e1,
						LinkedHashMap::new
				));

		System.out.println(String.format("---=========== GENERATION %s ===========---", generation));

		if (winMap.isEmpty())
		{
			System.out.println("nobody wins replace one individual");
		}

		sortedWinMap.forEach((p, win) ->
		{
			String areas = p.getGenes().stream()
					.map(g -> g.getPattern().getWidth() + "*" + g.getPattern().getHeight())
					.collect(Collectors.joining(", "));

			System.out.println(
					String.format(
							"player: %s, win %s times, number of genes: %s, gene size: %s",
							p.getName(), win, p.getNumberOfGenes(), areas));
		});

		System.out.println();

		return sortedWinMap;
	}
}
