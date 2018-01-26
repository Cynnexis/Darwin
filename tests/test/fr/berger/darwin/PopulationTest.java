package test.fr.berger.darwin;

import com.sun.istack.internal.NotNull;
import main.fr.berger.darwin.*;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PopulationTest {
	
	@NotNull
	private Population<String> population;
	
	@BeforeEach
	void setup() {
		population = new Population<>(new ArrayList<>(), 2048L, 16384L, 0.2f, 0.03f, 0.8f, new Mutable<String>() {
			
			private Random rand = new Random(System.currentTimeMillis());;
			private final String TARGET = "Hello world";
			
			@Override
			public int calculateFitness(Chromosome<String> genes) {
				int fitness = 0;
				for (int i = 0; i < genes.getGenes().get(i).getData().length(); i++)
					fitness += Math.abs(((int) genes.getGenes().get(i).getData().charAt(i)) - ((int) TARGET.charAt(i)));
				
				return fitness;
			}
			
			@Override
			public Individual<String> mutate(Individual<String> individual) {
				String gene = individual.getChromosomes().get(0).getGenes().get(0).getData();
				
				int i = rand.nextInt(gene.length());
				int delta = (rand.nextInt() % 90) + 32;
				
				StringBuilder geneBuilder = new StringBuilder(gene);
				geneBuilder.setCharAt(i, (char) ((gene.charAt(i) + delta) % 122));
				
				individual.getChromosomes().get(0).getGenes().get(0).setData(geneBuilder.toString());
				
				return individual;
			}
			
			@Override
			public ArrayList<Individual<String>> mate(Individual<String> parent1, Individual<String> parent2) {
				String g1 = parent1.getChromosomes().get(0).getGenes().get(0).getData();
				String g2 = parent2.getChromosomes().get(0).getGenes().get(0).getData();
				StringBuilder b1 = new StringBuilder(g1);
				StringBuilder b2 = new StringBuilder(g2);
				int pivot = rand.nextInt(g1.length());
				
				ArrayList<Individual<String>> children = new ArrayList<>(2);
				children.add(new Individual<>());
				children.add(new Individual<>());
				
				for (Individual<String> child : children) {
					StringBuilder builder = new StringBuilder("");
					for (int i = 0; i < pivot; i++)
						builder.append(g1.charAt(i));
					
					for (int i = pivot; i < g2.length(); i++)
						builder.append(g2.charAt(i));
					child.getChromosomes().get(0).getGenes().get(0).setData(builder.toString());
				}
				
				return children;
			}
			
			@Override
			public Individual<String> generateRandom() {
				StringBuilder builder = new StringBuilder("");
				
				for (int i = 0; i < TARGET.length(); i++)
					builder.append((char) (rand.nextInt(90) + 32));
				
				ArrayList<Gene<String>> genes = new ArrayList<>(1);
				genes.add(new Gene<>(builder.toString()));
				
				ArrayList<Chromosome<String>> chromosomes = new ArrayList<>(1);
				chromosomes.add(new Chromosome<>(genes));
				
				return new Individual<>(chromosomes);
			}
		});
	}
	
	@AfterEach
	void tearDown() {
	}
	
	@Test
	void test_evolve() {
		System.out.println(population.toString());
		
		StopWatch stop = new StopWatch();
		stop.start();
		Individual<String> best = population.getIndividuals().get(0);
		for (int i = 0; i < 3000; i++) {
			population.evolve();
			best = population.getIndividuals().get(population.getIndividuals().size() - 1);
		}
		stop.stop();
		
		System.out.println("Time: " + stop.getNanoTime() + "ns");
		System.out.println("Best: " + best.toString());
		System.out.println(population.toString());
	}
	
	@Test
	void test_selectParents() {
		System.out.println("Selected Parents: " + population.selectParents());
	}
}