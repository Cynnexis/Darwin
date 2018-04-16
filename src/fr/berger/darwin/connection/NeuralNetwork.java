package fr.berger.darwin.connection;

import fr.berger.arrow.Ref;
import fr.berger.beyondcode.util.EnhancedObservable;
import fr.berger.darwin.connection.neurallayers.HiddenLayer;
import fr.berger.darwin.connection.neurallayers.InputLayer;
import fr.berger.darwin.connection.neurallayers.NeuralLayer;
import fr.berger.darwin.connection.neurallayers.OutputLayer;
import fr.berger.enhancedlist.Couple;
import fr.berger.enhancedlist.ListUtil;
import fr.berger.enhancedlist.lexicon.Lexicon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;

public class NeuralNetwork extends EnhancedObservable implements Serializable, Cloneable {
	
	/* PROPERTIES */
	
	@NotNull
	private InputLayer inputLayer;
	@NotNull
	private Lexicon<HiddenLayer> hiddenLayers;
	@NotNull
	private OutputLayer outputLayer;
	
	/* CONSTRUCTORS & INITIALIZING METHODS */
	
	public NeuralNetwork(@NotNull InputLayer inputLayer, @NotNull OutputLayer outputLayer, @Nullable Lexicon<HiddenLayer> hiddenLayers) {
		setInputLayer(inputLayer);
		setHiddenLayers(hiddenLayers);
		setOutputLayer(outputLayer);
	}
	public NeuralNetwork(@NotNull InputLayer inputLayer, @NotNull OutputLayer outputLayer, @Nullable Collection<HiddenLayer> hiddenLayers) {
		setInputLayer(inputLayer);
		setHiddenLayers(new Lexicon<>(hiddenLayers));
		setOutputLayer(outputLayer);
	}
	public NeuralNetwork(@NotNull InputLayer inputLayer, @NotNull OutputLayer outputLayer, @Nullable HiddenLayer... hiddenLayers) {
		setInputLayer(inputLayer);
		setHiddenLayers(new Lexicon<>(hiddenLayers));
		setOutputLayer(outputLayer);
	}
	@SuppressWarnings("ConstantConditions")
	public NeuralNetwork(@NotNull NeuralNetwork neuralNetwork) {
		if (neuralNetwork == null)
			throw new NullPointerException();
		
		setInputLayer(neuralNetwork.getInputLayer());
		setHiddenLayers(neuralNetwork.getHiddenLayers());
		setOutputLayer(neuralNetwork.getOutputLayer());
	}
	public NeuralNetwork() {
		initInputLayer();
		initHiddenLayers();
		initOutputLayer();
	}
	
	/* NEURAL NETWORK METHODS */
	
	@NotNull
	public Lexicon<Neuron> getAllNeurons() {
		int nbNeurons = getInputLayer().getNeurons().size() + getOutputLayer().getNeurons().size();
		
		for (HiddenLayer hiddenLayer : getHiddenLayers())
			nbNeurons += hiddenLayer.getNeurons().size();
		
		Lexicon<Neuron> neurons = new Lexicon<>(Neuron.class, nbNeurons);
		
		neurons.addAll(getInputLayer().getNeurons());
		neurons.addAll(getOutputLayer().getNeurons());
		
		for (HiddenLayer hiddenLayer : getHiddenLayers())
			neurons.addAll(hiddenLayer.getNeurons());
		
		return neurons;
	}
	
	public Lexicon<Double> activate() {
		Lexicon<Couple<Double, Ref<Neuron>>> synapses;
		
		NeuralLayer currentLayer = getInputLayer();
		
		NeuralLayer nextLayer = null;
		int indexHiddenLayer = 0;
		
		while (!Objects.equals(nextLayer, getOutputLayer())) {
			if (!getHiddenLayers().isEmpty() && ListUtil.checkIndex(indexHiddenLayer, getHiddenLayers()))
				nextLayer = getHiddenLayers().get(indexHiddenLayer);
			else
				nextLayer = getOutputLayer();
			
			if (nextLayer == null)
				throw new NullPointerException();
			
			synapses = currentLayer.activate();
			
			// Feed the next neurons
			for (Couple<Double, Ref<Neuron>> synapse : synapses)
				if (synapse.getY() != null && synapse.getY().getElement() != null && nextLayer.getNeurons().contains(synapse.getY().getElement()))
					synapse.getY().getElement().getInputs().add(synapse.getX());
			
			// Activate the next layer
			currentLayer = nextLayer;
			indexHiddenLayer++;
		}
		
		// Now, currentLayer == getOutputLayer()
		synapses = currentLayer.activate();
		
		Lexicon<Double> output = new Lexicon<>(Double.class);
		
		for (Couple<Double, Ref<Neuron>> synapse : synapses)
			output.add(synapse.getX());
		
		return output;
	}
	
