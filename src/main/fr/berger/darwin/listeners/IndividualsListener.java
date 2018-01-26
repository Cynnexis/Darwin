package main.fr.berger.darwin.listeners;

import main.fr.berger.darwin.Individual;

import java.util.ArrayList;

public interface IndividualsListener<T> {
	
	void onIndividualsChanged(ArrayList<Individual<T>> individuals);
}
