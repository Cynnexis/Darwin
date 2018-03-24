package fr.berger.darwin.connection;

import fr.berger.beyondcode.annotations.Positive;
import fr.berger.darwin.connection.neurallayers.HiddenLayer;
import fr.berger.darwin.connection.neurallayers.InputLayer;
import fr.berger.darwin.connection.neurallayers.OutputLayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class NeuralNetwork implements Serializable, Cloneable {
	
	/* PROPERTIES */
	
	@NotNull
	private InputLayer inputLayer;
	@NotNull
	private ArrayList<HiddenLayer> hiddenLayers;
	@NotNull
	private OutputLayer outputLayer;
	
	/* CONSTRUCTORS & INITIALIZING METHODS */
	
	public NeuralNetwork(@Positive int defaultNumberOfInputs, @NotNull ArrayList<Integer>) {
		setInputLayer(new InputLayer(defaultNumberOfInputs));
		setHiddenLayers(new HiddenLayer());
	}
	
	/* NEURAL NETWORK METHODS */
	
	@NotNull
	public ArrayList<Neuron> getAllNeurons() {
		int numberOfNeurons = 0;
		numberOfNeurons += getInputLayer().getNeurons().size();
		for (HiddenLayer hiddenLayer : getHiddenLayers()) {
			numberOfNeurons += hiddenLayer.getNeurons().size();
		}
		numberOfNeurons += getOutputLayer().getNeurons().size();
		
		ArrayList<Neuron> neurons = new ArrayList<>(numberOfNeurons);
		return neurons;
	}
	
	/* GETTERS & SETTERS */
	
	@NotNull
	public InputLayer getInputLayer() {
		if (inputLayer == null)
			inputLayer = new InputLayer();
		
		return inputLayer;
	}
	
	public void setInputLayer(@NotNull InputLayer inputLayer) {
		if (inputLayer == null)
			throw new NullPointerException();
		
		this.inputLayer = inputLayer;
	}
	
	@NotNull
	public ArrayList<HiddenLayer> getHiddenLayers() {
		return hiddenLayers;
	}
	
	public void setHiddenLayers(@Nullable ArrayList<HiddenLayer> hiddenLayers) {
		if (hiddenLayers != null) {
			this.hiddenLayers = new ArrayList<>();
			for (HiddenLayer hiddenLayer : hiddenLayers)
				if (hiddenLayer != null)
					this.hiddenLayers.add(hiddenLayer);
		}
		else
			this.hiddenLayers = new ArrayList<>();
	}
	
	@NotNull
	public OutputLayer getOutputLayer() {
		if (outputLayer == null)
			outputLayer = new OutputLayer();
		
		return outputLayer;
	}
	
	public void setOutputLayer(@NotNull OutputLayer outputLayer) {
		if (outputLayer == null)
			throw new NullPointerException();
		
		this.outputLayer = outputLayer;
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
	
	@Override
	public String toString() {
		return "NeuralNetwork{" +
				"inputLayer=" + inputLayer +
				", hiddenLayers=" + hiddenLayers +
				", outputLayer=" + outputLayer +
				'}';
	}
}
