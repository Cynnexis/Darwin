package fr.berger.darwin.connection;

import fr.berger.arrow.Ref;
import fr.berger.darwin.connection.handlers.Sigmoid;
import fr.berger.darwin.connection.neurallayers.HiddenLayer;
import fr.berger.darwin.connection.neurallayers.InputLayer;
import fr.berger.darwin.connection.neurallayers.InputLayerBuilder;
import fr.berger.darwin.connection.neurallayers.OutputLayer;

/**
 * The example follow the following image: http://imgur.com/IDFRq5a.png
 */
public class NeuralNetConsoleTest {
	
	public static void main(String[] args) {
		Sigmoid sig = new Sigmoid(1);
		
		Neuron nh1 = new NeuronBuilder()
				.setWeights(0.8, 0.2, 0)
				.setBias(1)
				.setActivationHandler(sig)
				.createNeuron();
		
		Neuron nh2 = new NeuronBuilder()
				.setWeights(0.4, 0.9)
				.setActivationHandler(sig)
				.createNeuron();
		
		Neuron nh3 = new NeuronBuilder()
				.setWeights(0.3, 0.5, 0)
				.setActivationHandler(sig)
				.createNeuron();
		
		Neuron no1 = new NeuronBuilder()
				.setWeights(0.3, 0.5, 0.9, 0)
				.setActivationHandler(sig)
				.createNeuron();
		
		Ref<Neuron> refNo1 = new Ref<>(no1);
		
		nh1.getSynapses().add(refNo1);
		nh2.getSynapses().add(refNo1);
		nh3.getSynapses().add(refNo1);
		
		InputLayer inputLayer = new InputLayerBuilder()
				.addDendrite(1, new Ref(nh1))
				.addDendrite(1, new Ref(nh1))
				.addDendrite(1, new Ref(nh2))
				.addDendrite(1, new Ref(nh2))
				.addDendrite(1, new Ref(nh3))
				.addDendrite(1, new Ref(nh3))
				.createInputLayer();
		
		NeuralNetwork net = new NeuralNetwork(inputLayer, new OutputLayer(no1), new HiddenLayer(nh1, nh2, nh3));
		net.fire();
	}
}
