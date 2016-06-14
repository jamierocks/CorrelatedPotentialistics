package io.github.elytra.copo.core.compat;

import io.github.elytra.copo.core.block.BlockController;
import io.github.elytra.copo.core.block.BlockDriveBay;
import io.github.elytra.copo.core.block.BlockInterface;
import io.github.elytra.copo.core.block.BlockVT;
import io.github.elytra.copo.core.tile.TileEntityNetworkMember;
import io.github.elytra.copo.wireless.block.BlockWirelessEndpoint;
import mcp.mobius.waila.api.impl.ModuleRegistrar;

public class WailaCompatibility {
	public static void init() {
		CoPoWailaProvider provider = new CoPoWailaProvider();
		ModuleRegistrar.instance().registerNBTProvider(provider, TileEntityNetworkMember.class);
		ModuleRegistrar.instance().registerStackProvider(provider, BlockController.class);
		ModuleRegistrar.instance().registerBodyProvider(provider, BlockController.class);

		ModuleRegistrar.instance().registerStackProvider(provider, BlockDriveBay.class);
		ModuleRegistrar.instance().registerBodyProvider(provider, BlockDriveBay.class);

		ModuleRegistrar.instance().registerStackProvider(provider, BlockVT.class);
		ModuleRegistrar.instance().registerBodyProvider(provider, BlockVT.class);

		ModuleRegistrar.instance().registerStackProvider(provider, BlockInterface.class);
		ModuleRegistrar.instance().registerBodyProvider(provider, BlockInterface.class);
		
		ModuleRegistrar.instance().registerStackProvider(provider, BlockWirelessEndpoint.class);
		ModuleRegistrar.instance().registerBodyProvider(provider, BlockWirelessEndpoint.class);
	}
}
