package fr.berger.darwin.connection;

import fr.berger.darwin.connection.handlers.Sigmoid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NeuronTest {
	
	Neuron neuron;
	
	@BeforeEach
	void setup() {
		neuron = new Neuron();
		neuron.setInputs(1.0, 2.5, 0.7);
		neuron.generateRandomWeights();
		neuron.setActivationHandler(new Sigmoid(1.0));
	}
	
	@Test
	void sumAndActivate() {
		System.out.println("NeuronTest.activate> " + neuron.toString());
		double sum = neuron.sum();
		
		double computedSum = 0;
		for (int i = 0; i < neuron.getInputs().size(); i++)
			computedSum += neuron.getInputs().get(i) * neuron.getWeights().get(i);
		
		computedSum += neuron.getBias() * neuron.getWeights().get(neuron.getWeights().size() - 1);
		System.out.println("NeuronTest.activate> Inputs and Weights Sum: " + sum + " (computed sum: " + computedSum + ").");
		Assertions.assertEquals(computedSum, sum);
		
		System.out.println("NeuronTest.activate> Activation: " + neuron.activate());
	}
}