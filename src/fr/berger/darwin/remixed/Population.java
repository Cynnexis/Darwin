package fr.berger.darwin.remixed;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import fr.berger.beyondcode.util.EnhancedObservable;
import fr.berger.beyondcode.util.Irregular;
import fr.berger.darwin.remixed.listeners.IndividualsListener;
import fr.berger.darwin.remixed.annotations.Range;
import fr.berger.enhancedlist.lexicon.Lexicon;
import org.junit.jupiter.api.Assertions;
import org.opentest4j.AssertionFailedError;

import java.io.Serializable;
import java.util.*;

public class Population<T> extends EnhancedObservable implements Serializable, Cloneable, Iterable<Individual<T>> {

	private Lexicon<Individual<T>> individuals;
	private long size;
	private long maxGeneration;
	@Range(a = 0, b = 1)
	private float elitismRate;
	@Range(a = 0, b = 1)
	private float mutationRate;
	@Range(a = 0, b = 1)
	private float crossoverRate;
	@NotNull
	private Mutable<T> mutationAction;
	
	@NotNull
	private ArrayList<IndividualsListener<T>> individualsListeners;
	
	public Population(@NotNull Mutable<T> mutationAction) {
		initialize(null, 0, 1024, .2f, 0.1f, 0.1f, mutationAction);
	}
	public Population(@Nullable ArrayList<Individual<T>> individuals, @NotNull Mutable<T> mutationAction) {
		initialize(individuals, 0, 1024, .2f, 0.1f, 0.1f, mutationAction);
	}
	public Population(@Nullable ArrayList<Individual<T>> individuals, long size, @NotNull Mutable<T> mutationAction) {
		initialize(individuals, size, 1024, .2f, 0.1f, 0.1f, mutationAction);
	}
	public Population(@Nullable ArrayList<Individual<T>> individuals, long size, long maxGeneration, @NotNull Mutable<T> mutationAction) {
		initialize(individuals, size, maxGeneration, .2f, 0.1f, 0.1f, mutationAction);
	}
	public Population(@Nullable ArrayList<Individual<T>> individuals, long size, long maxGeneration, @Range float elitismRate, @NotNull Mutable<T> mutationAction) {
		initialize(individuals, size, maxGeneration, elitismRate, 0.1f, 0.1f, mutationAction);
	}
	public Population(@Nullable ArrayList<Individual<T>> individuals, long size, long maxGeneration, @Range float elitismRate, @Range float mutationRate, @NotNull Mutable<T> mutationAction) {
		initialize(individuals, size, maxGeneration, elitismRate, mutationRate, 0.1f, mutationAction);
	}
	public Population(@Nullable ArrayList<Individual<T>> individuals, long size, long maxGeneration, @Range float elitismRate, @Range float mutationRate, @Range float crossoverRate, @NotNull Mutable<T> mutationAction) {
		initialize(individuals, size, maxGeneration, elitismRate, mutationRate, crossoverRate, mutationAction);
	}
	
	private void initialize(@Nullable ArrayList<Individual<T>> individuals, long size, long maxGeneration, float elitismRate, float mutationRate, float crossoverRate, @NotNull Mutable<T> mutationAction) {
		Lexicon<Individual<T>> lexicon = new Lexicon<>(individuals);
		
		setIndividuals(lexicon);
		setSize(size);
		setMaxGeneration(maxGeneration);
		setElitismRate(elitismRate);
		setMutationRate(mutationRate);
		setCrossoverRate(crossoverRate);
		setMutationAction(mutationAction);
		
		for (int i = getIndividuals().size(); i < getSize(); i++)
			addIndividuals(getMutationAction().generateRandom());
	}
	
