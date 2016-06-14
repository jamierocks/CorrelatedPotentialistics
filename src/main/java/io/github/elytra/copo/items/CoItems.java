package io.github.elytra.copo.items;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;

@Mod(modid=CoItems.MODID, name="Correlated Itemistics", version="@VERSION@",
	updateJSON="http://unascribed.com/update-check/correlated-potentialistics.json")
public class CoItems {
	public static final String MODID = "correlated|items";
	
	@Instance(MODID)
	public static CoItems inst;
}
