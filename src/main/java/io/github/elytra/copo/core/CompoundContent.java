package io.github.elytra.copo.core;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import copo.api.Content;
import copo.api.ManagedContent;
import copo.api.RemoveResult;
import net.minecraft.nbt.NBTTagCompound;

public class CompoundContent extends ManagedContent<Object> {

	private final Iterable<ManagedContent<Object>> underlying;
	
	public CompoundContent(ManagedContent<Object>... underlying) {
		super(null, null);
		this.underlying = () -> {
			return Iterators.forArray(underlying);
		};
	}
	
	public CompoundContent(Iterable<ManagedContent<Object>> underlying) {
		super(null, null);
		this.underlying = underlying;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		Thread.dumpStack();
		CoCore.log.warn("writeToNBT called on CompoundContent");
		for (Content c : underlying) {
			c.writeToNBT(tag);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		Thread.dumpStack();
		CoCore.log.warn("readToNBT called on CompoundContent");
		for (Content c : underlying) {
			c.readFromNBT(tag);
		}
	}

	@Override
	public boolean canHandle(Object t) {
		return any(ManagedContent::canHandle, t);
	}

	@Override
	public boolean canHandle(Class<?> clazz) {
		return any(ManagedContent::canHandle, clazz);
	}

	@Override
	public Object insert(Object u) {
		for (ManagedContent<Object> c : underlying) {
			if (c.canHandle(u)) {
				u = c.insert(u);
			}
		}
		return u;
	}

	@Override
	public RemoveResult<Object> remove(Object u, int amount) {
		for (ManagedContent<Object> c : underlying) {
			if (c.canHandle(u)) {
				RemoveResult<Object> rr = c.remove(u, amount);
				amount = rr.getRemaining();
				u = rr.getThing();
			}
		}
		return new RemoveResult<Object>(u, amount);
	}

	@Override
	public Iterable<Object> getTypes() {
		return Iterables.concat(underlying);
	}

	@Override
	public int getAmountStored(Object u) {
		int amt = 0;
		for (ManagedContent<Object> c : underlying) {
			if (c.canHandle(u)) {
				amt += c.getAmountStored(c);
			}
		}
		return amt;
	}
	
	private <T> boolean any(BiFunction<ManagedContent<?>, T, Boolean> func, T arg) {
		for (ManagedContent<?> c : underlying) {
			if (func.apply(c, arg)) {
				return true;
			}
		}
		return false;
	}

}
