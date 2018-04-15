package fr.berger.darwin.connection.neurallayers;

import fr.berger.arrow.Ref;
import fr.berger.beyondcode.annotations.Positive;
import fr.berger.darwin.connection.Neuron;
import fr.berger.darwin.connection.Triggerable;
import fr.berger.enhancedlist.Couple;
import fr.berger.enhancedlist.lexicon.Lexicon;
import fr.berger.enhancedlist.lexicon.eventhandlers.AddHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.invoke.LambdaConversionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class InputLayer extends NeuralLayer implements Triggerable, Serializable, Cloneable {
	
	@NotNull
	private Lexicon<Couple<Double, Ref<Neuron>>> dendrites;
	
	public InputLayer(@NotNull Lexicon<Couple<Double, Ref<Neuron>>> dendrites) {
		super();
		setDendrites(dendrites);
	}
	public InputLayer(@NotNull Collection<Couple<Double, Ref<Neuron>>> dendrites) {
		super();
		setDendrites(new Lexicon<>(dendrites));
	}
	@SuppressWarnings("unchecked")
	public InputLayer(@NotNull Couple<Double, Ref<Neuron>>... dendrites) {
		super();
		setDendrites(new Lexicon<>(dendrites));
	}
	@SuppressWarnings("ConstantConditions")
	public InputLayer(@NotNull InputLayer inputLayer) {
		super();
		
		if (inputLayer == null)
			throw new NullPointerException();
		
		setDendrites(inputLayer.getDendrites());
		setNeurons(inputLayer.getNeurons());
	}
	public InputLayer() {
		super();
		initDendrites();
	}
	
	/* NEURONAL LAYER METHODS */
	
	@Override
	public Lexicon<Double> activate(@NotNull Lexicon<Double> inputsForNeurons) {
		return activate_content(inputsForNeurons);
	}
	@Override
	public Lexicon<Double> activate(@NotNull double... inputsForNeurons) {
		return activate_content(new Lexicon<>(Arrays.stream(inputsForNeurons).boxed().toArray(Double[]::new)));
	}
	@Override
	public Lexicon<Double> activate() {
		return activate_content(null);
	}
	
	private Lexicon<Double> activate_content(@Nullable Lexicon<Double> inputsForNeurons) {
		if (getNeurons().size() != 0) {
			if (inputsForNeurons == null)
				return super.activate();
			else
				return super.activate(inputsForNeurons);
		}
		else {
			Lexicon<Double> inputs = new Lexicon<>(Double.class);
			
			for (Couple<Double,Ref<Neuron>> dendrite : getDendrites())
				inputs.add(dendrite.getX());
			
			return inputs;
		}
	}
	
	@Override
	public double fire() {
		double activationResult = 0;
		
		// Add the inputs for all neurons
		for (Couple<Double, Ref<Neuron>> dendrite : getDendrites()) {
			dendrite.getY().getElement().getInputs().add(dendrite.getX());
		}
		
		// Fire every neurons
		for (Couple<Double, Ref<Neuron>> dendrite : getDendrites()) {
			activationResult += dendrite.getY().getElement().fire();
		}
		
		return activationResult;
	}
	
	/* GETTERS & SETTERS */
	
	@SuppressWarnings("ConstantConditions")
	@NotNull
	public Lexicon<Couple<Double, Ref<Neuron>>> getDendrites() {
		if (dendrites == null)
			initDendrites();
		
		return dendrites;
	}
	
	@SuppressWarnings("ConstantConditions")
	public void setDendrites(@NotNull Lexicon<Couple<Double, Ref<Neuron>>> dendrites) {
		if (dendrites == null)
			throw new NullPointerException();
		
		this.dendrites = dendrites;
		configureDendrites();
	}
	
	protected void initDendrites() {
		setDendrites(new Lexicon<>());
	}
	
	protected void configureDendrites() {
		getDendrites().setAcceptNullValues(false);
		getDendrites().addAddHandler((index, element) -> {
			for (Couple<Double, Ref<Neuron>> doubleRefCouple : getDendrites()) {
				if (doubleRefCouple == null)
					throw new NullPointerException();
				
				if (doubleRefCouple.getX() == null || doubleRefCouple.getY() == null)
					throw new NullPointerException();
			}
		});
		getDendrites().addSetHandler(((index, element) -> getDendrites().getAddHandlers().get(0).onElementAdded(index, element)));
		snap(getDendrites());
	}
	
	/* SERIALIZATION METHODS */
	
	private void writeObject(@NotNull ObjectOutputStream stream) throws IOException {
		stream.writeObject(getDendrites());
		stream.writeObject(getNeurons());
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(@NotNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
		setDendrites((Lexicon<Couple<Double, Ref<Neuron>>>) stream.readObject());
		setNeurons((Lexicon<Neuron>) stream.readObject());
	}
	
	/* OVERRIDES */
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof InputLayer)) return false;
		if (!super.equals(o)) return false;
		InputLayer that = (InputLayer) o;
		return Objects.equals(getDendrites(), that.getDendrites());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), getDendrites());
	}
	
	@Override
	public String toString() {
		return "InputLayer{" +
				"dendrites=" + dendrites +
				", neurons=" + getNeurons() +
				'}';
	}
}
