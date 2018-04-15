package fr.berger.darwin.connection.neurallayers;

import fr.berger.beyondcode.annotations.Positive;
import fr.berger.darwin.connection.Neuron;
import fr.berger.darwin.connection.Triggerable;
import fr.berger.darwin.connection.handlers.ActivationHandler;
import fr.berger.enhancedlist.lexicon.Lexicon;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;

public class OutputLayer extends NeuralLayer implements Triggerable, Serializable, Cloneable {
	
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
