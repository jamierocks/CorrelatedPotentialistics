package copo.api;

import javax.annotation.Nonnull;

import io.github.elytra.copo.core.CoCore;

/**
 * Allows the injection of custom storage types into CoPo. This is how all the
 * official kinds of storage work too, including items.
 */
public final class DigitalStorageRegistry {
	public static void register(@Nonnull DigitalStorage<?> storage) {
		CoCore.registry.register(storage);
	}
	
	private DigitalStorageRegistry() {}
}
