package io.github.elytra.copo.core;

import java.util.Map;

import copo.api.DigitalStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.IForgeRegistry.AddCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistry.ClearCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistry.CreateCallback;

public class DigitalStorageCallbacks implements AddCallback<DigitalStorage>, ClearCallback<DigitalStorage>, CreateCallback<DigitalStorage> {
	public static final DigitalStorageCallbacks INSTANCE = new DigitalStorageCallbacks();

	@Override
	public void onCreate(Map<ResourceLocation, ?> slaveset) {
		
	}

	@Override
	public void onClear(Map<ResourceLocation, ?> slaveset) {
		
	}

	@Override
	public void onAdd(DigitalStorage obj, int id, Map<ResourceLocation, ?> slaveset) {
		
	}

}
