package io.github.elytra.copo.core.helper;

public class IterationContext<T> {
	private boolean done;
	private T result;
	
	public boolean isDone() {
		return done;
	}
	
	public void setResult(T result) {
		this.result = result;
		done = true;
	}
	
	public T getResult() {
		return result;
	}
	
}
