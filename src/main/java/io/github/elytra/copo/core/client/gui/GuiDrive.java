package io.github.elytra.copo.core.client.gui;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import copo.api.content.ManagedContent;
import io.github.elytra.copo.core.CoCore;
import io.github.elytra.copo.core.DriveStorageManager;
import io.github.elytra.copo.core.helper.Numbers;
import io.github.elytra.copo.core.inventory.ContainerDrive;
import io.github.elytra.copo.core.item.ItemDrive.PartitioningMode;
import io.github.elytra.copo.core.item.ItemDrive.Priority;
import io.github.elytra.copo.items.CoItems;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiDrive extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation(CoCore.MODID, "textures/gui/container/drive_editor.png");
	private final ContainerDrive container;

	private final GuiButton priority;
	private final GuiButton partition;

	public GuiDrive(ContainerDrive container) {
		super(container);
		this.container = container;
		xSize = 212;
		ySize = 222;
		priority = new GuiButtonExt(0, 0, 0, 18, 18, "");
		partition = new GuiButtonExt(1, 0, 20, 18, 18, "");
	}

	@Override
	public void initGui() {
		super.initGui();
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		priority.xPosition = x+7;
		priority.yPosition = y+107;

		partition.xPosition = x+187;
		partition.yPosition = priority.yPosition;

		buttonList.add(priority);
		buttonList.add(partition);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == priority) {
			mc.playerController.sendEnchantPacket(container.windowId, 0);
		} else if (button == partition) {
			mc.playerController.sendEnchantPacket(container.windowId, 2);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (priority.isMouseOver() && mouseButton == 1) {
			priority.playPressSound(mc.getSoundHandler());
			mc.playerController.sendEnchantPacket(container.windowId, 1);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		GlStateManager.translate((width - xSize) / 2, (height - ySize) / 2, 0);
		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(0, 0, 0, 0, 212, 222);
		GlStateManager.popMatrix();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		FontRenderer renderer = container.getDrive().getItem().getFontRenderer(container.getDrive());
		if (renderer == null) {
			renderer = fontRendererObj;
		}
		DriveStorageManager dcp = container.getItemDrive().getStorage(container.getDrive());
		renderer.drawString(container.getDrive().getDisplayName(), 8, 6, 0x404040);
		fontRendererObj.drawString(I18n.format("gui.inventory"), 8, 128, 0x404040);
		GlStateManager.pushMatrix();
		GlStateManager.disableDepth();
		GlStateManager.scale(0.5f, 0.5f, 1);
		for (int i = 1; i < 65; i++) {
			Slot slot = container.inventorySlots.get(i);
			if (slot.getHasStack()) {
				ItemStack stack = slot.getStack();
				// TODO decouple
				int stored = ManagedContent.getTotalAmountStored(dcp.getContent(CoItems.itemDigitalStorage), stack);
				if (stored > 0) {
					String str = Numbers.humanReadableItemCount(stored);
					int x = slot.xDisplayPosition*2;
					int y = slot.yDisplayPosition*2;
					x += (32-mc.fontRendererObj.getStringWidth(str));
					y += (32-mc.fontRendererObj.FONT_HEIGHT);
					mc.fontRendererObj.drawStringWithShadow(str, x, y, -1);
				}
			}
		}
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();

		mc.getTextureManager().bindTexture(background);

		int color = container.getItemDrive().getBaseColor(container.getDrive());
		GlStateManager.color(((color >> 16)&0xFF)/255f, ((color >> 8)&0xFF)/255f, (color&0xFF)/255f);
		drawTexturedModalRect(195, 5, 222, 0, 10, 10);
		color = container.getItemDrive().getFullnessColor(container.getDrive());
		GlStateManager.color(((color >> 16)&0xFF)/255f, ((color >> 8)&0xFF)/255f, (color&0xFF)/255f);
		drawTexturedModalRect(195, 5, 212, 0, 10, 10);


		GlStateManager.color(1, 1, 1);
		GlStateManager.pushMatrix();
		GlStateManager.translate(-(width - xSize) / 2, -(height - ySize) / 2, 0);

		PartitioningMode part = container.getItemDrive().getPartitioningMode(container.getDrive());
		// TODO when blacklist is implemented, remove the 13 from the U calculation below
		drawTexturedModalRect(partition.xPosition+4, partition.yPosition+2, 246, 13+(part.ordinal()*13), 10, 13);

		Priority pri = container.getItemDrive().getPriority(container.getDrive());
		drawTexturedModalRect(priority.xPosition+4, priority.yPosition+2, 246, 39+(pri.ordinal()*13), 10, 13);
		if (partition.isMouseOver()) {
			List<String> li = Lists.newArrayList(
					I18n.format("gui."+CoCore.MODID+".partition_mode"),
					"\u00A77"+I18n.format("gui."+CoCore.MODID+".partition_mode."+part.lowerName)
					);
			drawHoveringText(li, mouseX, mouseY);
		}
		if (priority.isMouseOver()) {
			List<String> li = Lists.newArrayList(
					I18n.format("gui."+CoCore.MODID+".priority"),
					pri.color+I18n.format("gui."+CoCore.MODID+".priority."+pri.lowerName)
					);
			drawHoveringText(li, mouseX, mouseY);
		}
		GlStateManager.popMatrix();
	}

}
