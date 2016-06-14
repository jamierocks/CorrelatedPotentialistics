package io.github.elytra.copo.core.compat;

import java.util.List;

import io.github.elytra.copo.core.CoCore;
import io.github.elytra.copo.core.IDigitalStorageHandler;
import io.github.elytra.copo.core.ITrackedDigitalStorageHandler;
import io.github.elytra.copo.core.helper.Numbers;
import io.github.elytra.copo.core.tile.TileEntityController;
import io.github.elytra.copo.core.tile.TileEntityInterface;
import io.github.elytra.copo.core.tile.TileEntityNetworkMember;
import io.github.elytra.copo.wireless.CoWireless;
import io.github.elytra.copo.wireless.block.BlockWirelessEndpoint;
import io.github.elytra.copo.wireless.block.BlockWirelessEndpoint.Kind;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CoPoWailaProvider implements IWailaDataProvider {

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound nbt, World world, BlockPos pos) {
		if (te instanceof TileEntityController) {
			TileEntityController tec = (TileEntityController)te;
			nbt.setInteger("Energy", tec.getEnergyStored(EnumFacing.UP));
			nbt.setInteger("MaxEnergy", tec.getMaxEnergyStored(EnumFacing.UP));
			if (tec.error && tec.errorReason != null) {
				nbt.setString("ErrorReason", tec.errorReason);
			} else if (tec.booting) {
				nbt.setInteger("BootTicks", tec.bootTicks);
			}
		}
		if (te instanceof TileEntityNetworkMember) {
			nbt.setInteger("EnergyPerTick", ((TileEntityNetworkMember) te).getEnergyConsumedPerTick());
			nbt.setBoolean("HasController", ((TileEntityNetworkMember) te).hasController());
		}
		return nbt;
	}

	@Override
	public List<String> getWailaBody(ItemStack stack, List<String> body, IWailaDataAccessor access, IWailaConfigHandler config) {
		NBTTagCompound nbt = access.getNBTData();
		if (access.getBlock() == CoCore.controller) {
			if (nbt.hasKey("ErrorReason")) {
				body.add("\u00A7c"+I18n.format("tooltip."+CoCore.MODID+".controller_error."+nbt.getString("ErrorReason")));
			} else if (nbt.hasKey("BootTicks") && nbt.getInteger("Energy") >= nbt.getInteger("EnergyPerTick")) {
				int bootTicks = nbt.getInteger("BootTicks");
				if (bootTicks < 0) {
					body.add("\u00A7a"+I18n.format("tooltip."+CoCore.MODID+".controller_booting.hard"));
				} else {
					body.add("\u00A7a"+I18n.format("tooltip."+CoCore.MODID+".controller_booting"));
				}
				int seconds;
				if (bootTicks >= 0) {
					seconds = (100-bootTicks)/20;
				} else {
					seconds = ((bootTicks*-1)+100)/20;
				}
				if (seconds == 1) {
					body.add("\u00A7a"+I18n.format("tooltip."+CoCore.MODID+".controller_boot_eta_one"));
				} else {
					body.add("\u00A7a"+I18n.format("tooltip."+CoCore.MODID+".controller_boot_eta", seconds));
				}
			}
			body.add(I18n.format("tooltip."+CoCore.MODID+".controller_consumption_rate", nbt.getInteger("EnergyPerTick")));
			body.add(I18n.format("tooltip."+CoCore.MODID+".controller_energy_buffer", nbt.getInteger("Energy"), nbt.getInteger("MaxEnergy")));
		} else if (access.getTileEntity() instanceof TileEntityNetworkMember) {
			if (nbt.getBoolean("HasController")) {
				body.add(I18n.format("tooltip."+CoCore.MODID+".member_consumption_rate", nbt.getInteger("EnergyPerTick")));
			} else {
				body.add("\u00A7c"+I18n.format("tooltip."+CoCore.MODID+".no_controller"));
			}
		}
		if (access.getTileEntity().hasCapability(CoCore.DIGITAL_STORAGE, null)) {
			IDigitalStorageHandler idsh = access.getTileEntity().getCapability(CoCore.DIGITAL_STORAGE, null);
			int totalBytesUsed = 0;
			int totalMaxBytes = 0;
			int totalTypesUsed = 0;
			int totalMaxTypes = 0;
			int driveCount = 0;
			
			if (idsh instanceof ITrackedDigitalStorageHandler) {
				ITrackedDigitalStorageHandler itdsh = ((ITrackedDigitalStorageHandler) idsh);
				totalBytesUsed += itdsh.getBits()/8;
				totalMaxBytes += itdsh.getMaxBits()/8;
				totalTypesUsed += itdsh.getTypes();
				totalMaxTypes += itdsh.getMaxTypes();
			}

			int totalTypesPercent = (int)(((double)totalTypesUsed/(double)totalMaxTypes)*100);
			int totalBytesPercent = (int)(((double)totalBytesUsed/(double)totalMaxBytes)*100);

			body.add(I18n.format("tooltip.correlated.drive_count", driveCount));
			body.add(I18n.format("tooltip.correlated.types_used", totalTypesUsed, totalMaxTypes, totalTypesPercent));
			body.add(I18n.format("tooltip.correlated.bytes_used", Numbers.humanReadableBytes(totalBytesUsed), Numbers.humanReadableBytes(totalMaxBytes), totalBytesPercent));
		} else if (access.getTileEntity() instanceof TileEntityInterface) {
			TileEntityInterface tei = (TileEntityInterface)access.getTileEntity();
			EnumFacing side = access.getSide();
			body.add(I18n.format("tooltip.correlated.side", I18n.format("direction.correlated."+side.getName())));
			body.add(I18n.format("tooltip.correlated.mode", I18n.format("tooltip.correlated.iface.mode_"+tei.getModeForFace(side).getName())));
		}
		return body;
	}

	@Override
	public List<String> getWailaHead(ItemStack stack, List<String> head, IWailaDataAccessor access, IWailaConfigHandler config) {
		return head;
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor access, IWailaConfigHandler config) {
		if (access.getBlock() == CoCore.controller) {
			return new ItemStack(access.getBlock(), 1, access.getMetadata());
		} else if (access.getBlock() == CoWireless.wireless_endpoint) { // TODO decouple
			return new ItemStack(access.getBlock(), 1, access.getBlockState().getValue(BlockWirelessEndpoint.kind) == Kind.RECEIVER ? 0 : 1);
		} else {
			return new ItemStack(access.getBlock());
		}
	}

	@Override
	public List<String> getWailaTail(ItemStack stack, List<String> tail, IWailaDataAccessor access, IWailaConfigHandler config) {
		return tail;
	}

}
