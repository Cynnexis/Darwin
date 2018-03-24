package fr.berger.darwin.remixed.listeners;

import fr.berger.darwin.remixed.Gene;

import java.util.ArrayList;

public interface GenesListener<T> {
	
	void onGenesChanged(ArrayList<Gene<T>> genes);
}
