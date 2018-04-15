package fr.berger.darwin.connection.neurallayers;

import com.sun.org.glassfish.gmbal.ParameterNames;
import fr.berger.arrow.Ref;
import fr.berger.beyondcode.annotations.Positive;
import fr.berger.beyondcode.util.EnhancedObservable;
import fr.berger.darwin.connection.Neuron;
import fr.berger.darwin.connection.Triggerable;
import fr.berger.darwin.connection.handlers.ActivationHandler;
import fr.berger.enhancedlist.lexicon.Lexicon;
import fr.berger.enhancedlist.lexicon.eventhandlers.AddHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

public class NeuralLayer extends EnhancedObservable implements Triggerable, Serializable, Cloneable {
	
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
	
	public Lexicon<Double> activate(@NotNull Lexicon<Double> inputsForNeurons) {
		Lexicon<Double> outputs = new Lexicon<>(Double.class, getNeurons().size());
		
		for (Neuron neuron : getNeurons())
			outputs.add(neuron.activate(inputsForNeurons));
		
		return outputs;
	}
	public Lexicon<Double> activate(@NotNull double... inputsForNeurons) {
		return activate(new Lexicon<>(Arrays.stream(inputsForNeurons).boxed().toArray(Double[]::new)));
	}
	public Lexicon<Double> activate() {
		Lexicon<Double> outputs = new Lexicon<>(Double.class, getNeurons().size());
		
		for (Neuron neuron : getNeurons())
			outputs.add(neuron.activate());
		
		return outputs;
	}
	
	public double fire() {
		double activationResult = 0;
		for (Neuron neuron : getNeurons())
			activationResult += neuron.fire();
		
		return activationResult;
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
		//getNeurons().setAcceptDuplicates(false);
		//getNeurons().setSynchronizedAccess(false);
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
