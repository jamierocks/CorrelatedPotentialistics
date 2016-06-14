package io.github.elytra.copo.core.helper;

import java.util.Comparator;

import com.google.common.primitives.Booleans;
import com.google.common.primitives.Ints;

import copo.api.DigitalStorage.Content;
import io.github.elytra.copo.core.item.ItemDrive;
import net.minecraft.item.ItemStack;

public class ContentsComparator implements Comparator<Content> {

	@Override
	public int compare(Content a, Content b) {
		return Ints.compare(a.getPriority(), b.getPriority());
	}

}
