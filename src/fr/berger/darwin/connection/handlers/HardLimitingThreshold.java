package fr.berger.darwin.connection.handlers;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

public class HardLimitingThreshold implements ActivationHandler, Serializable, Cloneable {
	
	/* PROPERTY */
	
	private double threshold;
	
	/* CONSTRUCTORS */
	
	public HardLimitingThreshold(double threshold) {
		setThreshold(threshold);
	}
	public HardLimitingThreshold() {
		this(0.0);
	}
	
	/* SIGMOID METHOD */
	
	@Override
	public double activate(double x) {
		return x < getThreshold() ? 0 : 1;
	}
	
	/* GETTERS & SETTERS */
	
	public double getThreshold() {
		return threshold;
	}
	
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	/* SERIALIZATION METHODS */
	
	private void writeObject(@NotNull ObjectOutputStream stream) throws IOException {
		stream.writeDouble(getThreshold());
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(@NotNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
		setThreshold(stream.readDouble());
	}
	
	/* OVERRIDES */
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof HardLimitingThreshold)) return false;
		HardLimitingThreshold hardLimitingThreshold = (HardLimitingThreshold) o;
		return Double.compare(hardLimitingThreshold.getThreshold(), getThreshold()) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getThreshold());
	}
	
	@Override
	public String toString() {
		return "HardLimitingThreshold{" +
				"threshold=" + threshold +
				'}';
	}
}
