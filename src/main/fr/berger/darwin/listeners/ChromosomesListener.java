package main.fr.berger.darwin.listeners;

import main.fr.berger.darwin.Chromosome;

import java.util.ArrayList;

public interface ChromosomesListener<T> {
	
	void onChromosomesChanged(ArrayList<Chromosome<T>> chromosomes);
}