	public void evolve() {
		// Create a buffer
		ArrayList<Individual<T>> buffer = new ArrayList<>(getIndividuals().size());
		
		evaluate();
		sort();
		int pivot = Math.round(getIndividuals().size() * getElitismRate());
		
		for (int i = 0; i < pivot; i++)
			buffer.add(getIndividuals().get(i));
		
		for (int i = pivot; i < getIndividuals().size(); i++) {
			if (Irregular.rangeFloat(0, true, 1, true) <= getCrossoverRate()) {
				
				ArrayList<Individual<T>> parents = selectParents();
				
				if (parents == null || parents.size() < 2)
					continue;
				
				ArrayList<Individual<T>> children = getMutationAction().mate(parents.get(0), parents.get(1));
				
				int numberOfChildrenAdded = 0;
				for (int j = 0; j < children.size(); j++) {
					if (i < getIndividuals().size()) {
						if (Irregular.rangeFloat(0, true, 1, true) <= getMutationRate()) {
							buffer.add(getMutationAction().mutate(children.get(j)));
							numberOfChildrenAdded++;
						}
						else {
							buffer.add(children.get(j));
							numberOfChildrenAdded++;
						}
					}
				}
				
				if (numberOfChildrenAdded - 1 > 0)
					i += numberOfChildrenAdded - 1;
			}
			else {
				if (Irregular.rangeFloat(0, true, 1, true) <= getMutationRate())
					buffer.add(getMutationAction().mutate(getIndividuals().get(i)));
				else
					buffer.add(getIndividuals().get(i));
			}
		}
		
		// individuals := buffer
		setIndividuals(new Lexicon<>());
		for (Individual<T> individual : buffer)
			getIndividuals().add(individual);
		
		evaluate();
		sort();
		
		// Because the selection of the parent can be in the 'immovable' part (the first element in the buffer', more
		// than 'size' person can be in the population. That is why the first overflowed elements must be removed
		while (getIndividuals().size() > getSize())
			getIndividuals().remove((int) 0);
	}
	
	@Nullable
	public ArrayList<Individual<T>> selectParents() {
		ArrayList<Individual<T>> parents = new ArrayList<>(2);
		
		if (getIndividuals().size() < 2)
			return null;
		
		for (int i = 0; i < 2; i++) {
			
			int randomIndex = Irregular.rangeInt(0, true, getIndividuals().size(), false);
			Individual<T> individual = getIndividuals().get(randomIndex);
			
			parents.add(individual);
			
			for (int j = 0; j < 3; j++) {
				
				int idx = 0;
				do {
					idx = Irregular.rangeInt(0, true, getIndividuals().size(), false);
				} while(i == idx);
				
				if (getIndividuals().get(idx).compareTo(parents.get(i)) > 0)
					parents.set(i, getIndividuals().get(idx));
			}
		}
		
		return parents;
	}
	
	public void evaluate() {
		for (int i = 0; i < getIndividuals().size(); i++) {
			double fitness = getMutationAction().calculateFitness(getIndividuals().get(i));
			getIndividuals().get(i).setFitness(fitness);
		}
	}
	
	/**
	 * Sort the list of individuals in ascending order (from the smallest fitness to the greatest)
	 */
	public void sort() {
		getIndividuals().sort(Individual::compareTo);
	}
	
	/* GETTER & SETTER */
	
	@NotNull
	public Lexicon<Individual<T>> getIndividuals() {
		if (individuals == null) {
			setIndividuals(new Lexicon<>());
			this.individuals.setAcceptNullValues(false);
			this.individuals.addObserver(((observable, o) -> snap(o)));
		}
		
		return individuals;
	}
	
	public void setIndividuals(@NotNull Lexicon<Individual<T>> individuals) {
		if (individuals == null)
			throw new NullPointerException();
		
		this.individuals = individuals;
		this.individuals.setAcceptNullValues(false);
		this.individuals.addObserver(((observable, o) -> snap(o)));
	}
	
	@SafeVarargs
	public final void addIndividuals(@NotNull Individual<T>... individuals) {
		if (individuals == null)
			throw new NullPointerException();
		
		getIndividuals().addAll(Arrays.asList(individuals));
	}
	
	public long getSize() {
		return size;
	}
	
	public void setSize(long size) {
		if (size >= 0)
			this.size = size;
	}
	
	public long getMaxGeneration() {
		return maxGeneration;
	}
	
	public void setMaxGeneration(long maxGeneration) {
		if (maxGeneration >= 0)
			this.maxGeneration = maxGeneration;
	}
	
