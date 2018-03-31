package fr.berger.darwin.remixed;

import fr.berger.beyondcode.util.EnhancedObservable;
import fr.berger.darwin.remixed.listeners.DataListener;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Gene<T> extends EnhancedObservable implements Serializable, Cloneable {
	
	@Nullable
	private T data;
	
	@Nullable
	private Class<T> clazz;
	
	@NotNull
	private ArrayList<DataListener<T>> dataListeners;
	
	public Gene(@Nullable T data, @Nullable Class<T> clazz) {
		setData(data);
		setClazz(clazz);
		setDataListeners(new ArrayList<>());
	}
	public Gene(@Nullable T data) {
		setData(data);
		setDataListeners(new ArrayList<>());
	}
	public Gene(@Nullable Class<T> clazz) {
		this(null, clazz);
	}
	@Contract("null -> fail")
	public Gene(@NotNull Gene<T> copy) {
		this(copy.getData(), copy.getClazz());
	}
	public Gene() {
		this(null, null);
	}
	
	/* SERIALIZATION METHODS */
	
	private void writeObject(@NotNull ObjectOutputStream stream) throws IOException {
		// Class if serialized first to be read first (and extract easier the data during reading)
		stream.writeObject(getClazz());
		stream.writeObject(getData());
		stream.writeObject(getDataListeners());
	}
	
	@SuppressWarnings("unchecked")
	private void readObject(@NotNull ObjectInputStream stream) throws IOException, ClassNotFoundException {
		setClazz((Class<T>) stream.readObject());
		if (getClazz() != null)
			setData(getClazz().cast(stream.readObject()));
		else
			setData((T) stream.readObject());
		setDataListeners((ArrayList<DataListener<T>>) stream.readObject());
	}
	
	/* GETTER & SETTER */
	
	@Nullable
	public T getData() {
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public void setData(@Nullable T data) {
		this.data = data;
		
		if (this.data != null && getClazz() == null)
			setClazz((Class<T>) this.data.getClass());
		
		snap(this.data);
		
		for (DataListener<T> dataListener : getDataListeners())
			dataListener.onDataChanged(this.data);
	}
	
	@Nullable
	public Class<T> getClazz() {
		return clazz;
	}
	
	public void setClazz(@Nullable Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public @NotNull ArrayList<DataListener<T>> getDataListeners() {
		if (this.dataListeners == null)
			this.dataListeners = new ArrayList<>();
		
		return this.dataListeners;
	}
	
	public void setDataListeners(@NotNull ArrayList<DataListener<T>> dataListeners) {
		if (dataListeners == null)
			throw new NullPointerException();
		
		for (DataListener<T> dataListener : dataListeners)
			if (dataListener == null)
				throw new NullPointerException();
		
		this.dataListeners = dataListeners;
	}
	
	public void addDataListener(@NotNull DataListener<T> dataListener) {
		if (dataListener == null)
			throw new NullPointerException();
		
		if (getDataListeners() == null)
			this.dataListeners = new ArrayList<>();
		
		getDataListeners().add(dataListener);
	}
	
	/* OVERRIDES */
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Gene)) return false;
		Gene<?> gene = (Gene<?>) o;
		return Objects.equals(getData(), gene.getData()) &&
				Objects.equals(getClazz(), gene.getClazz());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getData(), getClazz());
	}
	
	@Override
	public String toString() {
		return "Gene{" +
				"data=\"" + data + '\"' +
				'}';
	}
}
