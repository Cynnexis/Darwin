package fr.berger.darwin.connection;

import fr.berger.beyondcode.annotations.Positive;
import fr.berger.beyondcode.util.EnhancedObservable;
import fr.berger.beyondcode.util.Irregular;
import fr.berger.darwin.connection.handlers.ActivationHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Neuron extends EnhancedObservable implements Serializable, Cloneable {
	
	/* PROPERTIES */
	
	/**
	 * List containing all the inputs of the neuron
	 */
	@NotNull
	protected ArrayList<Double> inputs;
	
	/**
	 * List containing all the weights of the inputs, with the bias
	 * (<c>inputs.size() + 1 == weights.size()</c>)
	 */
	@NotNull
	protected ArrayList<Double> weights;
	
	/**
	 * The default bias. Its weight is <c>weights.get(weights.size() - 1)</c>
	 */
	@Positive
	protected double bias;

	/**
	 * The activation handler, called when the neuron is activated
	 */
	@NotNull
	protected ActivationHandler activationHandler;
	
	/* CONSTRUCTORS & INITIALIZING METHODS */
	
	public Neuron(@Positive int numberOfInputs, @NotNull ArrayList<Double> inputs, @NotNull ArrayList<Double> weights, @Positive double bias, @NotNull ActivationHandler activationHandler) {
		super();
		initialize(numberOfInputs, inputs, weights, bias, activationHandler);
	}
	public Neuron(@NotNull ArrayList<Double> inputs, @NotNull ArrayList<Double> weights, @Positive double bias, @NotNull ActivationHandler activationHandler) {
		super();
		if (inputs == null)
			throw new NullPointerException();
		initialize(inputs.size(), inputs, weights, bias, activationHandler);
	}
	public Neuron(@Positive int numberOfInputs, @NotNull ActivationHandler activationHandler) {
		super();
		initialize(numberOfInputs, null, null, 1.0, activationHandler);
	}
	public Neuron() {
		super();
		initialize(0, null, null, 1.0, (input -> 0.0d));
	}
	
	protected void initialize(int numberOfInputs, @Nullable ArrayList<Double> inputs, @Nullable ArrayList<Double> weights, double bias, @Nullable ActivationHandler activationHandler) {
		// Setting the number of inputs
		if (numberOfInputs < 0)
			numberOfInputs = 0;
		
		// Settings the inputs list
		if (inputs == null)
			setInputs(new ArrayList<>(numberOfInputs));
		else if (inputs.isEmpty())
			setInputs(new ArrayList<>(numberOfInputs));
		else
			setInputs(inputs);
		
		// Setting the weights list
		if (weights == null)
			generateRandomWeights(numberOfInputs);
		else if (weights.isEmpty())
			generateRandomWeights(numberOfInputs);
		else
			setWeights(weights);
		
		// Setting the bias
		if (bias < 0)
			setBias(1.0);
		else
			setBias(bias);
		
		// Setting the activation handler
		if (activationHandler == null)
			setActivationHandler(outputBeforeActivation -> 0.0d);
		else
			setActivationHandler(activationHandler);
	}
	
	public void generateRandomWeights(@Positive int numberOfInputs) {
		int numberOfWeights = numberOfInputs + 1;
		
		for (int i = 0; i < numberOfWeights; i++) {
			double newWeight = Irregular.rangeDouble(0.0, true, 1.0, true);
			
			try {
				getWeights().set(i, newWeight);
			} catch (IndexOutOfBoundsException ignored) {
				getWeights().add(newWeight);
			}
		}
	}
	public void generateRandomWeights() {
		generateRandomWeights(getInputs().size());
	}
	
	/* NEURON METHODS */
	
	public double activate(@NotNull ArrayList<Double> inputs, @Positive double bias) {
		// Check the inputs argument
		if (inputs == null)
			throw new NullPointerException("The argument cannot be null.");
		
		// Check the bias argument
		if (bias < 0)
			throw new IllegalArgumentException("The bias must be greater or equals to 0.");
		
		// Check if the number of inputs is valid according to the weights
		if (inputs.size() + 1 != getWeights().size())
			throw new IllegalArgumentException("The number of inputs must be equals of the number of weights minus one. (number of inputs: \"" + getInputs().size() + "\" ; number of weights: \"" + getWeights() + "\" ; number of expected inputs: \"" + (getWeights().size() - 1) + "\").");
		
		// Check the inputs arguments
		for (Double input : inputs) {
			if (input == null)
				throw new NullPointerException("One of the input is invalid.");
			else if (input < 0)
				throw new IllegalArgumentException("One of the argument is less than zero.");
		}
		
		// Compute the sum of all inputs according to their weight
		double outputBeforeActivation = 0.0d;
		
		for (int i = 0; i < inputs.size(); i++) {
			outputBeforeActivation += inputs.get(i) * getWeights().get(i);
		}
		
		// Add the bias (and its weight)
		outputBeforeActivation += bias * getWeights().get(getWeights().size() - 1);
		
		// Finally, call the activation handler
		return getActivationHandler().activate(outputBeforeActivation);
	}
	public double activate(@NotNull ArrayList<Double> inputs) {
		return activate(inputs, getBias());
	}
	public double activate() {
		return activate(getInputs(), getBias());
	}
	
	/* GETTERS & SETTERS */

	@NotNull
	public ArrayList<Double> getInputs() {
		if (inputs == null) {
			inputs = new ArrayList<>();
			snap(inputs);
		}
		
		return inputs;
	}

	public void setInputs(@NotNull ArrayList<Double> inputs) {
		if (inputs == null)
			throw new NullPointerException();
		
		this.inputs = inputs;
		snap(this.inputs);
	}

	public ArrayList<Double> getWeights() {
		if (weights == null) {
			weights = new ArrayList<>();
			snap(weights);
		}
		
		return weights;
	}

	public void setWeights(@NotNull ArrayList<Double> weights) {
		if (weights == null)
			throw new NullPointerException();
		
		this.weights = weights;
		snap(this.weights);
	}
	
	@Positive
	public double getBias() {
		if (bias < 0d) {
			bias = 0d;
			snap(bias);
		}
		
		return bias;
	}

	public void setBias(@Positive double bias) {
		if (bias < 0d)
			throw new IllegalArgumentException("bias must be greater than 0.");
		
		this.bias = bias;
		snap(this.bias);
	}

	@NotNull
	public ActivationHandler getActivationHandler() {
		if (activationHandler == null) {
			activationHandler = outputBeforeActivation -> 0.0d;
			snap(activationHandler);
		}
		
		return activationHandler;
	}

	public void setActivationHandler(@NotNull ActivationHandler activationHandler) {
		if (activationHandler == null)
			throw new NullPointerException();
		
		this.activationHandler = activationHandler;
		snap(this.activationHandler);
	}

	/* SERIALIZATION METHODS */
	
	private void writeObject(@NotNull ObjectOutputStream stream) throws IOException {
		stream.writeObject(getInputs());
		stream.writeObject(getWeights());
		stream.writeDouble(getBias());
		stream.writeObject(getActivationHandler());
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(@NotNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
		setInputs((ArrayList<Double>) stream.readObject());
		setWeights((ArrayList<Double>) stream.readObject());
		setBias(stream.readDouble());
		setActivationHandler((ActivationHandler) stream.readObject());
	}
	
	/* OVERRIDES */
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Neuron)) return false;
		Neuron neuron = (Neuron) o;
		return Double.compare(neuron.getBias(), getBias()) == 0 &&
				Objects.equals(getInputs(), neuron.getInputs()) &&
				Objects.equals(getWeights(), neuron.getWeights()) &&
				Objects.equals(getActivationHandler(), neuron.getActivationHandler());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getInputs(), getWeights(), getBias(), getActivationHandler());
	}
	
	@Override
	public String toString() {
		return "Neuron{" +
				"inputs=" + inputs +
				", weights=" + weights +
				", bias=" + bias +
				", activationHandler=" + activationHandler +
				'}';
	}
}
