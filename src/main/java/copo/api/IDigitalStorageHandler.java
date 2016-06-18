package copo.api;

import copo.api.content.DigitalVolume;

public interface IDigitalStorageHandler {
	/**
	 * @return a collection of the contents of this storage handler, potentially
	 * 		immutable and potentially a view. (It is encouraged to have both be
	 * 		true.)
	 */
	Iterable<DigitalVolume<?>> getContents();
	<T> Iterable<DigitalVolume<T>> getContent(DigitalStorageKind<T> storage);
}
