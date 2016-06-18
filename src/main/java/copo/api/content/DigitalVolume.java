package copo.api.content;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import copo.api.DigitalStorageKind;
import copo.api.allocation.DummyAllocator;
import copo.api.allocation.StorageAllocator;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Represents a kind of storage within a Drive (liquid, mob, item, whatever).
 * All storage represented 
 * Represents a volume digital storage within a Drive. More exotic kinds of storage
 * must extend from this directly, but most implementors will want
 * {@link ManagedDigitalVolume} instead, as that lets you hook into the insert
 * and remove methods of the network.
 */
@ParametersAreNonnullByDefault
public abstract class DigitalVolume<T> {
	protected final StorageAllocator alloc;
	private int priority;
	private final DigitalStorageKind<T> owner;
	public DigitalVolume(DigitalStorageKind<T> owner, @Nullable StorageAllocator alloc) {
		if (alloc == null) {
			alloc = DummyAllocator.INSTANCE;
		}
		this.alloc = alloc;
		this.owner = owner;
	}
	
	public abstract void writeToNBT(NBTTagCompound tag);
	public abstract void readFromNBT(NBTTagCompound tag);
	
	public final NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return tag;
	}
	
	public final int getPriority() {
		return priority;
	}
	
	public final void setPriority(int priority) {
		this.priority = priority;
	}
	
	public final DigitalStorageKind<?> getOwner() {
		return owner;
	}
}