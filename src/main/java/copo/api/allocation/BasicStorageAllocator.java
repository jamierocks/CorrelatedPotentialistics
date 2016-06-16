package copo.api.allocation;

import copo.api.StorageInconsistentException;
import net.minecraft.nbt.NBTTagCompound;

public class BasicStorageAllocator implements StorageAllocator {
	private int types;
	private int bits;
	
	private int maxTypes;
	private int maxBits;
	
	private int typeAllocationCost;
	
	public void setMaxBits(int maxBits) {
		if (maxBits < bits) throw new IllegalArgumentException("Attempt to set maxBits below bits ("+maxBits+" < "+bits+")");
		this.maxBits = maxBits;
	}
	
	public void setMaxTypes(int maxTypes) {
		if (maxTypes < types) throw new IllegalArgumentException("Attempt to set maxTypes below types ("+maxTypes+" < "+types+")");
		this.maxTypes = maxTypes;
	}
	
	public void setTypeAllocationCost(int typeAllocationCost) {
		if (typeAllocationCost < 0) throw new IllegalArgumentException();
		if (types > 0) {
			checkConsistency();
			int work = bits;
			work -= (this.typeAllocationCost*types);
			work += (typeAllocationCost*types);
			if (work > maxBits) {
				throw new IllegalArgumentException("New type allocation cost ("+typeAllocationCost+") overflows max bits");
			} else {
				this.bits = work;
			}
		}
		this.typeAllocationCost = typeAllocationCost;
	}
	
	public int getTypeAllocationCost() {
		return typeAllocationCost;
	}
	
	public void checkConsistency() {
		if (bits < 0) {
			throw new StorageInconsistentException("Bits is negative");
		}
		if (types < 0) {
			throw new StorageInconsistentException("Types is negative");
		}
		if (types <= 0 && bits > 0) {
			throw new StorageInconsistentException("Bits are allocated, but no types are allocated");
		}
		if (bits - (typeAllocationCost*types) < 0) {
			throw new StorageInconsistentException("Bits minus type allocation cost is negative");
		}
	}
	
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("AllocatedTypes", types);
		tag.setInteger("AllocatedBits", bits);
		
		tag.setInteger("MaximumTypes", maxTypes);
		tag.setInteger("MaximumBits", maxBits);
		return tag;
	}

	public void deserializeNBT(NBTTagCompound nbt) {
		types = nbt.getInteger("AllocatedTypes");
		bits = nbt.getInteger("AllocatedBits");
		
		maxTypes = nbt.getInteger("MaximumTypes");
		maxBits = nbt.getInteger("MaximumBits");
	}

	@Override
	public boolean allocateType() {
		return allocateTypeAndBits(0) > 0;
	}

	@Override
	public boolean deallocateType() {
		if (types > 0) {
			types--;
			return true;
		}
		return false;
	}

	@Override
	public void assertDeallocateType() throws StorageInconsistentException {
		if (!deallocateType()) throw new StorageInconsistentException("Assertion failed!");
	}

	@Override
	public int allocateTypeAndBits(int amount) {
		if ((bits + typeAllocationCost + 1) > maxBits) return 0;
		types++;
		bits += typeAllocationCost;
		return allocateBits(amount);
	}

	@Override
	public int allocateBits(int amount) {
		if (types <= 0) {
			throw new IllegalStateException("Cannot allocate bits without allocating a type");
		}
		int amt = Math.min(amount, maxBits-bits);
		bits += amt;
		return amt;
	}

	@Override
	public int deallocateBits(int amount) {
		int amt = Math.min(amount, bits);
		bits -= amt;
		return amt;
	}

	public int getTypes() {
		return types;
	}

	public int getBits() {
		return bits;
	}

	public int getMaxTypes() {
		return maxTypes;
	}

	public int getMaxBits() {
		return maxBits;
	}

}
