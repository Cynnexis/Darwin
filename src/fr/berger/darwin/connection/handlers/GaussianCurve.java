package fr.berger.darwin.connection.handlers;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

public class GaussianCurve implements ActivationHandler, Serializable, Cloneable {
	
	/* PROPERTY */
	
	private double mean;
	private double variance;
	
	/* CONSTRUCTORS */
	
	public GaussianCurve(double mean, double variance) {
		setMean(mean);
		setVariance(variance);
	}
	public GaussianCurve() {
		this(1.0, 0.0);
	}
	
	/* SIGMOID METHOD */
	
	@Override
	public double activate(double x) {
		return (1.0/(Math.sqrt(2.0 * Math.PI * variance))) * Math.exp(- (Math.pow(x - getMean(), 2.0)/(2.0 * getVariance())));
	}
	
	/* GETTERS & SETTERS */
	
	public double getMean() {
		return mean;
	}
	
	public void setMean(double mean) {
		this.mean = mean;
	}
	
	public double getVariance() {
		return variance;
	}
	
	public void setVariance(double variance) {
		this.variance = variance;
	}
	
	public double getStandardDeviation() {
		return Math.sqrt(getVariance());
	}
	
	public void setStandardDeviation(double standardDeviation) {
		setVariance(Math.pow(standardDeviation, 2.0));
	}
	
	/* SERIALIZATION METHODS */
	
	private void writeObject(@NotNull ObjectOutputStream stream) throws IOException {
		stream.writeDouble(getMean());
		stream.writeDouble(getVariance());
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(@NotNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
		setMean(stream.readDouble());
		setVariance(stream.readDouble());
	}
	
	/* OVERRIDES */
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GaussianCurve)) return false;
		GaussianCurve that = (GaussianCurve) o;
		return Double.compare(that.getMean(), getMean()) == 0 &&
				Double.compare(that.getVariance(), getVariance()) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getMean(), getVariance());
	}
	
	@Override
	public String toString() {
		return "GaussianCurve{" +
				"mean=" + mean +
				", variance=" + variance +
				'}';
	}
}
