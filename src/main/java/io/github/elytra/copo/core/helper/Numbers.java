package io.github.elytra.copo.core.helper;

import java.text.NumberFormat;

import net.minecraft.client.resources.I18n;

public final class Numbers {
	public static final int GIBIBYTE = 1024*1024*1024;
	public static final int MIBIBYTE = 1024*1024;
	public static final int KIBIBYTE = 1024;
	public static String humanReadableBytes(int bytes) {
		if (bytes == 1) return I18n.format("numbers.correlated.byte");
		if (bytes >= GIBIBYTE) {
			return I18n.format("numbers.correlated.gibibytes", bytes/GIBIBYTE);
		} else if (bytes >= MIBIBYTE) {
			return I18n.format("numbers.correlated.mibibytes", bytes/MIBIBYTE);
		} else if (bytes >= KIBIBYTE) {
			return I18n.format("numbers.correlated.kibibytes", bytes/KIBIBYTE);
		}
		return I18n.format("numbers.correlated.bytes", bytes);
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
			return I18n.format("numbers.correlated.giga", formatter.format((double)count/GIGA));
		} else if (count >= MEGA) {
			return I18n.format("numbers.correlated.mega", formatter.format((double)count/MEGA));
		} else if (count >= 10_000) {
			return I18n.format("numbers.correlated.kilo", formatter.format((double)count/KILO));
		}
		return I18n.format("numbers.correlated.normal", count);
	}
	
	private Numbers() {}
}
