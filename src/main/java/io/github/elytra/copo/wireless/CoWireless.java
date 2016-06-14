package io.github.elytra.copo.wireless;

import io.github.elytra.copo.core.CoCore;
import io.github.elytra.copo.wireless.block.BlockWirelessEndpoint;
import io.github.elytra.copo.wireless.item.ItemWirelessTerminal;
import io.github.elytra.copo.wireless.tile.TileEntityWirelessReceiver;
import io.github.elytra.copo.wireless.tile.TileEntityWirelessTransmitter;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

@Mod(modid=CoWireless.MODID, name="Correlated Wirelesstics", version="@VERSION@",
	updateJSON="http://unascribed.com/update-check/correlated-potentialistics.json")
public class CoWireless {
	public static final String MODID = "correlated|wireless";
	
	@Instance(MODID)
	public static CoWireless inst;
	@SidedProxy(clientSide="io.github.elytra.copo.wireless.WirelessClientProxy", serverSide="io.github.elytra.copo.wireless.client.WirelessProxy")
	public static WirelessProxy proxy;
	
	public static BlockWirelessEndpoint wireless_endpoint;
	
	public static ItemWirelessTerminal wireless_terminal;
	
	
	public double defaultWirelessRange;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		defaultWirelessRange = CoCore.inst.config.getFloat("defaultWirelessRange", "Wireless", 64, 1, 65536, "The default radius of wireless transmitters, in blocks.");
		
		
		ItemStack processor = new ItemStack(CoCore.misc, 1, 0);
		ItemStack luminousTorch = new ItemStack(CoCore.misc, 1, 4);
		
		// Wireless Receiver
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(wireless_endpoint, 1, 0),
				" t ",
				"___",
				"ipi",
				'_', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
				'i', "ingotIron",
				't', luminousTorch,
				'p', processor
				));
		
		// Wireless Transmitter
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(wireless_endpoint, 1, 1),
				" t ",
				"iii",
				"ipi",
				'i', "ingotIron",
				't', luminousTorch,
				'p', processor
				));
		
		// Wireless Terminal
		GameRegistry.addRecipe(new ShapedOreRecipe(wireless_terminal,
				"r",
				"v",
				'r', new ItemStack(wireless_endpoint, 1, 0),
				'v', CoCore.vt
				));
				
		
		GameRegistry.registerTileEntity(TileEntityWirelessReceiver.class, CoWireless.MODID+":wireless_receiver");
		GameRegistry.registerTileEntity(TileEntityWirelessTransmitter.class, CoWireless.MODID+":wireless_transmitter");
	}
}
