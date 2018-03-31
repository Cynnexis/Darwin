package fr.berger.darwin.remixed;

import fr.berger.beyondcode.util.Irregular;
import fr.berger.enhancedlist.lexicon.Lexicon;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

class PopulationTest {
	
	@NotNull
	private Population<String> population;
	
	private long size = 2048L;
	
	private final String TARGET = "I'm learning to write this sentence.";
	
	@BeforeEach
	void setup() {
		population = new Population<>(new ArrayList<>(), size, 16384L, 0.2f, 0.03f, 0.8f, new Mutable<String>() {
			
			@Override
			public double calculateFitness(@NotNull Individual<String> individual) {
				double fitness = 0;
				try {
					for (int i = 0; i < Integer.min(individual.getChromosomes().get(0).getGenes().get(0).getData().length(), TARGET.length()); i++)
						fitness += -Math.abs(((int) individual.getChromosomes().get(0).getGenes().get(0).getData().charAt(i)) - ((int) TARGET.charAt(i)));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				return fitness;
			}
			
			@Override
			public Individual<String> mutate(@NotNull Individual<String> individual) {
				String gene = individual.getChromosomes().get(0).getGenes().get(0).getData();
				
				int i = Irregular.rangeInt(0, true, gene.length(), false);
				char delta = Irregular.rangeChar((char) 32, true, (char) 126, true);
				
				StringBuilder geneBuilder = new StringBuilder(gene);
				geneBuilder.setCharAt(i, delta);
				
				individual.getChromosomes().get(0).getGenes().get(0).setData(geneBuilder.toString());
				
				return individual;
			}
			
			@Override
			public ArrayList<Individual<String>> mate(@NotNull Individual<String> parent1, @NotNull Individual<String> parent2) {
				if (parent1 == null || parent2 == null)
					throw new NullPointerException();
				
				String g1 = parent1.getChromosomes().get(0).getGenes().get(0).getData();
				String g2 = parent2.getChromosomes().get(0).getGenes().get(0).getData();
				StringBuilder b1 = new StringBuilder();
				StringBuilder b2 = new StringBuilder();
				
				int pivot = Irregular.rangeInt(0, true, Integer.min(g1.length(), g2.length()), false);
				
				ArrayList<Individual<String>> children = new ArrayList<>(2);
				@SuppressWarnings("unchecked")
				Individual<String> child1 = new Individual<>(new Chromosome<>(new Gene<>(String.class)));
				Individual<String> child2 = new Individual<>(new Chromosome<>(new Gene<>(String.class)));
				
				for (int i = 0; i < pivot; i++)
					b1.append(g1.charAt(i));
				for (int i = pivot; i < g2.length(); i++)
					b1.append(g2.charAt(i));
				
				for (int i = 0; i < pivot; i++)
					b2.append(g2.charAt(i));
				for (int i = pivot; i < g1.length(); i++)
					b2.append(g1.charAt(i));
				
				Assertions.assertEquals(TARGET.length(), b1.length());
				Assertions.assertEquals(b1.length(), b2.length());
				
				child1.getChromosomes().get(0).getGenes().get(0).setData(b1.toString());
				child2.getChromosomes().get(0).getGenes().get(0).setData(b2.toString());
				
				children.add(child1);
				children.add(child2);
				
				return children;
			}
			
			@Override
			public Individual<String> generateRandom() {
				StringBuilder builder = new StringBuilder("");
				
				builder = new StringBuilder(Irregular.rangeString(TARGET.length(), TARGET.length(), true, false));
				
				Assertions.assertEquals(TARGET.length(), builder.length());
				
				ArrayList<Gene<String>> genes = new ArrayList<>(1);
				genes.add(new Gene<>(builder.toString()));
				
				Lexicon<Chromosome<String>> chromosomes = new Lexicon<>((Class<Chromosome<String>>) new Chromosome<String>().getClass(), 1);
				chromosomes.add(new Chromosome<>(genes));
				
				return new Individual<>(chromosomes);
			}
		});
	}
	
	@Test
	void test_evolve() {
		System.out.println(population.toString());
		
		long start = System.currentTimeMillis();
		Individual<String> best = population.getIndividuals().get(0);
		int i;
		for (i = 0; i < 1000 && !Objects.equals(best.getChromosomes().get(0).getGenes().get(0).getData(), TARGET); i++) {
			population.evolve();
			if (population.getIndividuals().size() > 0)
				best = population.getIndividuals().get(population.getIndividuals().size() - 1);
		}
		long stop = System.currentTimeMillis();
		
		//Assertions.assertEquals(size, population.getSize());
		
		System.out.println("Time: " + (stop - start) + "ms");
		System.out.println("Number of iteration: " + i);
		System.out.println("Best: " + population.getIndividuals().get(population.getIndividuals().size() - 1).toString());
		System.out.println(population.toString());
	}
}