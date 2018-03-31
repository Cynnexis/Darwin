package fr.berger.darwin.remixed;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public interface Mutable<T> {
	
	double calculateFitness(@NotNull Individual<T> individual);
	
	@NotNull
	Individual<T> mutate(@NotNull Individual<T> individual);
	
	@NotNull
	ArrayList<Individual<T>> mate(@NotNull Individual<T> parent1, @NotNull Individual<T> parent2);
	
	@NotNull
	Individual<T> generateRandom();
}
