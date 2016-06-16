package copo.api.allocation;

import copo.api.StorageInconsistentException;

public final class DummyAllocator implements StorageAllocator {

	public static final DummyAllocator INSTANCE = new DummyAllocator();
	
	@Override public boolean allocateType() { return true; }
	@Override public boolean deallocateType() { return true; }
	@Override public void assertDeallocateType() throws StorageInconsistentException {}
	@Override public int allocateTypeAndBits(int bits) { return bits; }
	@Override public int allocateBits(int bits) { return bits; }
	@Override public int deallocateBits(int bits) { return bits; }

	private DummyAllocator() {}
	
}
