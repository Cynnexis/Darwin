package fr.berger.darwin.connection.neurallayers;

import fr.berger.arrow.Ref;
import fr.berger.darwin.connection.Neuron;
import fr.berger.enhancedlist.Couple;
import fr.berger.enhancedlist.lexicon.Lexicon;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class OutputLayer extends NeuralLayer implements Serializable, Cloneable, Iterable<Neuron> {
	
	public OutputLayer(@NotNull Lexicon<Neuron> neurons) {
		super(neurons);
	}
	public OutputLayer(@NotNull Collection<Neuron> neurons) {
		super(neurons);
	}
	public OutputLayer(@NotNull Neuron... neurons) {
		super(neurons);
	}
	public OutputLayer() {
		super();
	}
	
	/* NEURAL LAYER METHODS */
	
	/**
	 * Return (activationResult, null)
	 * @return
	 */
	@Override
	public Lexicon<Couple<Double, Ref<Neuron>>> activate() {
		Lexicon<Couple<Double, Ref<Neuron>>> dendrites = new Lexicon<>();
		
		for (Neuron neuron : getNeurons()) {
			double activationResult = neuron.activate();
			
			dendrites.add(new Couple<>(activationResult, null));
		}
		
		return dendrites;
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
	
	@Override
	public String toString() {
		return "OutputLayer{" +
				"neurons=" + getNeurons() +
				'}';
	}
}
