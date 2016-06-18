package io.github.elytra.copo.core.helper;

import java.util.Comparator;

import com.google.common.primitives.Ints;

import copo.api.content.DigitalVolume;

public class ContentsComparator implements Comparator<DigitalVolume> {

	@Override
	public int compare(DigitalVolume a, DigitalVolume b) {
		return Ints.compare(a.getPriority(), b.getPriority());
	}

}
