package fr.berger.darwin.connection;

import fr.berger.arrow.Ref;
import fr.berger.darwin.connection.handlers.ActivationHandler;
import fr.berger.enhancedlist.lexicon.Lexicon;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NeuronBuilder {
	
	@NotNull
	private Neuron neuron;
	
	@SuppressWarnings("ConstantConditions")
	public NeuronBuilder(@NotNull Neuron neuron) {
		if (neuron == null)
			throw new NullPointerException();
		
		this.neuron = neuron;
	}
	public NeuronBuilder() {
		this.neuron = new Neuron();
	}
	
	@NotNull
	public NeuronBuilder setId(@NotNull UUID id) {
		neuron.setId(id);
		return this;
	}
	
	@NotNull
	public NeuronBuilder setInputs(@NotNull Lexicon<Double> inputs) {
		neuron.setInputs(inputs);
		return this;
	}
	@NotNull
	public NeuronBuilder setInputs(@NotNull double... inputs) {
		neuron.setInputs(inputs);
		return this;
	}
	
	@NotNull
	public NeuronBuilder setWeights(@NotNull Lexicon<Double> weights) {
		neuron.setWeights(weights);
		return this;
	}
	@NotNull
	public NeuronBuilder setWeights(@NotNull double... weights) {
		neuron.setWeights(weights);
		return this;
	}
	
	@NotNull
	public NeuronBuilder setBias(@NotNull double bias) {
		neuron.setBias(bias);
		return this;
	}
	
	@NotNull
	public NeuronBuilder setActivationHandler(@NotNull ActivationHandler activationHandler) {
		neuron.setActivationHandler(activationHandler);
		return this;
	}
	
	@NotNull
	public NeuronBuilder setSynapses(@NotNull Lexicon<Ref<Neuron>> synapses) {
		neuron.setSynapses(synapses);
		return this;
	}
	@SuppressWarnings("unchecked")
	@NotNull
	public NeuronBuilder setSynapses(@NotNull Ref<Neuron>... synapses) {
		neuron.setSynapses(synapses);
		return this;
	}
	
	@NotNull
	public Neuron createNeuron() {
		return new Neuron(neuron);
	}
}
