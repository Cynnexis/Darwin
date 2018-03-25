package fr.berger.darwin.remixed.listeners;

public interface DataListener<T> {
	
	void onDataChanged(T data);
}
