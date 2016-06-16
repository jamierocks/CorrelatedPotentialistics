package io.github.elytra.copo.core.item;

import java.awt.Color;
import java.util.List;
import java.util.Locale;

import io.github.elytra.copo.core.CoCore;
import io.github.elytra.copo.core.DriveStorageManager;
import io.github.elytra.copo.core.client.CoreClientProxy;
import io.github.elytra.copo.core.helper.ItemStacks;
import io.github.elytra.copo.core.helper.Numbers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ItemDrive extends Item {
	public enum Priority {
		HIGHEST(TextFormatting.RED),
		HIGHER(TextFormatting.DARK_RED),
		HIGH(TextFormatting.GRAY),
		DEFAULT(TextFormatting.GRAY),
		LOW(TextFormatting.GRAY),
		LOWER(TextFormatting.DARK_GREEN),
		LOWEST(TextFormatting.GREEN);
		public final String lowerName = name().toLowerCase(Locale.ROOT);
		public final TextFormatting color;
		private Priority(TextFormatting color) {
			this.color = color;
		}
	}
	public enum PartitioningMode {
		/*BLACKLIST, TODO*/ NONE, WHITELIST;
		public final String lowerName = name().toLowerCase(Locale.ROOT);
	}

	private final int[] tierColors = {
			0xFF1744, // Red A400
			0xFF9100, // Orange A400
			0x76FF03, // Light Green A400
			0x1DE9B6, // Teal A400
			0xD500F9, // Purple A400
	};
	private final int[] tierSizes = {
			1024 * 8,
			4096 * 8,
			16384 * 8,
			65536 * 8,
			-1
	};
	private final int[] tierAllocSizes = {
			8 * 8,
			32 * 8,
			128 * 8,
			512 * 8,
			0
	};

	public ItemDrive() {
		setMaxStackSize(1);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
		DriveStorageManager dcp = new DriveStorageManager(stack, nbt);
		if (dcp.getMaxBits() == 0) {
			dcp.setMaxBits(tierSizes[stack.getItemDamage()]);
			dcp.setMaxTypes(64);
			dcp.setTypeAllocationCost(tierAllocSizes[stack.getItemDamage()]);
		}
		return dcp;
	}
	
	public DriveStorageManager getStorage(ItemStack stack) {
		if (stack.hasCapability(CoCore.digitalStorage, null)) {
			DriveStorageManager dcp = (DriveStorageManager) stack.getCapability(CoCore.digitalStorage, null);
			dcp.checkConsistency();
			return dcp;
		}
		return null;
	}
	
	public int getFullnessColor(ItemStack stack) {
		int r;
		int g;
		int b;
		boolean dirty = stack.hasTagCompound() && stack.getTagCompound().getBoolean("Dirty") && itemRand.nextBoolean();
		if (dirty && itemRand.nextInt(20) == 0) {
			stack.getTagCompound().removeTag("Dirty");
		}
		if (stack.getItemDamage() == 4) {
			if (dirty) return 0xFF00FF;
			float sin = (MathHelper.sin(CoreClientProxy.ticks / 20f) + 2.5f) / 5f;
			r = ((int) (sin * 192f)) & 0xFF;
			g = 0;
			b = ((int) (sin * 255f)) & 0xFF;
			return r << 16 | g << 8 | b;
		} else {
			DriveStorageManager storage = getStorage(stack);
			float usedTypes = storage.getTypes()/(float)storage.getMaxTypes();
			float usedBits = storage.getBits()/(float)storage.getMaxBits();
			float both = (usedTypes+usedBits)/2;
			float hue = (1/3f)*(1-both);
			return Color.HSBtoRGB(hue, 1, dirty ? 1 : 0.65f);
		}
	}

	public int getTierColor(ItemStack stack) {
		return tierColors[stack.getItemDamage() % tierColors.length];
	}

	public int getBaseColor(ItemStack stack) {
		return stack.getItemDamage() == 4 ? 0x554455 : 0xFFFFFF;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.translateToLocalFormatted("tooltip.correlatedpotentialistics.rf_usage", getEnergyConsumptionRate(stack)));
		if (stack.getItemDamage() == 4) {
			int i = 0;
			while (I18n.canTranslate("tooltip.correlatedpotentialistics.void_drive." + i)) {
				tooltip.add(I18n.translateToLocalFormatted("tooltip.correlatedpotentialistics.void_drive." + i));
				i++;
			}
		} else {
			DriveStorageManager storage = getStorage(stack);
			int typesUsed = storage.getTypes();
			int typesMax = storage.getMaxTypes();
			int bytesUsed = storage.getBits() / 8;
			int bytesMax = storage.getMaxBits() / 8;

			int typesPercent = (int) (((double) typesUsed / (double) typesMax) * 100);
			int bytesPercent = (int) (((double) bytesUsed / (double) bytesMax) * 100);

			tooltip.add(I18n.translateToLocalFormatted("tooltip.correlatedpotentialistics.types_used", typesUsed, typesMax, typesPercent));
			tooltip.add(I18n.translateToLocalFormatted("tooltip.correlatedpotentialistics.bytes_used", Numbers.humanReadableBytes(bytesUsed), Numbers.humanReadableBytes(bytesMax), bytesPercent));
		}
	}

	public int getEnergyConsumptionRate(ItemStack stack) {
		if (stack.getItemDamage() == 4) {
			return 4;
		}
		int dmg = stack.getItemDamage() + 1;
		return ((int) Math.pow(2, dmg))/2;
	}
	
	public Priority getPriority(ItemStack stack) {
		return ItemStacks.getEnum(stack, "Priority", Priority.class)
				.or(Priority.DEFAULT);
	}

	public void setPriority(ItemStack stack, Priority priority) {
		ItemStacks.ensureHasTag(stack).getTagCompound().setString("Priority", priority.name());
	}

	public PartitioningMode getPartitioningMode(ItemStack stack) {
		return ItemStacks.getEnum(stack, "PartitioningMode", PartitioningMode.class)
				.or(PartitioningMode.NONE);
	}

	public void setPartitioningMode(ItemStack stack, PartitioningMode mode) {
		ItemStacks.ensureHasTag(stack).getTagCompound().setString("PartitioningMode", mode.name());
	}
	
	public void markDirty(ItemStack stack) {
		ItemStacks.ensureHasTag(stack).getTagCompound().setBoolean("Dirty", true);
	}

}
