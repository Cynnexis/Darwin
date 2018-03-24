package fr.berger.darwin.connection.neurallayers;

import fr.berger.beyondcode.annotations.Positive;
import fr.berger.darwin.connection.handlers.ActivationHandler;
import org.jetbrains.annotations.NotNull;

public class HiddenLayer extends NeuralLayer {
	
	public HiddenLayer(@Positive int defaultNumberOfNeurons, @Positive int defaultNumberOfInputs, @NotNull ActivationHandler defaultActivationHandler) {
		super(defaultNumberOfNeurons, defaultNumberOfInputs, defaultActivationHandler);
	}
	public HiddenLayer() {
		super();
	}
}
