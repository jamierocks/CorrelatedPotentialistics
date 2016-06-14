package io.github.elytra.copo.core;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class CRecipes {

	public static void register() {
		ItemStack processor = new ItemStack(CoCore.misc, 1, 0);
		ItemStack drivePlatterCeramic = new ItemStack(CoCore.misc, 1, 1);
		ItemStack drivePlatterMetallic = new ItemStack(CoCore.misc, 1, 2);
		ItemStack luminousPearl = new ItemStack(CoCore.misc, 1, 3);
		ItemStack luminousTorch = new ItemStack(CoCore.misc, 1, 4);

		// 1KiB Drive
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CoCore.drive, 1, 0),
				"III",
				"IOI",
				"IoI",
				'I', "ingotIron",
				'O', luminousPearl,
				'o', drivePlatterCeramic
				));

		// 4KiB Drive
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CoCore.drive, 1, 1),
				"III",
				"oOo",
				"IoI",
				'I', "ingotIron",
				'O', luminousPearl,
				'o', drivePlatterCeramic
				));

		// 16KiB Drive
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CoCore.drive, 1, 2),
				"III",
				"dOd",
				"IoI",
				'I', "ingotIron",
				'd', "gemDiamond",
				'O', luminousPearl,
				'o', drivePlatterMetallic
				));
		// 64KiB Drive
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CoCore.drive, 1, 3),
				"doO",
				"odo",
				"Ood",
				'd', "gemDiamond",
				'O', luminousPearl,
				'o', drivePlatterMetallic
				));

		// Void Drive
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CoCore.drive, 1, 4),
				"###",
				"#O#",
				"###",
				'O', luminousPearl,
				'#', Blocks.OBSIDIAN
				));


		// Enderic Processor
		if (CoCore.inst.easyProcessors) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CoCore.misc, 1, 0),
					"qdq",
					"gog",
					"qdq",
					'q', "gemQuartz",
					'g', "ingotGold",
					'd', "gemDiamond",
					'o', Items.ENDER_PEARL
					));
		} else {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(CoCore.misc, 2, 0),
					"qoq",
					"gpg",
					"qdq",
					'q', "gemQuartz",
					'g', "ingotGold",
					'd', "gemDiamond",
					'p', processor,
					'o', Items.ENDER_PEARL
					));
		}

		// Luminous Pearl
		GameRegistry.addRecipe(new ShapelessOreRecipe(luminousPearl,
				Items.ENDER_PEARL, "dustGlowstone"));

		// Ceramic Drive Platter
		GameRegistry.addRecipe(new ShapedOreRecipe(drivePlatterCeramic,
				" B ",
				"BiB",
				" B ",
				'B', "ingotBrick",
				'i', "ingotIron"
				));

		// Metallic Drive Platter
		GameRegistry.addRecipe(new ShapedOreRecipe(drivePlatterMetallic,
				"ioi",
				"oIo",
				"ioi",
				'o', drivePlatterCeramic,
				'i', "ingotIron",
				'I', "blockIron"
				));

		// Drive Bay
		GameRegistry.addRecipe(new ShapedOreRecipe(CoCore.drive_bay,
				"iii",
				" p ",
				"iii",
				'i', "ingotIron",
				'p', processor
				));

		// Controller
		GameRegistry.addRecipe(new ShapedOreRecipe(CoCore.controller,
				"ioi",
				"opo",
				"ioi",
				'i', "ingotIron",
				'p', processor,
				'o', luminousPearl
				));

		// Terminal
		GameRegistry.addRecipe(new ShapedOreRecipe(CoCore.vt,
				"iii",
				"ooo",
				"ipi",
				'i', "ingotIron",
				'p', processor,
				'o', luminousPearl
				));

		// Interface
		GameRegistry.addRecipe(new ShapedOreRecipe(CoCore.iface,
				"igi",
				"gog",
				"igi",
				'i', "ingotIron",
				'g', "ingotGold",
				'o', luminousPearl
				));
		
		// Luminous Torch
		GameRegistry.addRecipe(new ShapedOreRecipe(luminousTorch,
				"o",
				"i",
				"i",
				'i', "ingotIron",
				'o', luminousPearl
				));
		
		// Weldthrower Fuel
		GameRegistry.addShapelessRecipe(new ItemStack(CoCore.misc, 4, 5), luminousPearl);
		
		// Weldthrower
		GameRegistry.addRecipe(new ShapedOreRecipe(CoCore.weldthrower,
				"i  ",
				"ti_",
				"  i",
				'i', "ingotIron",
				'_', Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE,
				't', luminousTorch
				));
		
	}

}
