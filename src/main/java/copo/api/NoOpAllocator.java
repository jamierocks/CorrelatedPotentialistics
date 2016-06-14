package copo.api;

public class NoOpAllocator implements StorageAllocator {

	@Override public boolean allocateType() { return true; }
	@Override public boolean deallocateType() { return true; }
	@Override public void assertDeallocateType() throws StorageInconsistentException {}
	@Override public int allocateTypeAndBits(int bits) { return bits; }
	@Override public int allocateBits(int bits) { return bits; }
	@Override public int deallocateBits(int bits) { return bits; }

}
