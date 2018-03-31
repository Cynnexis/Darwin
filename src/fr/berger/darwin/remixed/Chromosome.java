package fr.berger.darwin.remixed;

import fr.berger.beyondcode.annotations.NotEmpty;
import fr.berger.beyondcode.util.EnhancedObservable;
import fr.berger.darwin.remixed.listeners.FitnessListener;
import fr.berger.darwin.remixed.listeners.GenesListener;
import fr.berger.enhancedlist.lexicon.Lexicon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class Chromosome<T> extends EnhancedObservable implements Serializable, Cloneable, Iterable<Gene<T>> {

	@NotNull
	private Lexicon<Gene<T>> genes;
	
	@NotNull
	private ArrayList<GenesListener<T>> genesListeners;
	
	public Chromosome(@Nullable Lexicon<Gene<T>> genes) {
		initialize(genes);
	}
	public Chromosome(@Nullable ArrayList<Gene<T>> genes) {
		initialize(new Lexicon<>(genes));
	}
	public Chromosome(@Nullable Gene<T>... genes) {
		initialize(new Lexicon<>(genes));
	}
	public Chromosome(@NotNull Chromosome<T> chromosome) {
		if (chromosome == null)
			throw new NullPointerException("chromosome copy cannot be null");
		
		initialize(chromosome.getGenes());
		setGenesListeners(chromosome.getGenesListeners());
	}
	public Chromosome() {
		initialize(new Lexicon<>());
	}
	
	private void initialize(@Nullable Lexicon<Gene<T>> genes) {
		if (genes == null)
			genes = new Lexicon<>();
		
		setGenes(genes);
		setGenesListeners(new ArrayList<>());
	}
	
	/* SERIALIZATION METHODS */
	
	private void writeObject(@NotNull ObjectOutputStream stream) throws IOException {
		stream.writeObject(getGenes());
		stream.writeObject(getGenesListeners());
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(@NotNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
		setGenes((Lexicon<Gene<T>>) stream.readObject());
		setGenesListeners((ArrayList<GenesListener<T>>) stream.readObject());
	}
	
	/* GETTERS & SETTERS */
	
	@NotNull
	public Lexicon<Gene<T>> getGenes() {
		if (this.genes == null)
			this.genes = new Lexicon<>();
		
		return genes;
	}
	
	public void setGenes(@NotNull Lexicon<Gene<T>> genes) {
		if (genes == null)
			throw new NullPointerException();
		
		for (Gene<T> gene : genes)
			if (gene == null)
				throw new NullPointerException();
		
		this.genes = genes;
		
		this.genes.setAcceptNullValues(false);
		this.genes.deleteNullElement();
		this.genes.addObserver((observable, o) -> snap(o));
		
		for (GenesListener<T> genesListener : getGenesListeners())
			genesListener.onGenesChanged(this.genes);
	}
	
	@NotNull
	public ArrayList<GenesListener<T>> getGenesListeners() {
		if (this.genesListeners == null)
			this.genesListeners = new ArrayList<>();
		
		return this.genesListeners;
	}
	
	public void setGenesListeners(@NotNull ArrayList<GenesListener<T>> genesListeners) {
		if (genesListeners == null)
			throw new NullPointerException();
		
		for (GenesListener<T> genesListener : genesListeners)
			if (genesListener == null)
				throw new NullPointerException();
		
		this.genesListeners = genesListeners;
	}
	
	public void addGenesListener(@NotNull GenesListener<T> genesListener) {
		if (genesListener == null)
			throw new NullPointerException();
		
		if (getGenesListeners() == null)
			this.genesListeners = new ArrayList<>();
		
		getGenesListeners().add(genesListener);
	}
	
	/* OVERRIDES */
	
	@Override
	@NotNull
	public Iterator<Gene<T>> iterator() {
		return new Iterator<Gene<T>>() {
			
			private int currentIndex = 0;
			
			@Override
			public boolean hasNext() {
				return currentIndex < getGenes().size();
			}
			
			@Override
			public Gene<T> next() {
				return getGenes().get(currentIndex++);
			}
		};
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Chromosome)) return false;
		Chromosome<?> that = (Chromosome<?>) o;
		return Objects.equals(getGenes(), that.getGenes());
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(getGenes());
	}
	
	@Override
	@NotNull
	@NotEmpty
	public String toString() {
		return "Chromosome{" +
				"genes=" + genes +
				'}';
	}
}
