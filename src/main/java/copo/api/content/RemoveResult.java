package copo.api.content;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

public class RemoveResult<T> {
	@Nullable
	private final T thing;
	@Nonnegative
	private final int remaining;
	/**
	 * @param thing
	 * @param remaining The amount of items that still need to be
	 * 		retrieved, or 0 if they were all retrieved.
	 */
	public RemoveResult(@Nullable T thing, @Nonnegative int remaining) {
		this.thing = thing;
		this.remaining = remaining;
	}
	
	@Nullable
	public T getThing() {
		return thing;
	}
	
	@Nonnegative
	public int getRemaining() {
		return remaining;
	}
}