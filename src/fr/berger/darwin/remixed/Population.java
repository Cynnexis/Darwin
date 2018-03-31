package fr.berger.darwin.remixed;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import fr.berger.beyondcode.util.EnhancedObservable;
import fr.berger.darwin.remixed.listeners.IndividualsListener;
import fr.berger.darwin.remixed.annotations.Range;
import fr.berger.enhancedlist.lexicon.Lexicon;

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
		
		//sort();
		int i = Math.round(getIndividuals().size() * getElitismRate());
		
		for (int k = 0; k < i; k++)
			buffer.add(getIndividual(k));
		
		Random rand = new Random(System.currentTimeMillis());
		
		for (; i < getIndividuals().size(); i++) {
			if (rand.nextFloat() <= getCrossoverRate()) {
				ArrayList<Individual<T>> parents = selectParents();
				
				if (parents == null || parents.size() < 2)
					return;
				
				ArrayList<Individual<T>> children = getMutationAction().mate(parents.get(0), parents.get(1));
				
				for (int j = 0; j < children.size(); j++) {
					if (i < getIndividuals().size()) {
						if (rand.nextFloat() <= getMutationRate()) {
							buffer.add(getMutationAction().mutate(children.get(j)));
							i++;
						}
						else {
							buffer.add(children.get(j));
							i++;
						}
					}
				}
			}
			else {
				if (rand.nextFloat() <= getMutationRate())
					buffer.add(getMutationAction().mutate(getIndividuals().get(i)));
				else
					buffer.add(getIndividuals().get(i));
			}
		}
		
		// individuals := buffer
		setIndividuals(new ArrayList<>(buffer.size()));
		for (Individual<T> individual : buffer)
			getIndividuals().add(individual);
		
		sort();
	}
	
	public @Nullable ArrayList<Individual<T>> selectParents() {
		ArrayList<Individual<T>> parents = new ArrayList<>(2);
		
		if (getIndividuals().size() < 2)
			return null;
		
		Random rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < 2; i++) {
			parents.add(getIndividuals().get(rand.nextInt(getIndividuals().size())));
			for (int j = 0; j < 3; j++) {
				int idx = rand.nextInt(getIndividuals().size());
				if (getIndividuals().get(idx).compareTo(parents.get(i)) < 0)
					parents.set(i, getIndividuals().get(idx));
			}
		}
		
		return parents;
	}
	
	/*
	@Override
	public void sort() {
		getIndividuals().sort(new Comparator<Individual<T>>() {
			@Override
			public int compare(Individual<T> o1, Individual<T> o2) {
				if (o1 == null && o2 == null)
					return 0;
				
				if (o1 != null && o2 == null)
					return 1;
				
				if (o1 == null && o2 != null)
					return -1;
				
				if (o1.getChromosomes().size() == 0 || o2.getChromosomes().size() == 0)
					return 0;
				
				if (o1.getChromosomes().size() > 0 && o2.getChromosomes().size() == 0)
					return 1;
				
				if (o1.getChromosomes().size() == 0 && o2.getChromosomes().size() > 0)
					return -1;
				
				return o1.compareTo(o2);
			}
		});
	}*/
	
	/* GETTER & SETTER */
	
	@NotNull
	public Lexicon<Individual<T>> getIndividuals() {
		if (individuals == null)
			setIndividuals(new Lexicon<>());
		
		return individuals;
	}
	
	public void setIndividuals(@NotNull Lexicon<Individual<T>> individuals) {
		if (individuals == null)
			throw new NullPointerException();
		
		this.individuals = individuals;
		this.individuals.deleteNullElement();
		this.individuals.setAcceptNullValues(false);
		this.individuals.addObserver(((observable, o) -> {
			snap(o);
		}));
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
				"individuals=" + individuals +
				", size=" + size +
				", maxGeneration=" + maxGeneration +
				", elitismRate=" + elitismRate +
				", mutationRate=" + mutationRate +
				", crossoverRate=" + crossoverRate +
				", mutationAction=" + mutationAction +
				'}';
	}
}
