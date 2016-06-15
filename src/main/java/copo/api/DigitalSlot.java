package copo.api;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Represents a slot in the storage system.
 *
 * @param <T> The type of stack stored in this slot, such as ItemStack
 * @see ItemDigitalSlot
 */
public abstract class DigitalSlot<T> {
	protected T t;
	public DigitalSlot() {}
	public DigitalSlot(T t) {
		this.t = t;
	}
	
	/**
	 * @return a DigitalSlotClientData for this slot.
	 */
	@SideOnly(Side.CLIENT)
	public abstract DigitalSlotClientData<T> createClientData();
	
	public abstract void writeToNetwork(ByteBuf buf);
	public abstract void readFromNetwork(ByteBuf buf);
	
}
