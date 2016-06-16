package copo.api;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

@ParametersAreNonnullByDefault
public interface CapabilityInjector {
	void addCapability(ResourceLocation key, ICapabilityProvider cap);
}
