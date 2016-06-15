package io.github.elytra.copo.core;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import gnu.trove.strategy.HashingStrategy;
import net.minecraft.item.ItemStack;

public class ProtoStackHashingStrategy implements HashingStrategy<ItemStack> {
	private static final long serialVersionUID = 574567345L;
	
	@Override
	public int computeHashCode(ItemStack object) {
		return new HashCodeBuilder()
				.append(object.getTagCompound())
				.append(object.getItem())
				.append(object.getMetadata())
				.build();
	}
	@Override
	public boolean equals(ItemStack a, ItemStack b) {
		return ItemStack.areItemsEqual(a, b) && ItemStack.areItemStackTagsEqual(a, b);
	}
}
