package copo.api;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import io.github.elytra.copo.items.ItemDigitalStorage;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.items.IItemHandler;

/**
 * Acts as a factory for objects that are specific to storage types.
 * 
 * @see ItemDigitalStorage
 */
@ParametersAreNonnullByDefault
public abstract class DigitalStorage<C> extends IForgeRegistryEntry.Impl<DigitalStorage<C>> {
	public abstract Content<C> createContents(StorageAllocator alloc);
	public DigitalSlot<C> createDigitalSlot() { return createDigitalSlot(null); }
	public abstract DigitalSlot<C> createDigitalSlot(@Nullable C c);
	/**
	 * Called when capabilities are being injected on an Interface tile entity.
	 * This is your chance to add capabilities such as {@link IFluidHandler} or
	 * {@link IItemHandler}.
	 * @param inj the capability injector
	 */
	public abstract void injectCapabilities(CapabilityInjector inj);
}
