package io.github.elytra.copo.core;

import java.util.Map;

import copo.api.DigitalStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.IForgeRegistry.AddCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistry.ClearCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistry.CreateCallback;

public class DigitalStorageCallbacks implements AddCallback<DigitalStorage<?>>, ClearCallback<DigitalStorage<?>>, CreateCallback<DigitalStorage<?>> {
	private static final DigitalStorageCallbacks INSTANCE = new DigitalStorageCallbacks();
	
	// workaround for fiddly generics
	
	@SuppressWarnings("unchecked")
	public static <T> AddCallback<T> add() {
		return (AddCallback<T>)INSTANCE;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ClearCallback<T> clear() {
		return (ClearCallback<T>)INSTANCE;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> CreateCallback<T> create() {
		return (CreateCallback<T>)INSTANCE;
	}

	@Override
	public void onCreate(Map<ResourceLocation, ?> slaveset) {
		
	}

	@Override
	public void onClear(Map<ResourceLocation, ?> slaveset) {
		
	}

	@Override
	public void onAdd(DigitalStorage<?> obj, int id, Map<ResourceLocation, ?> slaveset) {
		
	}

}
