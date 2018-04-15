package fr.berger.darwin.connection.neurallayers;

import fr.berger.arrow.Ref;
import fr.berger.darwin.connection.Neuron;
import fr.berger.enhancedlist.Couple;
import fr.berger.enhancedlist.lexicon.Lexicon;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class InputLayerBuilder {
	
	@NotNull
	private InputLayer inputLayer;
	
	@NotNull Lexicon<Double> inputs;
	@NotNull Lexicon<Ref<Neuron>> neurons;
	
	@SuppressWarnings("ConstantConditions")
	public InputLayerBuilder(@NotNull InputLayer inputLayer) {
		if (inputLayer == null)
			throw new NullPointerException();
		
		this.inputLayer = inputLayer;
		this.inputs = new Lexicon<>(Double.class);
		this.neurons = new Lexicon<>();
	}
	public InputLayerBuilder() {
		this(new InputLayer());
	}
	
	@NotNull
	public InputLayerBuilder addDendrite(@NotNull Couple<Double, Ref<Neuron>> dendrite) {
		inputLayer.getDendrites().add(dendrite);
		return this;
	}
	@NotNull
	public InputLayerBuilder addDendrite(double input, @NotNull Ref<Neuron> neuron) {
		return addDendrite(new Couple<>(input, neuron));
	}
	
	@NotNull
	public InputLayerBuilder addInputs(@NotNull Lexicon<Double> inputs) {
		this.inputs.addAll(inputs);
		return this;
	}
	@NotNull
	public InputLayerBuilder addInputs(@NotNull Collection<Double> inputs) {
		this.inputs.addAll(inputs);
		return this;
	}
	@NotNull
	public InputLayerBuilder addInputs(@NotNull double... inputs) {
		this.inputs.addAll(Arrays.stream(inputs).boxed().toArray(Double[]::new));
		return this;
	}
	
	@NotNull
	public InputLayerBuilder setInputs(@NotNull Lexicon<Double> inputs) {
		this.inputs.clear();
		return addInputs(inputs);
	}
	@NotNull
	public InputLayerBuilder setInputs(@NotNull Collection<Double> inputs) {
		this.inputs.clear();
		return addInputs(inputs);
	}
	@NotNull
	public InputLayerBuilder setInputs(@NotNull double... inputs) {
		this.inputs.clear();
		return addInputs(inputs);
	}
	
	@NotNull
	public InputLayerBuilder addNeurons(@NotNull Lexicon<Ref<Neuron>> neurons) {
		this.neurons.addAll(neurons);
		return this;
	}
	@NotNull
	public InputLayerBuilder addNeurons(@NotNull Collection<Ref<Neuron>> neurons) {
		this.neurons.addAll(neurons);
		return this;
	}
	@SuppressWarnings("unchecked")
	@NotNull
	public InputLayerBuilder addNeurons(@NotNull Ref<Neuron>... neurons) {
		this.neurons.addAll(neurons);
		return this;
	}
	
	@NotNull
	public InputLayerBuilder setNeurons(@NotNull Lexicon<Ref<Neuron>> neurons) {
		this.neurons.clear();
		return addNeurons(neurons);
	}
	@NotNull
	public InputLayerBuilder setNeurons(@NotNull Collection<Ref<Neuron>> neurons) {
		this.neurons.clear();
		return addNeurons(neurons);
	}
	@SuppressWarnings("unchecked")
	@NotNull
	public InputLayerBuilder setNeurons(@NotNull Ref<Neuron>... neurons) {
		this.neurons.clear();
		return addNeurons(neurons);
	}
	
	@NotNull
	public InputLayer createInputLayer() {
		if (!inputs.isEmpty() && !neurons.isEmpty()) {
			if (inputs.size() != neurons.size())
				throw new IllegalStateException("inputs.size() != neurons.size()");
			
			Lexicon<Couple<Double, Ref<Neuron>>> mix = new Lexicon<>();
			for (int i = 0, maxi = inputs.size(); i < maxi; i++)
				mix.add(new Couple<>(inputs.get(i), neurons.get(i)));
			
			inputLayer.setDendrites(mix);
		}
		
		return new InputLayer(inputLayer);
	}
}
