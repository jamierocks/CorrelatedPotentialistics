package copo.api;

/**
 * Allows the allocation of types and bits in a drive, tracking the maximum
 * bits and types for a drive, and not allowing it to go over the limit or under
 * zero.
 */
public interface StorageAllocator {
	/**
	 * Attempt to allocate one type.
	 * @return {@code true} if a type was allocated, {@code false} if not.
	 */
	public boolean allocateType();
	/**
	 * Attempt to deallocate one type.
	 * @return {@code true} if a type was deallocated, {@code false} if not.
	 */
	public boolean deallocateType();
	
	/**
	 * Deallocate one type. Throw an exception if it could not be deallocated.
	 * @throws StorageInconsistentException if it could not be deallocated
	 */
	public void assertDeallocateType() throws StorageInconsistentException;
	
	/**
	 * Attempt to allocate one type and N bits.
	 * @return The amount of bits allocated. If zero, a type was not allocated.
	 */
	public int allocateTypeAndBits(int bits);
	
	/**
	 * Attempt to allocate N bits.
	 * @return The amount of bits allocated. Potentially 0. Never greater than
	 * 		the amount requested.
	 */
	public int allocateBits(int bits);
	/**
	 * Attempt to deallocate N bits.
	 * @return The amount of bits deallocated. Potentially 0. Never greater than
	 * 		the amount requested.
	 */
	public int deallocateBits(int bits);
}
