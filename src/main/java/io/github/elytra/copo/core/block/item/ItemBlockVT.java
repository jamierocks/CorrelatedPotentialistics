package io.github.elytra.copo.core.block.item;

import java.util.List;

import io.github.elytra.copo.core.CoCore;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockVT extends ItemBlock {

	public ItemBlockVT(Block block) {
		super(block);
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.format("tooltip."+CoCore.MODID+".rf_usage", 4));
	}

}
