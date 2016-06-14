package copo.api;

import io.github.elytra.copo.core.CoCore;

/**
 * Allows the injection of custom storage types into CoPo. This is how all the
 * official kinds of storage work too, including items.
 */
public class DigitalStorageRegistry {
	public static void register(DigitalStorage storage) {
		CoCore.registry.register(storage);
	}
}
