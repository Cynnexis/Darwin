package main.fr.berger.darwin.listeners;

public interface DataListener<T> {
	
	void onDataChanged(T data);
}
