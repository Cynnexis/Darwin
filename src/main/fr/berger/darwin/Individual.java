package main.fr.berger.darwin;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import main.fr.berger.darwin.listeners.ChromosomesListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

public class Individual<T> implements Serializable, Sortable, Comparable<Individual<T>>, Iterable<Chromosome<T>> {
	
	@NotNull
	private ArrayList<Chromosome<T>> chromosomes = new ArrayList<>();
	
	@NotNull
	private ArrayList<ChromosomesListener<T>> chromosomesListeners;
	
	public Individual(@Nullable ArrayList<Chromosome<T>> chromosomes) {
		initialize(chromosomes);
	}
	public Individual(@Nullable Chromosome<T> chromosome) {
		ArrayList<Chromosome<T>> list = new ArrayList<>();
		if (chromosome != null)
			list.add(chromosome);
		
		initialize(list);
	}
	public Individual(@Nullable Individual<T> copy) {
		if (copy != null)
			initialize(copy.getChromosomes());
		else
			initialize(null);
	}
	public Individual() {
		initialize(null);
	}
	
	private void initialize(@Nullable ArrayList<Chromosome<T>> chromosomes) {
		ListUtility.deleteNullItems(chromosomes);
		
		setChromosomes(chromosomes != null ? chromosomes : new ArrayList<>());
		sort();
	}
	
	@Override
	public int compareTo(Individual<T> individual) {
		int fitness1 = 0, fitness2 = 0;
		
		for (Chromosome<T> c : this.getChromosomes())
			fitness1 += c.getFitness();
		
		for (Chromosome<T> c : individual.getChromosomes())
			fitness2 += c.getFitness();
		
		return Integer.compare(fitness1, fitness2);
	}
	
	@Override
	public void sort() {
		getChromosomes().sort(new Comparator<Chromosome<T>>() {
			@Override
			public int compare(Chromosome<T> o1, Chromosome<T> o2) {
				return o1.compareTo(o2);
			}
		});
	}
	
	/* GETTER & SETTER */
	
	public @NotNull ArrayList<Chromosome<T>> getChromosomes() {
		if (this.chromosomes == null)
			this.chromosomes = new ArrayList<>();
		
		return this.chromosomes;
	}
	
	public void setChromosomes(@NotNull ArrayList<Chromosome<T>> chromosomes) {
		if (chromosomes == null)
			throw new NullPointerException();
		
		for (Chromosome<T> chromosome : chromosomes)
			if (chromosome == null)
				throw new NullPointerException();
		
		this.chromosomes = chromosomes;
		sort();
		
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
	
	/* OVERRIDES */
	
	@Override
	public String toString() {
		return "Individual{" +
				"chromosomes=" + chromosomes +
				'}';
	}
	
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
		return Objects.equals(getChromosomes(), that.getChromosomes());
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(getChromosomes());
	}
}
