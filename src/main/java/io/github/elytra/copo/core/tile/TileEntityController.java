package io.github.elytra.copo.core.tile;

import java.util.List;
import java.util.Set;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import copo.api.IDigitalStorageHandler;
import copo.api.content.DigitalVolume;
import copo.api.content.ManagedDigitalVolume;
import copo.api.content.RemoveResult;
import io.github.elytra.copo.core.CoCore;
import io.github.elytra.copo.core.block.BlockController;
import io.github.elytra.copo.core.block.BlockController.State;
import io.github.elytra.copo.core.helper.ContentsComparator;
import io.github.elytra.copo.wireless.tile.TileEntityWirelessReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TileEntityController extends TileEntityNetworkMember implements IEnergyReceiver, ITickable {
	private final EnergyStorage energy = new EnergyStorage(32000, 321);
	public boolean error = false;
	public boolean booting = true;
	public String errorReason;
	private int consumedPerTick = 32;
	public int bootTicks = 0;
	private final transient Set<BlockPos> networkMembers = Sets.newHashSet();
	private final transient Set<BlockPos> receivers = Sets.newHashSet();
	
	private final transient List<ManagedDigitalVolume<?>> contents = Lists.newArrayList();
	
	public int changeId = 0;
	private boolean checkingInfiniteLoop = false;

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		energy.readFromNBT(compound);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		energy.writeToNBT(compound);
		return compound;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return energy.getMaxEnergyStored();
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public void update() {
		if (!hasWorldObj() || getWorld().isRemote) return;
		if (bootTicks > 100 && booting) {
			/*
			 * The booting delay is meant to deal with people avoiding the
			 * system's passive power drain by just shutting it off when it's
			 * not in use. Without this, I'd expect a common setup to be hooking
			 * up some sort of RF toggle to a pressure plate, so the system is
			 * only online when someone is actively using it. This makes such a
			 * minmax setup inconvenient.
			 */
			booting = false;
			scanNetwork();
		}
		if (isPowered()) {
			energy.modifyEnergyStored(-getEnergyConsumedPerTick());
			bootTicks++;
		} else {
			energy.setEnergyStored(0);
		}
		updateState();
	}

	@Override
	public int getEnergyConsumedPerTick() {
		return consumedPerTick;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		int rtrn = energy.receiveEnergy(maxReceive, simulate);
		updateState();
		return rtrn;
	}

	@Override
	public boolean hasController() {
		return true;
	}

	@Override
	public TileEntityController getController() {
		return this;
	}

	@Override
	public void setController(TileEntityController controller) {}

	public void scanNetwork() {
		if (!hasWorldObj()) return;
		if (worldObj.isRemote) return;
		if (booting) return;
		Set<BlockPos> seen = Sets.newHashSet();
		List<TileEntityNetworkMember> members = Lists.newArrayList();
		List<BlockPos> queue = Lists.newArrayList(getPos());
		boolean foundOtherController = false;
		int consumedPerTick = 32;

		for (BlockPos pos : networkMembers) {
			TileEntity te = worldObj.getTileEntity(pos);
			if (te instanceof TileEntityNetworkMember) {
				((TileEntityNetworkMember)te).setController(null);
			}
		}

		networkMembers.clear();
		receivers.clear();

		int itr = 0;
		while (!queue.isEmpty()) {
			if (itr > 100) {
				error = true;
				errorReason = "network_too_big";
				consumedPerTick = 320;
				return;
			}
			BlockPos pos = queue.remove(0);
			seen.add(pos);
			TileEntity te = getWorld().getTileEntity(pos);
			if (te instanceof TileEntityNetworkMember) {
				for (EnumFacing ef : EnumFacing.VALUES) {
					BlockPos p = pos.offset(ef);
					if (seen.contains(p)) continue;
					if (worldObj.getTileEntity(p) == null) {
						seen.add(p);
						continue;
					}
					queue.add(p);
				}
				if (te != this) {
					if (te instanceof TileEntityController) {
						error = true;
						((TileEntityController) te).error = true;
						CoCore.log.debug("Found other controller");
						foundOtherController = true;
					}
					if (!members.contains(te)) {
						members.add((TileEntityNetworkMember) te);
						if (te instanceof TileEntityWirelessReceiver) {
							receivers.add(pos);
						}
						networkMembers.add(pos);
						consumedPerTick += ((TileEntityNetworkMember) te).getEnergyConsumedPerTick();
					}
				}
			}
			itr++;
		}
		if (foundOtherController) {
			error = true;
			errorReason = "multiple_controllers";
			consumedPerTick = 4;
		} else {
			error = false;
			errorReason = null;
		}
		checkInfiniteLoop();
		for (TileEntityNetworkMember te : members) {
			te.setController(this);
		}
		if (consumedPerTick > 320) {
			error = true;
			errorReason = "too_much_power";
		}
		this.consumedPerTick = consumedPerTick;
		updateStorageCache();
		CoCore.log.info("Found "+members.size()+" network members");
	}
	
	public void checkInfiniteLoop() {
		checkingInfiniteLoop = true;
		for (TileEntityWirelessReceiver r : members(TileEntityWirelessReceiver.class, receivers)) {
			TileEntityController cont = r.getTransmitterController();
			if (cont != null && cont.isLinkedTo(this)) {
				error = true;
				errorReason = "infinite_loop";
				receivers.clear();
				checkingInfiniteLoop = false;
				return;
			}
		}
		if (error && errorReason.equals("infinite_loop")) {
			error = false;
			errorReason = null;
		}
		checkingInfiniteLoop = false;
	}
	
	private <T extends TileEntity> List<T> members(Class<T> clazz, Set<BlockPos> positions) {
		boolean missing = false;
		List<T> li = Lists.newArrayList();
		for (BlockPos pos : positions) {
			TileEntity te = getWorld().getTileEntity(pos);
			if (clazz.isInstance(te)) {
				li.add((T)te);
			} else {
				missing = true;
			}
		}
		if (missing) {
			CoCore.log.warn("One or more network members have gone missing without notice, rescanning network");
			scanNetwork();
		}
		return li;
	}

	public boolean isCheckingInfiniteLoop() {
		return checkingInfiniteLoop;
	}
	
	public boolean isLinkedTo(TileEntityController tec) {
		if (tec.equals(this)) return true;
		for (TileEntityWirelessReceiver r : members(TileEntityWirelessReceiver.class, receivers)) {
			TileEntityController cont = r.getTransmitterController();
			if (cont != null && cont.isLinkedTo(tec)) {
				return true;
			}
		}
		return false;
	}

	private void updateState() {
		if (!hasWorldObj()) return;
		if (worldObj.isRemote) return;
		State old = worldObj.getBlockState(getPos()).getValue(BlockController.state);
		State nw;
		if (isPowered()) {
			if (old == State.OFF) {
				booting = true;
				bootTicks = -200;
			}
			if (booting) {
				nw = State.BOOTING;
			} else if (error) {
				nw = State.ERROR;
			} else {
				nw = State.POWERED;
			}
		} else {
			nw = State.OFF;
		}
		if (old != nw) {
			worldObj.setBlockState(getPos(), worldObj.getBlockState(getPos())
					.withProperty(BlockController.state, nw));
		}
	}
	
	public boolean isPowered() {
		return energy.getEnergyStored() >= getEnergyConsumedPerTick();
	}

	/** assumes the network cache is also up to date, if it's not, call scanNetwork */
	public void updateStorageCache() {
		if (hasWorldObj() && worldObj.isRemote) return;
		contents.clear();
		for (TileEntity te : members(TileEntity.class, networkMembers)) {
			if (te.hasCapability(CoCore.digitalStorage, null)) {
				IDigitalStorageHandler h = te.getCapability(CoCore.digitalStorage, null);
				for (DigitalVolume c : h.getContents()) {
					if (c instanceof ManagedDigitalVolume) {
						contents.add((ManagedDigitalVolume<?>)c);
					}
				}
			}
		}
		contents.sort(new ContentsComparator());
	}

	public void updateConsumptionRate(int change) {
		consumedPerTick += change;
		if (consumedPerTick > 320) {
			error = true;
			errorReason = "too_much_power";
		} else {
			if (error && "too_much_power".equals(errorReason)) {
				error = false;
				errorReason = null;
			}
		}
	}

	public <T> T addToNetwork(T t) {
		if (t == null) return null;
		for (ManagedDigitalVolume<?> c : contents) {
			if (c.canHandle(t)) {
				t = ((ManagedDigitalVolume<T>)c).insert(t);
				if (t == null) break;
			}
		}
		if (t != null) {
			for (TileEntityWirelessReceiver r : members(TileEntityWirelessReceiver.class, receivers)) {
				TileEntityController cont = r.getTransmitterController();
				if (cont != null) {
					t = cont.addToNetwork(t);
				}
			}
		}
		changeId++;
		return t;
	}

	public <T> RemoveResult<T> removeFromNetwork(T t, int amount) {
		if (t == null) return new RemoveResult<T>(null, amount);
		for (ManagedDigitalVolume<?> c : contents) {
			if (c.canHandle(t)) {
				RemoveResult<T> res = ((ManagedDigitalVolume<T>)c).remove(t, amount);
				t = res.getThing();
				amount = res.getRemaining();
				if (amount <= 0) break;
			}
		}
		if (amount > 0) {
			for (TileEntityWirelessReceiver r : members(TileEntityWirelessReceiver.class, receivers)) {
				TileEntityController cont = r.getTransmitterController();
				if (cont != null) {
					RemoveResult<T> res = cont.removeFromNetwork(t, amount);
					t = res.getThing();
					amount = res.getRemaining();
					if (amount <= 0) break;
				}
			}
		}
		changeId++;
		return new RemoveResult<T>(t, amount);
	}

	/**
	 * Gets all contents of the specified type (ItemStack, FluidStack, StoredMonster, StoredPhysicalSpace, etc.)
	 * 
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getContents(Class<T> type) {
		List<T> li = Lists.newArrayList();
		for (ManagedDigitalVolume<?> c : contents) {
			if (c.canHandle(type)) {
				for(T t : ((ManagedDigitalVolume<T>)c).getContents()) {
					li.add(t);
				}
			}
		}
		for (TileEntityWirelessReceiver r : members(TileEntityWirelessReceiver.class, receivers)) {
			TileEntityController cont = r.getTransmitterController();
			if (cont != null) {
				li.addAll(cont.getContents(type));
			}
		}
		return li;
	}

	public void onNetworkPatched(TileEntityNetworkMember tenm) {
		if (networkMembers.isEmpty()) return;
		if (tenm instanceof TileEntityWirelessReceiver) {
			if (!receivers.contains(tenm.getPos())) {
				receivers.add(tenm.getPos());
				checkInfiniteLoop();
				changeId++;
			}
		}
		if (tenm.hasCapability(CoCore.digitalStorage, null)) {
			for (DigitalVolume<?> c : tenm.getCapability(CoCore.digitalStorage, null).getContents()) {
				if (c instanceof ManagedDigitalVolume) {
					contents.add((ManagedDigitalVolume<?>)c);
				}
			}
		}
		if (networkMembers.add(tenm.getPos())) {
			if (networkMembers.size() > 100) {
				error = true;
				errorReason = "network_too_big";
				consumedPerTick = 320;
			}
		}
	}

	public boolean knowsOfMemberAt(BlockPos pos) {
		return networkMembers.contains(pos);
	}

}
