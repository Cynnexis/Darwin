package fr.berger.darwin.connection.neurallayers;

import fr.berger.arrow.Ref;
import fr.berger.beyondcode.util.EnhancedObservable;
import fr.berger.darwin.connection.Neuron;
import fr.berger.enhancedlist.Couple;
import fr.berger.enhancedlist.lexicon.Lexicon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class NeuralLayer extends EnhancedObservable implements Serializable, Cloneable, Iterable<Neuron> {
	
	/* PROPERTY */
	
	@NotNull
	private Lexicon<Neuron> neurons;
	
	/* CONSTRUCTORS & INITIALIZING METHODS */
	
	public NeuralLayer(@NotNull Lexicon<Neuron> neurons) {
		super();
		initialize(neurons);
	}
	public NeuralLayer(@NotNull Collection<Neuron> neurons) {
		super();
		initialize(new Lexicon<>(neurons));
	}
	public NeuralLayer(@NotNull Neuron... neurons) {
		super();
		initialize(new Lexicon<>(neurons));
	}
	public NeuralLayer() {
		super();
		initialize(null);
	}
	
	protected void initialize(@Nullable Lexicon<Neuron> neurons) {
		if (neurons == null)
			initNeurons();
		else
			setNeurons(neurons);
	}
	
	/* NEURONAL LAYER METHODS */
	
	public Lexicon<Couple<Double, Ref<Neuron>>> activate() {
		Lexicon<Couple<Double, Ref<Neuron>>> dendrites = new Lexicon<>();
		
		for (Neuron neuron : getNeurons()) {
			double activationResult = neuron.activate();
			
			for (Ref<Neuron> synapse : neuron.getSynapses())
				dendrites.add(new Couple<>(activationResult, synapse));
		}
		
		return dendrites;
	}
	
	/* GETTER & SETTER */
	
	@SuppressWarnings("ConstantConditions")
	@NotNull
	public Lexicon<Neuron> getNeurons() {
		if (neurons == null)
			initNeurons();
		
		return neurons;
	}
	
	@SuppressWarnings("ConstantConditions")
	public void setNeurons(@NotNull Lexicon<Neuron> neurons) {
		if (neurons == null)
			throw new NullPointerException();
		
		this.neurons = neurons;
		configureNeurons();
	}
	
	public void initNeurons() {
		setNeurons(new Lexicon<>(Neuron.class));
	}
	
	@SuppressWarnings("ConstantConditions")
	public void configureNeurons() {
		getNeurons().setAcceptNullValues(false);
		getNeurons().addAddHandler((index, neuron) -> {
			for (int i = 0; i < getNeurons().size(); i++) {
				if (index != i && getNeurons().get(i).getId().equals(neuron.getId()))
					throw new IllegalArgumentException("Two neurons cannot have the same ID (Neuron n°" + index + "\": " + neuron.toString()  + "\" ; Neuron n°" + i + ": \"" + getNeurons().get(i) + "\").");
			}
		});
		getNeurons().addSetHandler(((index, element) -> getNeurons().getAddHandlers().get(0).onElementAdded(index, element)));
		getNeurons().addObserver((observable, o) -> snap(o));
		snap(getNeurons());
	}
	
	/* SERIALIZATION METHODS */
	
	private void writeObject(@NotNull ObjectOutputStream stream) throws IOException {
		stream.writeObject(getNeurons());
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(@NotNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
		setNeurons((Lexicon<Neuron>) stream.readObject());
	}
	
	/* OVERRIDES */
	
	@NotNull
	@Override
	public Iterator iterator() {
		return getNeurons().iterator();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof NeuralLayer)) return false;
		NeuralLayer that = (NeuralLayer) o;
		return Objects.equals(getNeurons(), that.getNeurons());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getNeurons());
	}
	
	@Override
	public String toString() {
		return "NeuralLayer{" +
				"neurons=" + neurons +
				'}';
	}
}
