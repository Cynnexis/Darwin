package fr.berger.darwin.connection.neurallayers;

import fr.berger.beyondcode.annotations.Positive;
import fr.berger.beyondcode.util.EnhancedObservable;
import fr.berger.darwin.connection.Neuron;
import fr.berger.darwin.connection.handlers.ActivationHandler;
import fr.berger.enhancedlist.lexicon.Lexicon;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

public class NeuralLayer extends EnhancedObservable implements Serializable, Cloneable {
	
	/* PROPERTY */
	
	@NotNull
	private Lexicon<Neuron> neurons;
	
	/* CONSTRUCTORS & INITIALIZING METHODS */
	
	public NeuralLayer(@Positive int defaultNumberOfNeurons, @Positive int defaultNumberOfInputs, @NotNull ActivationHandler defaultActivationHandler) {
		initialize(defaultNumberOfNeurons, defaultNumberOfInputs, defaultActivationHandler);
	}
	public NeuralLayer(@Positive int defaultNumberOfNeurons, @NotNull ActivationHandler defaultActivationHandler) {
		initialize(defaultNumberOfNeurons, 0, defaultActivationHandler);
	}
	public NeuralLayer(@Positive int defaultNumberOfNeurons, @Positive int defaultNumberOfInputs) {
		initialize(defaultNumberOfNeurons, defaultNumberOfInputs, outputBeforeActivation -> 0.0);
	}
	public NeuralLayer(@Positive int defaultNumberOfNeurons) {
		initialize(defaultNumberOfNeurons, 0, outputBeforeActivation -> 0.0);
	}
	public NeuralLayer(@NotNull Collection<Neuron> neurons) {
		initializeLexicon(neurons.size());
		
		for (Neuron neuron : neurons) {
			if (neuron != null)
				getNeurons().add(neuron);
		}
	}
	public NeuralLayer(@NotNull Neuron... neurons) {
		this(Arrays.asList(neurons));
	}
	public NeuralLayer() {
		super();
		initialize(0, 0, outputBeforeActivation -> 0.0);
	}
	
	protected void initialize(int defaultNumberOfNeurons, @Positive int defaultNumberOfInputs, @NotNull ActivationHandler defaultActivationHandler) {
		if (defaultNumberOfNeurons < 0)
			defaultNumberOfNeurons = 0;
		
		initializeLexicon(defaultNumberOfNeurons);
		
		for (int i = 0; i < defaultNumberOfNeurons; i++) {
			Neuron neuron = new Neuron(defaultNumberOfInputs, defaultActivationHandler);
			try {
				getNeurons().set(i, neuron);
			} catch (IndexOutOfBoundsException ignored) {
				getNeurons().add(neuron);
			}
		}
	}
	
	protected void initializeLexicon(@Positive int defaultNumberOfNeurons) {
		setNeurons(new Lexicon<>(Neuron.class, defaultNumberOfNeurons));
		getNeurons().setAcceptNullValues(false);
		getNeurons().addObserver((observable, o) -> snap(o));
	}
	protected void initializeLexicon() {
		initializeLexicon(0);
	}
	
	/* NEURONAL LAYER METHODS */
	
	public ArrayList<Double> activate(@NotNull ArrayList<Double> inputsForNeurons) {
		ArrayList<Double> outputs = new ArrayList<>(getNeurons().size());
		
		for (Neuron neuron : getNeurons())
			outputs.add(neuron.activate(inputsForNeurons));
		
		return outputs;
	}
	public ArrayList<Double> activate() {
		ArrayList<Double> outputs = new ArrayList<>(getNeurons().size());
		
		for (Neuron neuron : getNeurons())
			outputs.add(neuron.activate());
		
		return outputs;
	}
	
	/* GETTER & SETTER */
	
	@NotNull
	public Lexicon<Neuron> getNeurons() {
		if (neurons == null)
			neurons = new Lexicon<>(Neuron.class);
		
		return neurons;
	}
	
	public void setNeurons(@NotNull Lexicon<Neuron> neurons) {
		if (neurons == null)
			throw new NullPointerException();
		
		this.neurons = neurons;
	}
}
