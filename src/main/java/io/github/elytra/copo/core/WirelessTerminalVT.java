package io.github.elytra.copo.core;

import io.github.elytra.copo.core.tile.TileEntityController;
import io.github.elytra.copo.wireless.CoWirelessWorldData.Transmitter;
import io.github.elytra.copo.wireless.item.ItemWirelessTerminal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class WirelessTerminalVT implements IVT {
	private World world;
	private EntityPlayer player;
	private ItemWirelessTerminal iwt;
	private ItemStack stack;
	private UserPreferences prefs = new UserPreferences();
	
	public WirelessTerminalVT(World world, EntityPlayer player, ItemWirelessTerminal iwt, ItemStack stack) {
		this.world = world;
		this.player = player;
		this.iwt = iwt;
		this.stack = stack;
		prefs.readFromNBT(stack.getSubCompound("Preferences", true));
	}

	@Override
	public UserPreferences getPreferences(EntityPlayer player) {
		return prefs;
	}

	@Override
	public TileEntityController getController() {
		return iwt.getTransmitterController(stack, world, player);
	}

	@Override
	public boolean hasController() {
		return iwt.getTransmitterController(stack, world, player) != null;
	}

	@Override
	public boolean supportsDumpSlot() {
		return false;
	}

	@Override
	public IInventory getDumpSlotInventory() {
		return null;
	}

	@Override
	public boolean canContinueInteracting(EntityPlayer player) {
		Transmitter t = iwt.getTransmitter(stack, world, player, false);
		return t.position.distanceSq(player.posX, player.posY, player.posZ) <= t.range*t.range;
	}

	@Override
	public void markUnderlyingStorageDirty() {
		prefs.writeToNBT(stack.getSubCompound("Preferences", true));
	}

}
