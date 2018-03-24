package fr.berger.darwin.connection.handlers;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

public class Linear implements ActivationHandler, Serializable, Cloneable {
	
	/* PROPERTY */
	
	private double a;
	
	/* CONSTRUCTORS */
	
	public Linear(double a) {
		setA(a);
	}
	public Linear() {
		this(1.0);
	}
	
	/* SIGMOID METHOD */
	
	@Override
	public double activate(double x) {
		return getA() * x;
	}
	
	/* GETTERS & SETTERS */
	
	public double getA() {
		return a;
	}
	
	public void setA(double a) {
		this.a = a;
	}
	
	/* SERIALIZATION METHODS */
	
	private void writeObject(@NotNull ObjectOutputStream stream) throws IOException {
		stream.writeDouble(getA());
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(@NotNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
		setA(stream.readDouble());
	}
	
	/* OVERRIDES */
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Linear)) return false;
		Linear linear = (Linear) o;
		return Double.compare(linear.getA(), getA()) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getA());
	}
	
	@Override
	public String toString() {
		return "Linear{" +
				"a=" + a +
				'}';
	}
}
