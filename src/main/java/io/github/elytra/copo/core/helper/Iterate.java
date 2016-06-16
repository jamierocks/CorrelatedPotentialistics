package io.github.elytra.copo.core.helper;

import java.util.Iterator;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class Iterate {

	public interface Over<T> {
		void visit(Iterator<T> iter, T t);
	}
	
	public interface OverNBT<T extends NBTBase> {
		void visit(int i, T t);
	}

	public static <T> void over(Iterable<T> types, Over<T> over) {
		over(types.iterator(), over);
	}
	
	public static <T> void over(Iterator<T> types, Over<T> over) {
		while (types.hasNext()) {
			over.visit(types, types.next());
		}
	}

	public static void overCompoundList(NBTTagList tagList, OverNBT<NBTTagCompound> over) {
		for (int i = 0; i < tagList.tagCount(); i++) {
			over.visit(i, tagList.getCompoundTagAt(i));
		}
	}
	
	private Iterate() {}

}
