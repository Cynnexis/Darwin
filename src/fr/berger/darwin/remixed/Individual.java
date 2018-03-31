package fr.berger.darwin.remixed;

import fr.berger.beyondcode.annotations.NotEmpty;
import fr.berger.beyondcode.util.EnhancedObservable;
import fr.berger.darwin.remixed.listeners.ChromosomesListener;
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

public class Individual<T> extends EnhancedObservable implements Serializable, Cloneable, Iterable<Chromosome<T>> {
	
	@NotNull
	private UUID id;
	
	@NotNull
	private Lexicon<Chromosome<T>> chromosomes = new Lexicon<>();
	
	@NotNull
	private ArrayList<ChromosomesListener<T>> chromosomesListeners;
	
	/*@NotNull
	private ArrayList<FitnessListener> fitnessListeners;*/
	
	public Individual(@Nullable Lexicon<Chromosome<T>> chromosomes) {
		initialize(chromosomes);
	}
	public Individual(@Nullable ArrayList<Chromosome<T>> chromosomes) {
		initialize(new Lexicon<>(chromosomes));
	}
	public Individual(@Nullable Chromosome<T>... chromosomes) {
		initialize(new Lexicon<>(chromosomes));
	}
	public Individual(@NotNull Individual<T> copy) {
		if (copy == null)
			throw new NullPointerException();
		
		initialize(copy.getChromosomes());
	}
	public Individual() {
		initialize(null);
	}
	
	private void initialize(@Nullable Lexicon<Chromosome<T>> chromosomes) {
		// Generate random ID
		setId(UUID.randomUUID());
		
		if (chromosomes == null)
			chromosomes = new Lexicon<>();
		
		setChromosomes(chromosomes);
		setChromosomesListeners(new ArrayList<>());
	}
	
	/* GETTER & SETTER */
	
	@NotNull
	public UUID getId() {
		if (id == null)
			id = UUID.randomUUID();
		
		return id;
	}
	
	public void setId(@NotNull UUID id) {
		if (id == null)
			throw new NullPointerException();
		
		this.id = id;
	}
	
	public @NotNull Lexicon<Chromosome<T>> getChromosomes() {
		if (this.chromosomes == null)
			this.chromosomes = new Lexicon<>();
		
		return this.chromosomes;
	}
	
	public void setChromosomes(@NotNull Lexicon<Chromosome<T>> chromosomes) {
		if (chromosomes == null)
			throw new NullPointerException();
		
		for (Chromosome<T> chromosome : chromosomes)
			if (chromosome == null)
				throw new NullPointerException();
		
		this.chromosomes = chromosomes;
		
		this.chromosomes.setAcceptNullValues(false);
		this.chromosomes.deleteNullElement();
		this.chromosomes.addObserver((observable, o) -> snap(o));
		
		for (ChromosomesListener<T> chromosomesListener : getChromosomesListeners())
			chromosomesListener.onChromosomesChanged(this.chromosomes);
	}
	
	public @NotNull ArrayList<ChromosomesListener<T>> getChromosomesListeners() {
		if (this.chromosomesListeners == null)
			this.chromosomesListeners = new ArrayList<>();
		
		return this.chromosomesListeners;
	}
	
	public void setChromosomesListeners(ArrayList<ChromosomesListener<T>> chromosomesListeners) {
		if (chromosomesListeners == null)
			throw new NullPointerException();
		
		for (ChromosomesListener<T> chromosomesListener : chromosomesListeners)
			if (chromosomesListener == null)
				throw new NullPointerException();
		
		this.chromosomesListeners = chromosomesListeners;
	}
	
	public void addChromosomesListener(@NotNull ChromosomesListener<T> chromosomesListener) {
		if (chromosomesListener == null)
			throw new NullPointerException();
		
		if (getChromosomesListeners() == null)
			this.chromosomesListeners = new ArrayList<>();
		
		getChromosomesListeners().add(chromosomesListener);
	}
	
	/*public @NotNull ArrayList<FitnessListener> getFitnessListeners() {
		if (this.fitnessListeners == null)
			this.fitnessListeners = new ArrayList<>();
		
		return this.fitnessListeners;
	}
	
	public void setFitnessListeners(ArrayList<FitnessListener> fitnessListeners) {
		if (fitnessListeners == null)
			throw new NullPointerException();
		
		for (FitnessListener fitnessListener : fitnessListeners)
			if (fitnessListener == null)
				throw new NullPointerException();
		
		this.fitnessListeners = fitnessListeners;
	}
	
	public void addFitnessListener(@NotNull FitnessListener fitnessListener) {
		if (fitnessListener == null)
			throw new NullPointerException();
		
		if (getFitnessListeners() == null)
			this.fitnessListeners = new ArrayList<>();
		
		getFitnessListeners().add(fitnessListener);
	}*/
	
	/* SERIALIZATION METHODS */
	
	private void writeObject(@NotNull ObjectOutputStream stream) throws IOException {
		stream.writeObject(getChromosomes());
		stream.writeObject(getChromosomesListeners());
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(@NotNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
		setChromosomes((Lexicon<Chromosome<T>>) stream.readObject());
		setChromosomesListeners((ArrayList<ChromosomesListener<T>>) stream.readObject());
	}
	
	/* OVERRIDES */
	
	@Override
	public Iterator<Chromosome<T>> iterator() {
		return new Iterator<Chromosome<T>>() {
			
			private int currentIndex = 0;
			
			@Override
			public boolean hasNext() {
				return currentIndex < getChromosomes().size();
			}
			
			@Override
			public Chromosome<T> next() {
				return getChromosomes().get(currentIndex++);
			}
		};
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Individual)) return false;
		Individual<?> that = (Individual<?>) o;
		return Objects.equals(getId(), that.getId()) &&
				Objects.equals(getChromosomes(), that.getChromosomes());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getId(), getChromosomes());
	}
	
	@Override
	public String toString() {
		return "Individual{" +
				"id=" + getId().toString() +
				", chromosomes=" + getChromosomes().toString() +
				'}';
	}
}
