package main.fr.berger.darwin.listeners;

import main.fr.berger.darwin.Gene;

import java.util.ArrayList;

public interface GenesListener<T> {
	
	void onGenesChanged(ArrayList<Gene<T>> genes);
}
