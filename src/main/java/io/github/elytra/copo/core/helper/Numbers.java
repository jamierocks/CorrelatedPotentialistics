package io.github.elytra.copo.core.helper;

import java.text.NumberFormat;

import io.github.elytra.copo.core.CoCore;
import net.minecraft.client.resources.I18n;

public class Numbers {
	public static final int GIBIBYTE = 1024*1024*1024;
	public static final int MIBIBYTE = 1024*1024;
	public static final int KIBIBYTE = 1024;
	public static String humanReadableBytes(int bytes) {
		if (bytes == 1) return I18n.format("numbers."+CoCore.MODID+".byte");
		if (bytes >= GIBIBYTE) {
			return I18n.format("numbers."+CoCore.MODID+".gibibytes", bytes/GIBIBYTE);
		} else if (bytes >= MIBIBYTE) {
			return I18n.format("numbers."+CoCore.MODID+".mibibytes", bytes/MIBIBYTE);
		} else if (bytes >= KIBIBYTE) {
			return I18n.format("numbers."+CoCore.MODID+".kibibytes", bytes/KIBIBYTE);
		}
		return I18n.format("numbers."+CoCore.MODID+".bytes", bytes);
	}

	public static final int GIGA = 1_000_000_000;
	public static final int MEGA = 1_000_000;
	public static final int KILO = 1_000;

	private static final NumberFormat formatter = NumberFormat.getNumberInstance();
	private static boolean formatterInitialized = false;
	public static String humanReadableItemCount(int count) {
		if (!formatterInitialized) {
			formatterInitialized = true;
			formatter.setMinimumFractionDigits(1);
			formatter.setMaximumFractionDigits(1);
		}
		if (count >= GIGA) {
			return I18n.format("numbers."+CoCore.MODID+".giga", formatter.format((double)count/GIGA));
		} else if (count >= MEGA) {
			return I18n.format("numbers."+CoCore.MODID+".mega", formatter.format((double)count/MEGA));
		} else if (count >= 10_000) {
			return I18n.format("numbers."+CoCore.MODID+".kilo", formatter.format((double)count/KILO));
		}
		return I18n.format("numbers."+CoCore.MODID+".normal", count);
	}
	
	private Numbers() {}
}
