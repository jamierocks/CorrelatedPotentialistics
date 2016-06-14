package copo.api;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.elytra.copo.items.ItemDigitalStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

/**
 * Represents common functionality between all kinds of digital storage. Extend
 * this class and add your own methods to add new functionality to the
 * Correlated storage system.
 * 
 * @see ItemDigitalStorage
 */
@ParametersAreNonnullByDefault
public abstract class DigitalStorage<T extends DigitalStorage.Content> extends IForgeRegistryEntry.Impl<DigitalStorage<T>> {
	/**
	 * Represents the contents of digital storage. More exotic kinds of storage
	 * must extend from this directly, but most implementors will want
	 * {@link ManagedContent} instead, as that lets you hook into the insert
	 * and remove methods of the network.
	 */
	public static abstract class Content {
		protected final StorageAllocator alloc;
		private int priority;
		private final DigitalStorage<?> owner;
		public Content(DigitalStorage<?> owner, StorageAllocator alloc) {
			if (alloc == null) {
				alloc = new NoOpAllocator();
			}
			this.alloc = alloc;
			this.owner = owner;
		}
		
		public abstract void writeToNBT(NBTTagCompound tag);
		public abstract void readFromNBT(NBTTagCompound tag);
		
		public final NBTTagCompound writeToNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			writeToNBT(tag);
			return tag;
		}
		
		public final int getPriority() {
			return priority;
		}
		
		public final void setPriority(int priority) {
			this.priority = priority;
		}
		
		public final DigitalStorage<?> getOwner() {
			return owner;
		}
	}
	/**
	 * Represents the most common type of storage contents, namely stacks.
	 * @param <U> The kind of stack, such as FluidStacks or ItemStacks.
	 */
	public static abstract class ManagedContent<U> extends Content {
		public ManagedContent(DigitalStorage<?> owner, StorageAllocator alloc) {
			super(owner, alloc);
		}

		public abstract boolean canHandle(Object t);
		public abstract boolean canHandle(Class<?> clazz);
		
		/**
		 * Add an item, fluid, etc to this storage.
		 * @param u The thing to insert.
		 * @return Either u or a new instance of U, depending on if U is
		 * 		immutable. The stack size will be modified according to how
		 *		many things fit in this storage. <b>If all the things fit in
		 *		this storage, {@code null} will be returned.</b>
		 */
		public abstract U insert(U u);
		
		/**
		 * Remove an item, fluid, etc, from this storage.
		 * @param t The thing to remove
		 * @param amount The amount to remove
		 * @return A non-null {@link RemoveResult}, containing either the passed
		 * 		in u or a clone, depending on whether or not U is mutable. As U
		 * 		is opaque to the storage system, the RemoveResult contains the
		 * 		amount of items that still need to be removed, or 0 if the
		 * 		request was completely fulfilled.
		 */
		public abstract RemoveResult<U> remove(U u, int amount);
		
		/**
		 * @return A potentially immutable list containing a list of Us with
		 * 		stack sizes matching the total amount of items stored. The
		 * 		returned list must effectively be a view, and automatically
		 * 		update when the backing store is changed. If U is mutable, the
		 * 		contents of the view <b>must not</b> be modified.
		 */
		public abstract List<U> getTypes();
		
	}
	
	public static class RemoveResult<U> {
		private final U thing;
		private final int remaining;
		/**
		 * @param thing
		 * @param remaining The amount of items that still need to be
		 * 		retrieved, or 0 if they were all retrieved.
		 */
		public RemoveResult(U thing, int remaining) {
			this.thing = thing;
			this.remaining = remaining;
		}
		
		public U getThing() {
			return thing;
		}
		
		public int getRemaining() {
			return remaining;
		}
	}

	public abstract T createContents(StorageAllocator alloc);
}
