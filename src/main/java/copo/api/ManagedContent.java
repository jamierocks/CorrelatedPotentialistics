package copo.api;

import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Represents the most common type of storage contents, namely stacks.
 * @param <U> The kind of stack, such as FluidStacks or ItemStacks.
 */
public abstract class ManagedContent<U> extends Content<U> {
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
	 * @param t The thing to remove, ignoring stack size.
	 * @param amount The amount to remove
	 * @return A non-null {@link RemoveResult}, containing either the passed
	 * 		in u or a clone, depending on whether or not U is mutable. As U
	 * 		is opaque to the storage system, the RemoveResult contains the
	 * 		amount of items that still need to be removed, or 0 if the
	 * 		request was completely fulfilled.
	 */
	public abstract RemoveResult<U> remove(U u, int amount);
	
	/**
	 * @return A potentially immutable view containing a collection of Us
	 * 		with stack sizes matching the total amount of items stored. The
	 * 		returned view must actually be a view, and automatically update
	 * 		when the backing store is changed. If U is mutable, the contents
	 * 		of the view <b>must not</b> be modified.
	 */
	public abstract Iterable<U> getTypes();
	
	/**
	 * @param u The thing to look up
	 * @return The amount of the given thing, ignoring stack size, currently
	 * 		stored.
	 */
	public abstract int getAmountStored(U u);
	
	/** @see #getAmountStored(Object) */
	public static <T> int getTotalAmountStored(Iterable<ManagedContent<T>> iter, T thing) {
		if (iter == null) throw new IllegalArgumentException("Cannot manipulate a null iterable");
		int amt = 0;
		for (ManagedContent<T> c : iter) {
			amt += c.getAmountStored(thing);
		}
		return amt;
	}
	
	/** @see #getTypes() */
	public static <T> Iterable<T> getAllTypes(Iterable<ManagedContent<T>> iter) {
		if (iter == null) throw new IllegalArgumentException("Cannot manipulate a null iterable");
		List<Iterable<T>> li = Lists.newArrayList();
		for (ManagedContent<T> c : iter) {
			li.add(c.getTypes());
		}
		return Iterables.concat(li);
	}
	
	/** @see #remove(Object, int) */
	public static <T> RemoveResult<T> remove(Iterable<ManagedContent<T>> iter, T t, int amount) {
		if (iter == null) throw new IllegalArgumentException("Cannot manipulate a null iterable");
		for (ManagedContent<T> c : iter) {
			RemoveResult<T> rr = c.remove(t, amount);
			amount = rr.getRemaining();
			t = rr.getThing();
		}
		return new RemoveResult<T>(t, amount);
	}
	
	/** @see #insert(Object) */
	public static <T> T insert(Iterable<ManagedContent<T>> iter, T t) {
		if (iter == null) throw new IllegalArgumentException("Cannot manipulate a null iterable");
		for (ManagedContent<T> c : iter) {
			if (t == null) return null;
			t = c.insert(t);
		}
		return t;
	}
	
}