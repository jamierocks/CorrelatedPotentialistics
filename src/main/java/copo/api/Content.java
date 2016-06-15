package copo.api;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Represents the contents of digital storage. More exotic kinds of storage
 * must extend from this directly, but most implementors will want
 * {@link ManagedContent} instead, as that lets you hook into the insert
 * and remove methods of the network.
 */
public abstract class Content<C> {
	protected final StorageAllocator alloc;
	private int priority;
	private final DigitalStorage<?> owner;
	public Content(DigitalStorage<?> owner, StorageAllocator alloc) {
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
	
	public final DigitalStorage<?> getOwner() {
		return owner;
	}
}