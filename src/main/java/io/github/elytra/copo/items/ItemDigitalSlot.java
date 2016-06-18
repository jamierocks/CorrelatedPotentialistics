package io.github.elytra.copo.items;

import java.io.IOException;

import copo.api.DigitalStorageKind;
import copo.api.slot.DigitalSlot;
import copo.api.slot.DigitalSlotClientData;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

final class ItemDigitalSlot extends DigitalSlot<ItemStack> {

	public ItemDigitalSlot(DigitalStorageKind<ItemStack> parent, ItemStack content) {
		super(parent, content);
	}

	public ItemDigitalSlot(DigitalStorageKind<ItemStack> parent) {
		super(parent);
	}

	
	@Override
	public DigitalSlotClientData<ItemStack> createClientData() {
		return new ItemSlotClientData(this);
	}

	@Override
	public void writeToNetwork(PacketBuffer buf) throws IOException {
		buf.writeItemStackToBuffer(getContent());
		buf.writeInt(getContent().stackSize);
	}

	@Override
	public void readFromNetwork(PacketBuffer buf) throws IOException {
		ItemStack content = buf.readItemStackFromBuffer();
		content.stackSize = buf.readInt();
		setContent(content);
	}

}
