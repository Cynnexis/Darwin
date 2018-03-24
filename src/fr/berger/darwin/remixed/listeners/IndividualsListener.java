package fr.berger.darwin.remixed.listeners;

import fr.berger.darwin.remixed.Individual;

import java.util.ArrayList;

public interface IndividualsListener<T> {
	
	void onIndividualsChanged(ArrayList<Individual<T>> individuals);
}
