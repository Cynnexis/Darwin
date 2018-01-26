package main.fr.berger.darwin;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import main.fr.berger.darwin.listeners.DataListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Gene<T> implements Serializable {
	
	@Nullable
	private T data;
	
	@NotNull
	private ArrayList<DataListener<T>> dataListeners;
	
	public Gene(T data) {
		setData(data);
		setDataListeners(new ArrayList<>());
	}
	public Gene(Gene<T> copy) {
		setData(copy.getData());
		setDataListeners(new ArrayList<>());
	}
	public Gene() {
		setData(null);
		setDataListeners(new ArrayList<>());
	}
	
	/* GETTER & SETTER */
	
	public T getData() {
		return data;
	}
	
	public void setData(T data) {
		this.data = data;
		
		for (DataListener<T> dataListener : getDataListeners())
			dataListener.onDataChanged(this.data);
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
	public String toString() {
		return "Gene{" +
				"data=\"" + data.toString() + "\"" +
				'}';
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Gene)) return false;
		Gene<?> gene = (Gene<?>) o;
		return Objects.equals(getData(), gene.getData());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getData());
	}
}
