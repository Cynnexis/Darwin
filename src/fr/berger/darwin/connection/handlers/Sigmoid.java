package fr.berger.darwin.connection.handlers;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Sigmoid implements ActivationHandler, Serializable, Cloneable {
	
	/* PROPERTY */
	
	private double a;
	
	/* CONSTRUCTORS */
	
	public Sigmoid(double a) {
		setA(a);
	}
	public Sigmoid() {
		this(1.0);
	}
	
	/* SIGMOID METHOD */
	
	@Override
	public double activate(double x) {
		return 1.0/(1.0 + Math.exp(- getA() * x));
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
		if (!(o instanceof Sigmoid)) return false;
		Sigmoid sigmoid = (Sigmoid) o;
		return Double.compare(sigmoid.getA(), getA()) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getA());
	}
	
	@Override
	public String toString() {
		return "Sigmoid{" +
				"a=" + a +
				'}';
	}
}
