package main.fr.berger.darwin;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import main.fr.berger.darwin.listeners.GenesListener;
import main.fr.berger.darwin.listeners.FitnessListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Chromosome<T> implements Serializable, Comparable<Chromosome<T>>, Iterable<Gene<T>> {

	@NotNull
	private ArrayList<Gene<T>> genes;
	@NotNull
	private int fitness;
	
	@NotNull
	private ArrayList<GenesListener<T>> genesListeners;
	@NotNull
	private ArrayList<FitnessListener> fitnessListeners;
	
	public Chromosome(@Nullable ArrayList<Gene<T>> genes, @NotNull int fitness) {
		initialize(genes, fitness);
	}
	public Chromosome(@Nullable Gene<T> gene, @NotNull int fitness) {
		ArrayList<Gene<T>> list = new ArrayList<>();
		if (gene != null)
			list.add(gene);
		
		initialize(list, fitness);
	}
	public Chromosome(@Nullable ArrayList<Gene<T>> genes) {
		initialize(genes, 0);
	}
	public Chromosome(@Nullable Gene<T> gene) {
		ArrayList<Gene<T>> list = new ArrayList<>();
		if (gene != null)
			list.add(gene);
		
		initialize(list, 0);
	}
	public Chromosome(@NotNull Chromosome<T> chromosome) {
		if (chromosome == null)
			throw new NullPointerException("chromosome copy cannot be null");
			
		initialize(chromosome.getGenes(), chromosome.getFitness());
	}
	public Chromosome() {
		initialize(new ArrayList<>(), 0);
	}
	
	private void initialize(@Nullable ArrayList<Gene<T>> genes, @NotNull int fitness) {
		ListUtility.deleteNullItems(genes);
		
		setGenes(genes != null ? genes : new ArrayList<>());
		setFitness(fitness);
		setGenesListeners(new ArrayList<>());
		setFitnessListeners(new ArrayList<>());
	}
	
	/* GETTERS & SETTERS */
	
	public @NotNull ArrayList<Gene<T>> getGenes() {
		if (this.genes == null)
			this.genes = new ArrayList<>();
		
		return genes;
	}
	
	public void setGenes(@Nullable ArrayList<Gene<T>> genes) {
		if (genes == null)
			throw new NullPointerException();
		
		for (Gene<T> gene : genes)
			if (gene == null)
				throw new NullPointerException();
		
		this.genes = genes;
		
		for (GenesListener<T> genesListener : getGenesListeners())
			genesListener.onGenesChanged(this.genes);
	}
	
	public int getFitness() {
		return fitness;
	}
	
	public void setFitness(int fitness) {
		this.fitness = fitness;
		
		for (FitnessListener fitnessListener : getFitnessListeners())
			fitnessListener.onFitnessChanged(this.fitness);
	}
	
	public @NotNull ArrayList<GenesListener<T>> getGenesListeners() {
		if (this.genesListeners == null)
			this.genesListeners = new ArrayList<>();
		
		return this.genesListeners;
	}
	
	public void setGenesListeners(ArrayList<GenesListener<T>> genesListeners) {
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
	
	public @NotNull ArrayList<FitnessListener> getFitnessListeners() {
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
	}
	
	/* OVERRIDES */
	
	@Override
	public String toString() {
		return "Chromosome{" +
				"genes=" + genes +
				", fitness=" + fitness +
				'}';
	}
	
	@Override
	public int compareTo(Chromosome<T> c) {
		if (getFitness() < c.getFitness())
			return -1;
		else if (getFitness() > c.getFitness())
			return 1;
		
		return 0;
	}
	
	@Override
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
		return getFitness() == that.getFitness() &&
				Objects.equals(getGenes(), that.getGenes());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getGenes(), getFitness());
	}
}
