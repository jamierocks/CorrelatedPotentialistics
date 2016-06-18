package copo.api.content;

import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import copo.api.DigitalStorageKind;
import copo.api.allocation.StorageAllocator;

/**
 * Represents the most common type of storage contents, namely stacks.
 * @param <T> The kind of stack, such as FluidStacks or ItemStacks.
 */
public abstract class ManagedDigitalVolume<T> extends DigitalVolume<T> {
	public ManagedDigitalVolume(DigitalStorageKind<T> owner, StorageAllocator alloc) {
		super(owner, alloc);
	}

	public abstract boolean canHandle(@Nullable Object t);
	public abstract boolean canHandle(@Nullable Class<?> clazz);
	
	/**
	 * Add an item, fluid, etc to this storage.
	 * @param t The thing to insert.
	 * @return Either t or a new instance of T, depending on if T is
	 * 		immutable. The stack size will be modified according to how
	 *		many things fit in this storage. <b>If all the things fit in
	 *		this storage, {@code null} will be returned.</b>
	 */
	@Nullable
	public abstract T insert(@Nullable T t);
	
	/**
	 * Remove an item, fluid, etc, from this storage.
	 * @param t The thing to remove, ignoring stack size.
	 * @param amount The amount to remove
	 * @return A non-null {@link RemoveResult}, containing either the passed
	 * 		in t or a clone, depending on whether or not T is mutable. As T
	 * 		is opaque to the storage system, the RemoveResult contains the
	 * 		amount of items that still need to be removed, or 0 if the
	 * 		request was completely fulfilled.
	 */
	@Nonnull
	public abstract RemoveResult<T> remove(@Nullable T t, @Nonnegative int amount);
	
	/**
	 * @return A view of 'items' stored by this Volume, with stack sizes (if applicable)
	 * 		matching the total amount of 'items' stored. The returned view must actually
	 *      be a view, and automatically update when the backing store is changed. If T
	 *      is a mutable type, you <b>must not</b> modify items that are exposed by this
	 *      method.
	 */
	@Nonnull
	public abstract Iterable<T> getContents();
	
	/**
	 * @param t The thing to look up
	 * @return The amount of the given thing, ignoring stack size, currently
	 * 		stored.
	 */
	@Nonnegative
	public abstract int getAmountStored(@Nullable T t);
	
	/** @see #getAmountStored(Object) */
	@Nonnegative
	public static <T> int getTotalAmountStored(@Nonnull Iterable<ManagedDigitalVolume<T>> iter, @Nullable T thing) {
		if (iter == null) throw new IllegalArgumentException("Cannot manipulate a null iterable");
		if (thing == null) return 0;
		int amt = 0;
		for (ManagedDigitalVolume<T> c : iter) {
			amt += c.getAmountStored(thing);
		}
		return amt;
	}
	
	/** @see #getContents() */
	@Nonnull
	public static <T> Iterable<T> getAllTypes(@Nonnull Iterable<ManagedDigitalVolume<T>> iter) {
		if (iter == null) throw new IllegalArgumentException("Cannot manipulate a null iterable");
		List<Iterable<T>> li = Lists.newArrayList();
		for (ManagedDigitalVolume<T> c : iter) {
			li.add(c.getContents());
		}
		return Iterables.concat(li);
	}
	
	/** @see #remove(Object, int) */
	@Nonnull
	public static <T> RemoveResult<T> remove(@Nonnull Iterable<ManagedDigitalVolume<T>> iter, @Nullable T t, @Nonnegative int amount) {
		if (iter == null) throw new IllegalArgumentException("Cannot manipulate a null iterable");
		for (ManagedDigitalVolume<T> c : iter) {
			RemoveResult<T> rr = c.remove(t, amount);
			amount = rr.getRemaining();
			t = rr.getThing();
		}
		return new RemoveResult<T>(t, amount);
	}
	
	/** @see #insert(Object) */
	@Nullable
	public static <T> T insert(@Nonnull Iterable<ManagedDigitalVolume<T>> iter, @Nullable T t) {
		if (iter == null) throw new IllegalArgumentException("Cannot manipulate a null iterable");
		for (ManagedDigitalVolume<T> c : iter) {
			if (t == null) return null;
			t = c.insert(t);
		}
		return t;
	}
	
}