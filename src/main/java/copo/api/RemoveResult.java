package copo.api;

public class RemoveResult<U> {
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