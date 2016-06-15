package copo.api;

import java.util.List;

import io.github.elytra.copo.core.helper.Numbers;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class DigitalSlotClientData<T> {
	/**
	 * When called, any applicable transformations have already been applied.
	 * Render into a 16x16 area containing the icon for this slot.
	 * <p>
	 * <b>Do not render the stack size.</b> This will be handled by Correlated,
	 * based on your {@link #getAmount} method. If you want to
	 * apply transformations to the rendered stack size, that is where to do it.
	 * 
	 * @param t the stack to render
	 * @param partialTicks the current partial render ticks value
	 */
	public abstract void render(T t, float partialTicks);
	
	
	/**
	 * @return a String to be used for lexical sorting when the user chooses
	 * 		Name sorting mode. The return value should be the same between
	 * 		multiple calls if the contents of the slot do not change.
	 */
	public abstract String getNameForSorting();
	
	/**
	 * @return a list of strings to be used as the tooltip.
	 */
	public abstract List<String> getTooltip();
	
	/**
	 * @return the string you would like to be rendered in the slot's amount
	 * 		number. Return the empty string if you do not want the amount to be
	 * 		rendered. Otherwise, call {@link Numbers#humanReadableItemCount(int)}
	 * 		with the amount of items stored. If storing something such as fluid,
	 * 		it's your choice if you want to pass in the raw amount or divide it
	 * 		first.
	 */
	public abstract String getAmount();
}
