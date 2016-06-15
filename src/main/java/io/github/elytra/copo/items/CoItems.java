package io.github.elytra.copo.items;

import copo.api.DigitalStorageRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid=CoItems.MODID, name="Correlated Itemistics", version="@VERSION@",
	updateJSON="http://unascribed.com/update-check/correlated-potentialistics.json")
public class CoItems {
	public static final String MODID = "correlated|items";
	
	@Instance(MODID)
	public static CoItems inst;
	
	public static ItemDigitalStorage itemDigitalStorage;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		itemDigitalStorage = new ItemDigitalStorage();
		itemDigitalStorage.setRegistryName("item");
		DigitalStorageRegistry.register(itemDigitalStorage);
	}
}
