package io.github.elytra.copo.core.helper;

public interface IterationCallback<T, R> {
	void accept(IterationContext<R> ctx, T t);
}