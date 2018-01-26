package main.fr.berger.darwin;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Comparator;

public interface Mutable<T> {
	int calculateFitness(Chromosome<T> genes);
	
	@NotNull Individual<T> mutate(Individual<T> individual);
	
	@NotNull ArrayList<Individual<T>> mate(Individual<T> parent1, Individual<T> parent2);
	
	@NotNull Individual<T> generateRandom();
}
