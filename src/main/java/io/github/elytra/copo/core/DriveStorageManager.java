package io.github.elytra.copo.core;

import java.util.Collections;
import java.util.Map;

import copo.api.DigitalStorageKind;
import copo.api.ITrackedDigitalStorageHandler;
import copo.api.allocation.BasicStorageAllocator;
import copo.api.allocation.StorageAllocator;
import copo.api.content.DigitalVolume;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class DriveStorageManager implements ICapabilitySerializable<NBTTagCompound>, ITrackedDigitalStorageHandler {
	
	private final Map<DigitalStorageKind<?>, DigitalVolume<?>> contents;
	private final BasicStorageAllocator alloc = new BasicStorageAllocator();
	
	public DriveStorageManager(ItemStack item, NBTTagCompound nbt) {
		contents = CoCore.collectContents(alloc);
		deserializeNBT(nbt);
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (facing == null) {
			if (capability == CoCore.digitalStorage) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (facing == null) {
			if (capability == CoCore.digitalStorage) {
				return (T) this;
			}
		}
		return null;
	}

	@Override
	public <T> Iterable<DigitalVolume<T>> getContent(DigitalStorageKind<T> storage) {
		return Collections.singleton((DigitalVolume<T>) contents.get(storage));
	}
	
	@Override
	public Iterable<DigitalVolume<?>> getContents() {
		return contents.values();
	}
	
	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("Allocation", alloc.serializeNBT());
		for (DigitalVolume<?> c : contents.values()) {
			NBTTagCompound child = new NBTTagCompound();
			c.writeToNBT(child);
			tag.setTag(String.valueOf(c.getOwner().getRegistryName()), child);
		}
		return tag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		alloc.deserializeNBT(nbt.getCompoundTag("Allocation"));
		
		for (DigitalVolume<?> c : contents.values()) {
			NBTTagCompound child = nbt.getCompoundTag(String.valueOf(c.getOwner().getRegistryName()));
			c.readFromNBT(child);
		}
	}

	@Override
	public int getTypes() {
		return alloc.getTypes();
	}

	@Override
	public int getBits() {
		return alloc.getBits();
	}

	@Override
	public int getMaxTypes() {
		return alloc.getMaxTypes();
	}

	@Override
	public int getMaxBits() {
		return alloc.getMaxBits();
	}

	@Override
	public int getTypeAllocationCost() {
		return alloc.getTypeAllocationCost();
	}
	
	public BasicStorageAllocator getAllocator() {
		return alloc;
	}
}