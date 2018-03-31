package fr.berger.darwin.remixed.listeners;

import org.jetbrains.annotations.NotNull;

public interface DataListener<T> {
	
	void onDataChanged(@NotNull T data);
}
