package io.github.elytra.copo.core;

import copo.api.DigitalStorage;
import copo.api.content.Content;

public interface IDigitalStorageHandler {
	/**
	 * @return a collection of the contents of this storage handler, potentially
	 * 		immutable and potentially a view. (It is encouraged to have both be
	 * 		true.)
	 */
	Iterable<? extends Content<?>> getContents();
	<T> Iterable<Content<T>> getContent(DigitalStorage<T> storage);
}
