package io.github.elytra.copo.core;

import java.util.List;

import copo.api.DigitalStorage.Content;
import copo.api.StorageAllocator;
import copo.api.StorageInconsistentException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class DriveCapabilityProvider implements ICapabilitySerializable<NBTTagCompound>, ITrackedDigitalStorageHandler, StorageAllocator {
	private int types;
	private int bits;
	
	private int maxTypes;
	private int maxBits;
	
	private int typeAllocationCost;
	
	private List<Content> contents;
	
	public DriveCapabilityProvider(ItemStack item, NBTTagCompound nbt) {
		contents = CoCore.collectContents(item, nbt);
		deserializeNBT(nbt);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (facing == null) {
			if (capability == CoCore.DIGITAL_STORAGE) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (facing == null) {
			if (capability == CoCore.DIGITAL_STORAGE) {
				return (T) this;
			}
		}
		return null;
	}

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
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("AllocatedTypes", types);
		tag.setInteger("AllocatedBits", bits);
		
		tag.setInteger("MaximumTypes", maxTypes);
		tag.setInteger("MaximumBits", maxBits);
		for (Content c : contents) {
			NBTTagCompound child = new NBTTagCompound();
			c.writeToNBT(child);
			tag.setTag(String.valueOf(c.getOwner().getRegistryName()), child);
		}
		return tag;
	}

	@Override
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
		return amt;
	}

	@Override
	public int deallocateBits(int amount) {
		int amt = Math.min(amount, bits);
		bits -= amt;
		return amt;
	}

	@Override
	public List<Content> getContents() {
		return contents;
	}

	@Override
	public int getTypes() {
		return types;
	}

	@Override
	public int getBits() {
		return bits;
	}

	@Override
	public int getMaxTypes() {
		return maxTypes;
	}

	@Override
	public int getMaxBits() {
		return maxBits;
	}
	
}
