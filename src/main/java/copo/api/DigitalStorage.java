package copo.api;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import copo.api.allocation.StorageAllocator;
import copo.api.content.Content;
import copo.api.slot.DigitalSlot;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.items.IItemHandler;

/**
 * Acts as a factory for objects that are specific to storage types.
 * 
 * @see io.github.elytra.copo.items.ItemDigitalStorage
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class DigitalStorage<T> extends IForgeRegistryEntry.Impl<DigitalStorage<T>> {
	public abstract Content<T> createContents(@Nullable StorageAllocator alloc);
	public DigitalSlot<T> createDigitalSlot() { return createDigitalSlot(null); }
	public abstract DigitalSlot<T> createDigitalSlot(@Nullable T c);
	/**
	 * Called when capabilities are being injected on an Interface tile entity.
	 * This is your chance to add capabilities such as {@link IFluidHandler} or
	 * {@link IItemHandler}.
	 * @param inj the capability injector
	 */
	public abstract void injectCapabilities(CapabilityInjector inj);
}
