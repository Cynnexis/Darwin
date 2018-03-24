package fr.berger.darwin.connection.neurallayers;

import fr.berger.beyondcode.annotations.Positive;
import fr.berger.darwin.connection.Neuron;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class InputLayer extends NeuralLayer {
	
	@NotNull
	private ArrayList<Double> inputs;
	
	public InputLayer(@Positive int defaultNumberOfInputs) {
		super(0, defaultNumberOfInputs);
	}
	public InputLayer(@NotNull ArrayList<Double> inputs) {
		super(0, inputs.size());
		setInputs(inputs);
	}
	public InputLayer() {
		super();
	}
	
	/* NEURONAL LAYER METHODS */
	
	@Override
	public ArrayList<Double> activate(@NotNull ArrayList<Double> inputsForNeurons) {
		if (getNeurons().size() != 0)
			return super.activate(inputsForNeurons);
		else
			return getInputs();
	}
	@Override
	public ArrayList<Double> activate() {
		if (getNeurons().size() != 0)
			return super.activate();
		else
			return getInputs();
	}
	
	/* GETTERS & SETTERS */
	
	@NotNull
	public ArrayList<Double> getInputs() {
		if (inputs == null)
			inputs = new ArrayList<>();
		
		return inputs;
	}
	
	public void setInputs(@NotNull ArrayList<Double> inputs) {
		if (inputs == null)
			throw new NullPointerException();
		this.inputs = inputs;
		snap(this.inputs);
	}
}
