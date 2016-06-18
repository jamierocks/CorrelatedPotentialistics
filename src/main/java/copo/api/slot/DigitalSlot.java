package copo.api.slot;

import java.io.IOException;

import javax.annotation.ParametersAreNonnullByDefault;

import copo.api.DigitalStorageKind;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;

import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Represents a slot in the storage system.
 *
 * @param <T> The type of stack stored in this slot, such as ItemStack
 * @see io.github.elytra.copo.items.ItemDigitalSlot
 */
@ParametersAreNonnullByDefault
public abstract class DigitalSlot<T> {
	protected final DigitalStorageKind<T> parent;
	private T content;
	public DigitalSlot(DigitalStorageKind<T> parent) {
		this.parent = parent;
	}
	public DigitalSlot(DigitalStorageKind<T> parent, T content) {
		this(parent);
		this.content = content;
	}

	public final T getContent() {
		return content;
	}
	
	public final void setContent(T content) {
		if (content == null) throw new IllegalArgumentException("Remove this slot instead of setting its content to null");
		this.content = content;
	}
	
	/**
	 * @return a DigitalSlotClientData for this slot.
	 */
	@SideOnly(Side.CLIENT)
	public abstract DigitalSlotClientData<T> createClientData();
	
	public abstract void writeToNetwork(PacketBuffer buf) throws IOException;
	public abstract void readFromNetwork(PacketBuffer buf) throws IOException;
	
}
