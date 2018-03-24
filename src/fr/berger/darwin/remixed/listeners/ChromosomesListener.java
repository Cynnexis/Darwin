package fr.berger.darwin.remixed.listeners;

import fr.berger.darwin.remixed.Chromosome;

import java.util.ArrayList;

public interface ChromosomesListener<T> {
	
	void onChromosomesChanged(ArrayList<Chromosome<T>> chromosomes);
}