	/* GETTERS & SETTERS */
	
	@SuppressWarnings("ConstantConditions")
	@NotNull
	public InputLayer getInputLayer() {
		if (inputLayer == null)
			initInputLayer();
		
		return inputLayer;
	}
	
	@SuppressWarnings("ConstantConditions")
	public void setInputLayer(@NotNull InputLayer inputLayer) {
		if (inputLayer == null)
			throw new NullPointerException();
		
		this.inputLayer = inputLayer;
		snap(this.inputLayer);
	}
	
	protected void initInputLayer() {
		setInputLayer(new InputLayer());
	}
	
	@SuppressWarnings("ConstantConditions")
	@NotNull
	public Lexicon<HiddenLayer> getHiddenLayers() {
		if (hiddenLayers == null)
			initHiddenLayers();
		
		return hiddenLayers;
	}
	
	public void setHiddenLayers(@Nullable Lexicon<HiddenLayer> hiddenLayers) {
		if (hiddenLayers != null) {
			this.hiddenLayers = hiddenLayers;
			configureHiddenLayers();
		}
		else
			initInputLayer();
	}
	
	protected void initHiddenLayers() {
		setHiddenLayers(new Lexicon<>());
	}
	
	protected void configureHiddenLayers() {
		getHiddenLayers().setAcceptNullValues(false);
		snap(getHiddenLayers());
	}
	
	@SuppressWarnings("ConstantConditions")
	@NotNull
	public OutputLayer getOutputLayer() {
		if (outputLayer == null)
			initOutputLayer();
		
		return outputLayer;
	}
	
	@SuppressWarnings("ConstantConditions")
	public void setOutputLayer(@NotNull OutputLayer outputLayer) {
		if (outputLayer == null)
			throw new NullPointerException();
		
		this.outputLayer = outputLayer;
		snap(this.outputLayer);
	}
	
	protected void initOutputLayer() {
		setOutputLayer(new OutputLayer());
	}
	
	/* SERIALIZATION METHODS */
	
	private void writeObject(@NotNull ObjectOutputStream stream) throws IOException {
		stream.writeObject(getInputLayer());
		stream.writeObject(getHiddenLayers());
		stream.writeObject(getOutputLayer());
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(@NotNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
		setInputLayer((InputLayer) stream.readObject());
		setHiddenLayers((Lexicon<HiddenLayer>) stream.readObject());
		setOutputLayer((OutputLayer) stream.readObject());
	}
	
	/* OVERRIDES */
	
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (!(o instanceof NeuralNetwork)) return false;
		NeuralNetwork that = (NeuralNetwork) o;
		return Objects.equals(getInputLayer(), that.getInputLayer()) &&
				Objects.equals(getHiddenLayers(), that.getHiddenLayers()) &&
				Objects.equals(getOutputLayer(), that.getOutputLayer());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getInputLayer(), getHiddenLayers(), getOutputLayer());
	}
	
	@SuppressWarnings("ConstantConditions")
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("--- Neural Network ---\n");
		
		builder.append("Input Layer:\n");
		for (Neuron neuron : getInputLayer()) {
			if (neuron != null) {
				builder.append("\t")
						.append(neuron.toString())
						.append("\n");
			}
		}
		for (Couple<Double,Ref<Neuron>> synapse : getInputLayer().getDendrites()) {
			if (synapse != null && synapse.getX() != null && synapse.getY() != null) {
				builder.append("Synapse{")
						.append("value=")
						.append(synapse.getX())
						.append(" --> ")
						.append("Neuron{id=")
						.append(synapse.getY().getElement().getId())
						.append("}}")
						.append('\n');
			}
		}
		
		if (!getHiddenLayers().isEmpty()) {
			for (int i = 0, maxi = getHiddenLayers().size(); i < maxi; i++) {
				if (getHiddenLayers().get(i) != null) {
					// Draw line
					for (int j = 0; j < 10; j++)
						builder.append('-');
					
					builder.append('\n');
					
					builder.append("Hidden Layer number ")
							.append(i + 1)
							.append(':')
							.append('\n');
					
					for (Neuron neuron : getHiddenLayers().get(i)) {
						if (neuron != null) {
							builder.append("\t")
									.append(neuron.toString())
									.append("\n");
						}
					}
				}
			}
		}
		
		// Draw line
		for (int j = 0; j < 10; j++)
			builder.append('-');
		
		builder.append('\n');
		
		builder.append("Output Layer:\n");
		for (Neuron neuron : getOutputLayer()) {
			if (neuron != null) {
				builder.append("\t")
						.append(neuron.toString())
						.append("\n");
			}
		}
		
		return builder.toString();
	}
}
