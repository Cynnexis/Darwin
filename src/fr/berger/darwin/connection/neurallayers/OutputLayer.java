package fr.berger.darwin.connection.neurallayers;

import fr.berger.beyondcode.annotations.Positive;
import fr.berger.darwin.connection.handlers.ActivationHandler;
import org.jetbrains.annotations.NotNull;

public class OutputLayer extends NeuralLayer {
	
	public OutputLayer(@Positive int defaultNumberOfNeurons, @Positive int defaultNumberOfInputs, @NotNull ActivationHandler defaultActivationHandler) {
		super(defaultNumberOfNeurons, defaultNumberOfInputs, defaultActivationHandler);
	}
	public OutputLayer() {
		super();
	}
}
