package io.github.elytra.copo.core;

import copo.api.Content;
import copo.api.DigitalStorage;

public interface IDigitalStorageHandler {
	/**
	 * @return a collection of the contents of this storage handler, potentially
	 * 		immutable and potentially a view. (It is encouraged to have both be
	 * 		true.)
	 */
	Iterable<Content<?>> getContents();
	<T> Iterable<Content<T>> getContent(DigitalStorage<T> storage);
}
