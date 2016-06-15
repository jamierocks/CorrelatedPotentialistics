package io.github.elytra.copo.core.tile;

import java.util.List;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import copo.api.Content;
import copo.api.DigitalStorage;
import io.github.elytra.copo.core.CoCore;
import io.github.elytra.copo.core.IDigitalStorageHandler;
import io.github.elytra.copo.core.block.BlockDriveBay;
import io.github.elytra.copo.core.helper.ItemStacks;
import io.github.elytra.copo.core.item.ItemDrive;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityDriveBay extends TileEntityNetworkMember implements ITickable, IDigitalStorageHandler {

	private ItemStack[] drives = new ItemStack[8];
	private int consumedPerTick = 8;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		writeDrives(compound, 0, 1, 2, 3, 4, 5, 6, 7);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readDrives(compound);
	}

	@Override
	public int getEnergyConsumedPerTick() {
		return consumedPerTick;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("x", getPos().getX());
		nbt.setInteger("y", getPos().getY());
		nbt.setInteger("z", getPos().getZ());
		writeDrives(nbt, 0, 1, 2, 3, 4, 5, 6, 7);
		return nbt;
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readDrives(pkt.getNbtCompound());
	}

	@Override
	public void update() {
		if (hasWorldObj() && !worldObj.isRemote) {
			for (int i = 0; i < 8; i++) {
				ItemStack is = drives[i];
				if (is == null) continue;
				if (ItemStacks.getBoolean(is, "Dirty").or(false)) {
					is.getTagCompound().removeTag("Dirty");
					markDirty();
					setDriveInSlot(i, is);
				}
			}
			IBlockState state = getWorld().getBlockState(getPos());
			if (state.getBlock() == CoCore.drive_bay) {
				boolean lit;
				if (hasController() && getController().isPowered()) {
					lit = true;
				} else {
					lit = false;
				}
				if (lit != state.getValue(BlockDriveBay.lit)) {
					getWorld().setBlockState(getPos(), state.withProperty(BlockDriveBay.lit, lit));
				}
			}
		}
	}

	private void writeDrives(NBTTagCompound nbt, int... slots) {
		for (int i : slots) {
			writeDrive(nbt, i);
		}
	}

	private void writeDrive(NBTTagCompound nbt, int slot) {
		NBTTagCompound drive = new NBTTagCompound();
		if (drives[slot] != null) {
			drives[slot].writeToNBT(drive);
		}
		nbt.setTag("Drive"+slot, drive);
	}

	private void readDrives(NBTTagCompound nbt) {
		for (int i = 0; i < drives.length; i++) {
			if (nbt.hasKey("Drive"+i)) {
				NBTTagCompound drive = nbt.getCompoundTag("Drive"+i);
				if (drive.hasNoTags()) {
					drives[i] = null;
				} else {
					ItemStack is = ItemStack.loadItemStackFromNBT(drive);
					if (hasWorldObj() && worldObj.isRemote) {
						ItemStacks.ensureHasTag(is);
						is.setTagCompound((NBTTagCompound)is.getTagCompound().copy());
						is.getTagCompound().setBoolean("Dirty", true);
					}
					drives[i] = is;
				}
			}
		}
		onDriveChange();
	}

	public void setDriveInSlot(int slot, ItemStack drive) {
		drives[slot] = drive;
		if (hasWorldObj() && !worldObj.isRemote && worldObj instanceof WorldServer) {
			NBTTagCompound nbt = new NBTTagCompound();
			writeDrive(nbt, slot);
			WorldServer ws = (WorldServer)worldObj;
			Chunk c = worldObj.getChunkFromBlockCoords(getPos());
			SPacketUpdateTileEntity packet = new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), nbt);
			for (EntityPlayerMP player : worldObj.getPlayers(EntityPlayerMP.class, Predicates.alwaysTrue())) {
				if (ws.getPlayerChunkMap().isPlayerWatchingChunk(player, c.xPosition, c.zPosition)) {
					player.connection.sendPacket(packet);
				}
			}
			onDriveChange();
		}
	}

	private void onDriveChange() {
		int old = consumedPerTick;
		consumedPerTick = 8;
		for (ItemStack is : drives) {
			if (is == null) continue;
			if (is.getItem() instanceof ItemDrive) {
				consumedPerTick += ((ItemDrive)is.getItem()).getEnergyConsumptionRate(is);
			}
		}
		if (hasWorldObj() && !worldObj.isRemote && hasController()) {
			getController().updateConsumptionRate(consumedPerTick-old);
			getController().updateStorageCache();
		}
	}

	public ItemStack getDriveInSlot(int slot) {
		return drives[slot];
	}

	public boolean hasDriveInSlot(int slot) {
		return drives[slot] != null;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (facing == null && capability == CoCore.DIGITAL_STORAGE) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (facing == null & capability == CoCore.DIGITAL_STORAGE) {
			return (T)this;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public Iterable<Content> getContents() {
		List<Iterable<Content>> li = Lists.newArrayList();
		for (ItemStack is : drives) {
			if (is != null && is.hasCapability(CoCore.DIGITAL_STORAGE, null)) {
				li.add(is.getCapability(CoCore.DIGITAL_STORAGE, null).getContents());
			}
		}
		return Iterables.concat(li);
	}

	@Override
	public <T extends Content> Iterable<T> getContent(DigitalStorage<T> storage) {
		return (Iterable<T>) Iterables.filter(getContents(), it -> it.getOwner() == storage);
	}

}
