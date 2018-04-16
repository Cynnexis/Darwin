package fr.berger.darwin.connection;

import fr.berger.arrow.Ref;
import fr.berger.beyondcode.annotations.Positive;
import fr.berger.beyondcode.util.EnhancedObservable;
import fr.berger.beyondcode.util.Irregular;
import fr.berger.darwin.connection.handlers.ActivationHandler;
import fr.berger.enhancedlist.lexicon.Lexicon;
import fr.berger.enhancedlist.lexicon.LexiconBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Neuron extends EnhancedObservable implements Serializable, Cloneable {
	
	/* PROPERTIES */
	
	/**
	 * Unique identifier for the neuron
	 */
	@NotNull
	protected UUID id;
	
	/**
	 * List containing all the inputs of the neuron
	 */
	@NotNull
	protected Lexicon<Double> inputs;
	
	/**
	 * List containing all the weights of the inputs, with the bias
	 * (<c>inputs.size() + 1 == weights.size()</c>)
	 */
	@NotNull
	protected Lexicon<Double> weights;
	
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
	
	/**
	 * The synapses of the neurons: the destination of the information after the computation
	 */
	@NotNull
	private Lexicon<Ref<Neuron>> synapses;
	
	/* CONSTRUCTORS & INITIALIZING METHODS */
	
	public Neuron(@NotNull Lexicon<Double> inputs, @NotNull Lexicon<Double> weights, @Positive double bias, @NotNull ActivationHandler activationHandler, @NotNull Lexicon<Ref<Neuron>> synapses) {
		super();
		initialize(inputs, weights, bias, activationHandler, synapses);
	}
	@SuppressWarnings("unchecked")
	public Neuron(@NotNull Lexicon<Double> inputs, @NotNull Lexicon<Double> weights, @Positive double bias, @NotNull ActivationHandler activationHandler, @NotNull Ref<Neuron>... synapses) {
		super();
		initialize(inputs, weights, bias, activationHandler, new Lexicon<>(synapses));
	}
	public Neuron(@NotNull Lexicon<Double> inputs, @NotNull Lexicon<Double> weights, @Positive double bias, @NotNull ActivationHandler activationHandler) {
		super();
		initialize(inputs, weights, bias, activationHandler, null);
	}
	public Neuron(@NotNull ActivationHandler activationHandler, @NotNull Lexicon<Ref<Neuron>> synapses) {
		super();
		initialize(null, null, 1.0, activationHandler, synapses);
	}
	@SuppressWarnings("unchecked")
	public Neuron(@NotNull ActivationHandler activationHandler, @Nullable Ref<Neuron>... synapses) {
		super();
		initialize(null, null, 1.0, activationHandler, new Lexicon<>(synapses));
	}
	public Neuron(@NotNull ActivationHandler activationHandler) {
		super();
		initialize(null, null, 1.0, activationHandler, null);
	}
	@SuppressWarnings("ConstantConditions")
	public Neuron(@NotNull Neuron neuron) {
		super();
		
		if (neuron == null)
			throw new NullPointerException();
		
		setId(neuron.getId());
		setInputs(neuron.getInputs());
		setWeights(neuron.getWeights());
		setBias(neuron.getBias());
		setActivationHandler(neuron.getActivationHandler());
		setSynapses(neuron.getSynapses());
	}
	public Neuron() {
		super();
		initialize(null, null, 1.0, (input -> 0.0d), null);
	}
	
	protected void initialize(@Nullable Lexicon<Double> inputs, @Nullable Lexicon<Double> weights, double bias, @Nullable ActivationHandler activationHandler, Lexicon<Ref<Neuron>> synapses) {
		// Setting the identifier
		initId();
		
		// Settings the inputs list
		if (inputs == null)
			initInputs();
		else if (inputs.isEmpty())
			initInputs();
		else
			setInputs(inputs);
		
		// Setting the weights list
		if (weights != null)
			setWeights(weights);
		else //noinspection ConstantConditions
			if (weights == null && !getInputs().isEmpty())
			generateRandomWeights();
		
		// Setting the bias
		if (bias < 0)
			initBias();
		else
			setBias(bias);
		
		// Setting the activation handler
		if (activationHandler == null)
			initActivationHandler();
		else
			setActivationHandler(activationHandler);
		
		if (synapses == null)
			initSynapses();
		else
			setSynapses(synapses);
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
	
	@SuppressWarnings("ConstantConditions")
	public double sum(@NotNull Lexicon<Double> inputs, @Positive double bias) {
		// Check the inputs argument
		if (inputs == null)
			throw new NullPointerException("The argument cannot be null.");
		
		// Check the bias argument
		if (bias < 0)
			throw new IllegalArgumentException("The bias must be greater or equals to 0.");
		
		// Check if the number of inputs is valid according to the weights
		if (inputs.size() + 1 != getWeights().size())
			throw new IllegalArgumentException("The number of inputs must be equals to the number of weights minus one. (number of inputs: \"" + getInputs().size() + "\" ; number of weights: \"" + getWeights().size() + "\" ; number of expected inputs: \"" + (getWeights().size() - 1) + "\").");
		
		// Check the inputs arguments
		inputs.deleteNullElement();
		
		// Compute the sum of all inputs according to their weight
		double outputBeforeActivation = 0.0;
		
		for (int i = 0; i < inputs.size(); i++)
			outputBeforeActivation += inputs.get(i) * getWeights().get(i);
		
		// Add the bias (and its weight)
		outputBeforeActivation += bias * getWeights().get(getWeights().size() - 1);
		
		return outputBeforeActivation;
	}
	public double sum(@NotNull Lexicon<Double> inputs) {
		return sum(inputs, getBias());
	}
	public double sum(@Positive double bias) {
		return sum(getInputs(), bias);
	}
	public double sum() {
		return sum(getInputs(), getBias());
	}
	
	public double activate(@NotNull Lexicon<Double> inputs, @Positive double bias) {
		return getActivationHandler().activate(sum(inputs, bias));
	}
	public double activate(@NotNull Lexicon<Double> inputs) {
		return activate(inputs, getBias());
	}
	public double activate(@Positive double bias) {
		return activate(getInputs(), bias);
	}
	public double activate() {
		return activate(getInputs(), getBias());
	}
	
	/* GETTERS & SETTERS */
	
	@SuppressWarnings("ConstantConditions")
	@NotNull
	public UUID getId() {
		if (id == null)
			initId();
		
		return id;
	}
	
	@SuppressWarnings("ConstantConditions")
	public void setId(@NotNull UUID id) {
		if (id == null)
			throw new NullPointerException();
		
		this.id = id;
		snap(this.id);
	}
	
	protected void initId() {
		setId(UUID.randomUUID());
	}
	
	@SuppressWarnings("ConstantConditions")
	@NotNull
	public Lexicon<Double> getInputs() {
		if (inputs == null)
			initInputs();
		
		return inputs;
	}
	
	@SuppressWarnings("ConstantConditions")
	public void setInputs(@NotNull Lexicon<Double> inputs) {
		if (inputs == null)
			throw new NullPointerException();
		
		this.inputs = inputs;
		configureInputs();
	}
	public void setInputs(double... inputs) {
		setInputs(new LexiconBuilder<>(Double.class)
				.addAll(Arrays.stream(inputs).boxed().toArray(Double[]::new))
				.createLexicon());
	}
	
	protected void initInputs() {
		setInputs(new Lexicon<>(Double.class));
	}
	
	protected void configureInputs() {
		getInputs().setAcceptNullValues(false);
		//getWeights().setSynchronizedAccess(false);
		snap(getInputs());
	}
	
	@SuppressWarnings("ConstantConditions")
	public Lexicon<Double> getWeights() {
		if (weights == null)
			initWeights();
		
		return weights;
	}
	
	@SuppressWarnings("ConstantConditions")
	public void setWeights(@NotNull Lexicon<Double> weights) {
		if (weights == null)
			throw new NullPointerException();
		
		this.weights = weights;
		configureWeights();
	}
	public void setWeights(double... weights) {
		setWeights(new LexiconBuilder<>(Double.class)
				.addAll(Arrays.stream(weights).boxed().toArray(Double[]::new))
				.createLexicon());
	}
	
	protected void initWeights() {
		setWeights(new Lexicon<>(Double.class));
	}
	
	protected void configureWeights() {
		getWeights().setAcceptNullValues(false);
		//getWeights().setSynchronizedAccess(false);
		snap(getWeights());
	}
	
	@Positive
	public double getBias() {
		if (bias < 0.0)
			initBias();
		
		return bias;
	}

	public void setBias(@Positive double bias) {
		if (bias < 0.0)
			throw new IllegalArgumentException("bias must be greater than 0.");
		
		this.bias = bias;
		snap(this.bias);
	}
	
	protected void initBias() {
		setBias(1.0);
	}
	
	@SuppressWarnings("ConstantConditions")
	@NotNull
	public ActivationHandler getActivationHandler() {
		if (activationHandler == null)
			initActivationHandler();
		
		return activationHandler;
	}
	
	@SuppressWarnings("ConstantConditions")
	public void setActivationHandler(@NotNull ActivationHandler activationHandler) {
		if (activationHandler == null)
			throw new NullPointerException();
		
		this.activationHandler = activationHandler;
		snap(this.activationHandler);
	}
	
	protected void initActivationHandler() {
		setActivationHandler(outputBeforeActivation -> 0.0);
	}
	
	@SuppressWarnings("ConstantConditions")
	@NotNull
	public Lexicon<Ref<Neuron>> getSynapses() {
		if (synapses == null)
			initSynapses();
		
		return synapses;
	}
	
	@SuppressWarnings("ConstantConditions")
	public void setSynapses(@NotNull Lexicon<Ref<Neuron>> synapses) {
		if (synapses == null)
			throw new NullPointerException();
		
		this.synapses = synapses;
		configureWeights();
	}
	@SuppressWarnings("unchecked")
	public void setSynapses(@NotNull Ref<Neuron>... synapses) {
		setSynapses(new Lexicon<>(synapses));
	}
	
	protected void initSynapses() {
		setSynapses(new Lexicon<>());
	}
	
	protected void configureSynapses() {
		getSynapses().setAcceptDuplicates(true);
		getSynapses().setAcceptNullValues(false);
		snap(getSynapses());
	}
	
	/* SERIALIZATION METHODS */
	
	private void writeObject(@NotNull ObjectOutputStream stream) throws IOException {
		stream.writeObject(getId());
		stream.writeObject(getInputs());
		stream.writeObject(getWeights());
		stream.writeDouble(getBias());
		stream.writeObject(getActivationHandler());
		stream.writeObject(getSynapses());
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(@NotNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
		setId((UUID) stream.readObject());
		setInputs((Lexicon<Double>) stream.readObject());
		setWeights((Lexicon<Double>) stream.readObject());
		setBias(stream.readDouble());
		setActivationHandler((ActivationHandler) stream.readObject());
		setSynapses((Lexicon<Ref<Neuron>>) stream.readObject());
	}
	
	/* OVERRIDES */
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Neuron)) return false;
		Neuron neuron = (Neuron) o;
		return Double.compare(neuron.getBias(), getBias()) == 0 &&
				Objects.equals(getId(), neuron.getId()) &&
				Objects.equals(getInputs(), neuron.getInputs()) &&
				Objects.equals(getWeights(), neuron.getWeights()) &&
				Objects.equals(getActivationHandler(), neuron.getActivationHandler()) &&
				Objects.equals(getSynapses(), neuron.getSynapses());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getId(), getInputs(), getWeights(), getBias(), getActivationHandler(), getSynapses());
	}
	
	@Override
	public String toString() {
		return "Neuron{" +
				"id=" + id +
				", inputs=" + inputs +
				", weights=" + weights +
				", bias=" + bias +
				", activationHandler=" + activationHandler +
				", synapses=" + synapses +
				'}';
	}
}
