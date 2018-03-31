package fr.berger.darwin.remixed.listeners;

import fr.berger.darwin.remixed.Gene;
import fr.berger.enhancedlist.lexicon.Lexicon;
import org.jetbrains.annotations.NotNull;

public interface GenesListener<T> {
	
	void onGenesChanged(@NotNull Lexicon<Gene<T>> genes);
}
