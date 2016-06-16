package io.github.elytra.copo.items;

import java.util.List;

import copo.api.slot.DigitalSlot;
import copo.api.slot.DigitalSlotClientData;
import io.github.elytra.copo.core.helper.Numbers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemSlotClientData extends DigitalSlotClientData<ItemStack> {

	public ItemSlotClientData(DigitalSlot<ItemStack> slot) {
		super(slot);
	}

	@Override
	public void render(float partialTicks) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getNameForSorting() {
		return slot.getContent().getDisplayName();
	}

	@Override
	public List<String> getTooltip(EntityPlayer player, boolean advancedTooltips) {
		return slot.getContent().getTooltip(player, advancedTooltips);
	}

	@Override
	public String getFormattedAmount() {
		return Numbers.humanReadableItemCount(slot.getContent().stackSize);
	}
	
	@Override
	public int getAmountForSorting() {
		return slot.getContent().stackSize;
	}

}
