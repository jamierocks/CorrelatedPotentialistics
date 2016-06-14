package io.github.elytra.copo.core;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Throwables;

import copo.api.DigitalStorage;
import copo.api.DigitalStorage.Content;
import io.github.elytra.copo.core.block.BlockController;
import io.github.elytra.copo.core.block.BlockDriveBay;
import io.github.elytra.copo.core.block.BlockInterface;
import io.github.elytra.copo.core.block.BlockVT;
import io.github.elytra.copo.core.block.item.ItemBlockController;
import io.github.elytra.copo.core.block.item.ItemBlockDriveBay;
import io.github.elytra.copo.core.block.item.ItemBlockInterface;
import io.github.elytra.copo.core.block.item.ItemBlockVT;
import io.github.elytra.copo.core.compat.WailaCompatibility;
import io.github.elytra.copo.core.item.ItemDrive;
import io.github.elytra.copo.core.item.ItemMisc;
import io.github.elytra.copo.core.item.ItemWeldthrower;
import io.github.elytra.copo.core.network.CoPoGuiHandler;
import io.github.elytra.copo.core.network.SetSearchQueryMessage;
import io.github.elytra.copo.core.network.SetSlotSizeMessage;
import io.github.elytra.copo.core.network.StartWeldthrowingMessage;
import io.github.elytra.copo.core.tile.TileEntityController;
import io.github.elytra.copo.core.tile.TileEntityDriveBay;
import io.github.elytra.copo.core.tile.TileEntityInterface;
import io.github.elytra.copo.core.tile.TileEntityVT;
import io.github.elytra.copo.wireless.CoWirelessWorldData;
import io.github.elytra.copo.wireless.block.BlockWirelessEndpoint;
import io.github.elytra.copo.wireless.block.item.ItemBlockWirelessEndpoint;
import io.github.elytra.copo.wireless.item.ItemWirelessTerminal;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.PersistentRegistryManager;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid=CoCore.MODID, name="Correlated Core", version="@VERSION@",
	updateJSON="http://unascribed.com/update-check/correlated-potentialistics.json")
public class CoCore {
	public static final String MODID = "correlated|core";

	public static Logger log;

	@Instance(MODID)
	public static CoCore inst;
	@SidedProxy(clientSide="io.github.elytra.copo.core.client.ClientProxy", serverSide="io.github.elytra.copo.core.Proxy")
	public static CoreProxy proxy;

	public static BlockController controller;
	public static BlockDriveBay drive_bay;
	public static BlockVT vt;
	public static BlockInterface iface;

	public static ItemMisc misc;
	public static ItemDrive drive;
	public static ItemWeldthrower weldthrower;
	
	public static SoundEvent weldthrow;

	public static CreativeTabs creativeTab = new CreativeTabs(CoCore.MODID) {
		@Override
		public Item getTabIconItem() {
			return Item.getItemFromBlock(controller);
		}
	};

	@CapabilityInject(IDigitalStorageHandler.class)
	public static Capability<IDigitalStorageHandler> DIGITAL_STORAGE;

	public SimpleNetworkWrapper network;
	public Configuration config;
	
	public boolean easyProcessors;

	public static final FMLControlledNamespacedRegistry<DigitalStorage<?>> registry = PersistentRegistryManager.createRegistry(
				new ResourceLocation(MODID, "storage"), DigitalStorage.class, null, 0, 255, false,
				DigitalStorageCallbacks.INSTANCE, DigitalStorageCallbacks.INSTANCE, DigitalStorageCallbacks.INSTANCE);

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		log = LogManager.getLogger(CoCore.MODID);

		File configFolder = new File("config", "correlated");
		config = new Configuration(new File(configFolder, "core.cfg"));
		easyProcessors = config.getBoolean("easyProcessors", "Core", false, "If true, processors can be crafted without finding one in a dungeon.");
		
		CapabilityManager.INSTANCE.register(IDigitalStorageHandler.class, null, () -> null);
		
