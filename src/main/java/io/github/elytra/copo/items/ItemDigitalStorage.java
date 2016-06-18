package io.github.elytra.copo.items;

import copo.api.CapabilityInjector;
import copo.api.DigitalStorageKind;
import copo.api.allocation.StorageAllocator;
import copo.api.content.ManagedDigitalVolume;
import copo.api.slot.DigitalSlot;
import net.minecraft.item.ItemStack;

final class ItemDigitalStorage extends DigitalStorageKind<ItemStack> {
	@Override
	public ManagedDigitalVolume<ItemStack> createContents(StorageAllocator alloc) {
		return new ItemContents(this, alloc);
	}

	@Override
	public DigitalSlot<ItemStack> createDigitalSlot(ItemStack c) {
		return new ItemDigitalSlot(this, c);
	}

	@Override
	public void injectCapabilities(CapabilityInjector inj) {
		
	}
}