	public float getElitismRate() {
		return elitismRate;
	}
	
	public void setElitismRate(float elitismRate) {
		if (0f <= elitismRate && elitismRate <= 1f)
			this.elitismRate = elitismRate;
	}
	
	public float getMutationRate() {
		return mutationRate;
	}
	
	public void setMutationRate(float mutationRate) {
		if (0f <= mutationRate && mutationRate <= 1f)
			this.mutationRate = mutationRate;
	}
	
	public float getCrossoverRate() {
		return crossoverRate;
	}
	
	public void setCrossoverRate(float crossoverRate) {
		if (0f <= crossoverRate && crossoverRate <= 1f)
			this.crossoverRate = crossoverRate;
	}
	
	public @NotNull Mutable<T> getMutationAction() {
		if (this.mutationAction == null)
			this.mutationAction = new Mutable<T>() {
				@Override
				public double calculateFitness(@org.jetbrains.annotations.NotNull Individual<T> individual) {
					return 0;
				}
				
				@Override
				public Individual<T> mutate(Individual<T> individual) {
					return null;
				}
				
				@Override
				public ArrayList<Individual<T>> mate(Individual<T> parent1, Individual<T> parent2) {
					return null;
				}
				
				@Override
				public Individual<T> generateRandom() {
					return null;
				}
			};
		
		return this.mutationAction;
	}
	
	public void setMutationAction(@NotNull Mutable<T> mutationAction) {
		if (mutationAction == null)
			throw new NullPointerException();
		
		this.mutationAction = mutationAction;
	}
	
	public @NotNull ArrayList<IndividualsListener<T>> getIndividualsListeners() {
		if (this.individualsListeners == null)
			this.individualsListeners = new ArrayList<>();
		
		return this.individualsListeners;
	}
	
	public void setIndividualsListeners(@NotNull ArrayList<IndividualsListener<T>> individualsListeners) {
		if (individualsListeners == null)
			throw new NullPointerException();
		
		for (IndividualsListener<T> individualsListener : individualsListeners)
			if (individualsListener == null)
				throw new NullPointerException();
		
		this.individualsListeners = individualsListeners;
	}
	
	public void addIndividualsListener(@NotNull IndividualsListener<T> individualsListener) {
		if (individualsListener == null)
			throw new NullPointerException();
		
		getIndividualsListeners().add(individualsListener);
	}
	
	/* OVERRIDES */
	
	@Override
	public Iterator<Individual<T>> iterator() {
		return new Iterator<Individual<T>>() {
			
			private int currentIndex = 0;
			
			@Override
			public boolean hasNext() {
				return currentIndex < getIndividuals().size();
			}
			
			@Override
			public Individual<T> next() {
				return getIndividuals().get(currentIndex++);
			}
		};
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Population)) return false;
		Population<?> that = (Population<?>) o;
		return getSize() == that.getSize() &&
				getMaxGeneration() == that.getMaxGeneration() &&
				Float.compare(that.getElitismRate(), getElitismRate()) == 0 &&
				Float.compare(that.getMutationRate(), getMutationRate()) == 0 &&
				Float.compare(that.getCrossoverRate(), getCrossoverRate()) == 0 &&
				Objects.equals(getIndividuals(), that.getIndividuals()) &&
				Objects.equals(getMutationAction(), that.getMutationAction());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getIndividuals(), getSize(), getMaxGeneration(), getElitismRate(), getMutationRate(), getCrossoverRate(), getMutationAction());
	}
	
	@Override
	public String toString() {
		return "Population{" +
				"individuals=\"" + (getIndividuals().size() > 3 ? "... (" + getIndividuals().size() + ")" : getIndividuals().toString()) + '\"' +
				", size=\"" + getSize() + '\"' +
				", maxGeneration=\"" + getMaxGeneration() + '\"' +
				", elitismRate=\"" + getElitismRate() + '\"' +
				", mutationRate=\"" + getMutationRate() + '\"' +
				", crossoverRate=\"" + getCrossoverRate() + '\"' +
				", mutationAction=\"" + getMutationRate() + '\"' +
				'}';
	}
}
