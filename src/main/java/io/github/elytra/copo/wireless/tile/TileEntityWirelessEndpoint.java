package io.github.elytra.copo.wireless.tile;

import io.github.elytra.copo.core.tile.TileEntityNetworkMember;
import io.github.elytra.copo.wireless.CoWireless;
import io.github.elytra.copo.wireless.block.BlockWirelessEndpoint;
import io.github.elytra.copo.wireless.block.BlockWirelessEndpoint.State;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

public abstract class TileEntityWirelessEndpoint extends TileEntityNetworkMember implements ITickable {

	@Override
	public int getEnergyConsumedPerTick() {
		return 24;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos(), getPos().add(1,1,1));
	}
	
	@Override
	public void update() {
		if (hasWorldObj() && !worldObj.isRemote) {
			IBlockState state = getWorld().getBlockState(getPos());
			if (state.getBlock() == CoWireless.wireless_endpoint) {
				State newState;
				if (hasController() && getController().isPowered()) {
					newState = getCurrentState();
				} else {
					newState = State.DEAD;
				}
				if (newState != state.getValue(BlockWirelessEndpoint.state)) {
					getWorld().setBlockState(getPos(), state.withProperty(BlockWirelessEndpoint.state, newState));
				}
			}
		}
	}

	protected abstract State getCurrentState();
	
}
