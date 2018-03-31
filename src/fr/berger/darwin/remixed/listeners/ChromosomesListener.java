package fr.berger.darwin.remixed.listeners;

import fr.berger.darwin.remixed.Chromosome;
import fr.berger.enhancedlist.lexicon.Lexicon;
import org.jetbrains.annotations.NotNull;

public interface ChromosomesListener<T> {
	
	void onChromosomesChanged(@NotNull Lexicon<Chromosome<T>> chromosomes);
}