		network = NetworkRegistry.INSTANCE.newSimpleChannel("Correlated");
		network.registerMessage(SetSearchQueryMessage.class, SetSearchQueryMessage.class, 0, Side.SERVER);
		network.registerMessage(SetSearchQueryMessage.class, SetSearchQueryMessage.class, 1, Side.CLIENT);
		network.registerMessage(SetSlotSizeMessage.class, SetSlotSizeMessage.class, 2, Side.CLIENT);
		network.registerMessage(StartWeldthrowingMessage.class, StartWeldthrowingMessage.class, 3, Side.CLIENT);

		register(new BlockController().setHardness(2), ItemBlockController.class, "controller", 4);
		register(new BlockDriveBay().setHardness(2), ItemBlockDriveBay.class, "drive_bay", 0);
		register(new BlockVT().setHardness(2), ItemBlockVT.class, "vt", 0);
		register(new BlockInterface().setHardness(2), ItemBlockInterface.class, "iface", 0);
		register(new BlockWirelessEndpoint().setHardness(2), ItemBlockWirelessEndpoint.class, "wireless_endpoint", -4);

		register(new ItemMisc(), "misc", -2);
		register(new ItemDrive(), "drive", -1);
		register(new ItemWirelessTerminal(), "wireless_terminal", 0);
		register(new ItemWeldthrower(), "weldthrower", 0);
		
		ResourceLocation loc = new ResourceLocation(CoCore.MODID, "weldthrow");
		GameRegistry.register(weldthrow = new SoundEvent(loc), loc);

		CRecipes.register();

		GameRegistry.registerTileEntity(TileEntityController.class, CoCore.MODID+":controller");
		GameRegistry.registerTileEntity(TileEntityDriveBay.class, CoCore.MODID+":drive_bay");
		GameRegistry.registerTileEntity(TileEntityVT.class, CoCore.MODID+":vt");
		GameRegistry.registerTileEntity(TileEntityInterface.class, CoCore.MODID+":interface");
		if (Loader.isModLoaded("Waila")) {
			WailaCompatibility.init();
		}
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new CoPoGuiHandler());
		MinecraftForge.EVENT_BUS.register(this);
		proxy.preInit();
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e) {
		config.save();
		proxy.postInit();
	}
	
	@SubscribeEvent
	public void onLootAdd(LootTableLoadEvent e) {
		if (e.getName().toString().startsWith("minecraft:chests/")) {
			e.getTable().getPool("main").addEntry(new LootEntryItem(misc, 45, 0, new LootFunction[0], new LootCondition[0], CoCore.MODID+":processor"));
		}
	}
	
	public static CoWirelessWorldData getDataFor(World w) {
		CoWirelessWorldData data = (CoWirelessWorldData)w.getPerWorldStorage().getOrLoadData(CoWirelessWorldData.class, CoCore.MODID);
		if (data == null) {
			data = new CoWirelessWorldData(CoCore.MODID);
			w.getPerWorldStorage().setData(CoCore.MODID, data);
		}
		return data;
	}

	private void register(Block block, Class<? extends ItemBlock> item, String name, int itemVariants) {
		block.setUnlocalizedName(CoCore.MODID+"."+name);
		block.setCreativeTab(creativeTab);
		block.setRegistryName(name);
		GameRegistry.register(block);
		try {
			ItemBlock ib = item.getConstructor(Block.class).newInstance(block);
			ib.setRegistryName(name);
			GameRegistry.register(ib);
		} catch (Exception e1) {
			Throwables.propagate(e1);
		}
		proxy.registerItemModel(Item.getItemFromBlock(block), itemVariants);
		try {
			this.getClass().getField(name).set(this, block);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void register(Item item, String name, int variants) {
		item.setUnlocalizedName(CoCore.MODID+"."+name);
		item.setCreativeTab(creativeTab);
		item.setRegistryName(name);
		GameRegistry.register(item);
		proxy.registerItemModel(item, variants);
		try {
			this.getClass().getField(name).set(this, item);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Content> collectContents(ItemStack item, NBTTagCompound nbt) {
		return null;
	}

}
