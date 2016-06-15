package copo.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public interface CapabilityInjector {
	void addCapability(ResourceLocation key, ICapabilityProvider cap);
}
