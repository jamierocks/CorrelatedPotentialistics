package io.github.elytra.copo.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

public class ItemMisc extends Item {
	public static final String[] items = {
			"processor",
			"ceramic_drive_platter",
			"metallic_drive_platter",
			"luminous_pearl",
			"lumtorch",
			"weldthrower_fuel"
	};

	@Override
	public boolean getHasSubtypes() {
		return true;
	}
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack)+"."+stack.getItemDamage();
	}
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		int i = 0;
		while (I18n.canTranslate("tooltip.correlatedpotentialistics.misc." + stack.getItemDamage() + "." + i)) {
			tooltip.add(I18n.translateToLocal("tooltip.correlatedpotentialistics.misc." + stack.getItemDamage() + "." + i));
			i++;
		}
	}
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		for (int i = 0; i < items.length; i++) {
			subItems.add(new ItemStack(itemIn, 1, i));
		}
	}
}
