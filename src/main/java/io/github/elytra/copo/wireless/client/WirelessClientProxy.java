package io.github.elytra.copo.wireless.client;

import io.github.elytra.copo.core.CoCore;
import io.github.elytra.copo.wireless.CoWireless;
import io.github.elytra.copo.wireless.WirelessProxy;
import io.github.elytra.copo.wireless.client.render.RenderWirelessReceiver;
import io.github.elytra.copo.wireless.client.render.RenderWirelessTransmitter;
import io.github.elytra.copo.wireless.tile.TileEntityWirelessReceiver;
import io.github.elytra.copo.wireless.tile.TileEntityWirelessTransmitter;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WirelessClientProxy extends WirelessProxy {
	@SuppressWarnings("deprecation")
	@Override
	public void preInit() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWirelessReceiver.class, new RenderWirelessReceiver());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWirelessTransmitter.class, new RenderWirelessTransmitter());
		
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(CoWireless.wireless_endpoint), 0, TileEntityWirelessReceiver.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(CoWireless.wireless_endpoint), 1, TileEntityWirelessTransmitter.class);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	@Override
	public void postInit() {
	}
	
	@SubscribeEvent
	public void onStitch(TextureStitchEvent.Pre e) {
		e.getMap().registerSprite(new ResourceLocation(CoCore.MODID, "blocks/wireless_endpoint_error"));
		e.getMap().registerSprite(new ResourceLocation(CoCore.MODID, "blocks/wireless_endpoint_linked"));
	}
}
