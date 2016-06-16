package copo.api.slot;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import io.github.elytra.copo.core.helper.Numbers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public abstract class DigitalSlotClientData<T> {
	protected DigitalSlot<T> slot;
	public DigitalSlotClientData(DigitalSlot<T> slot) {
		this.slot = slot;
	}
	
	/**
	 * When called, any applicable transformations have already been applied.
	 * Render into a 16x16 area containing the icon for this slot.
	 * <p>
	 * <b>Do not render the stack size.</b> This will be handled by Correlated,
	 * based on your {@link #getAmount} method. If you want to
	 * apply transformations to the rendered stack size, that is where to do it.
	 * 
	 * @param partialTicks the current partial render ticks value
	 */
	public abstract void render(float partialTicks);
	
	
	/**
	 * @return a String to be used for lexical sorting when the user chooses
	 * 		Name sorting mode. The return value should be the same between
	 * 		multiple calls if the contents of the slot do not change, and the
	 * 		user has not changed their language.
	 */
	public abstract String getNameForSorting();
	
	/**
	 * @param player the player retrieving the tooltip
	 * @param advancedTooltips {@code true} if the player has enabled advanced
	 * 		tooltips
	 * @return a list of strings to be used as the tooltip.
	 */
	public abstract List<String> getTooltip(EntityPlayer player, boolean advancedTooltips);
	
	/**
	 * @return the string you would like to be rendered in the slot's amount
	 * 		number. Return the empty string if you do not want the amount to be
	 * 		rendered. Otherwise, call {@link Numbers#humanReadableItemCount(int)}
	 * 		with the amount of items stored. If storing something such as fluid,
	 * 		it's your choice if you want to pass in the raw amount or divide it
	 * 		first.
	 */
	public abstract String getFormattedAmount();
	
	/**
	 * @return the raw amount, used for sorting when the user chooses Quantity
	 * 		sorting mode.
	 */
	public abstract int getAmountForSorting();
	
	/**
	 * @return the weight for sorting, high weights go toward the end of the
	 * 		list
	 */
	public int getSortWeight() { return 0; }
	
	/**
	 * @return {@code true} if this slot should only be visible when its type is
	 * 		specifically asked for
	 */
	public boolean hiddenByDefault() { return false; }
}
