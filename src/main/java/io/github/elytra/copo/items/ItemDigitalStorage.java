package io.github.elytra.copo.items;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

import com.google.common.collect.Lists;

import copo.api.DigitalStorage;
import copo.api.StorageAllocator;
import io.github.elytra.copo.core.helper.Iterate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemDigitalStorage extends DigitalStorage<ItemDigitalStorage.ItemContents> {
	public class ItemContents extends DigitalStorage.ManagedContent<ItemStack> {

		private List<ItemStack> types = Lists.newArrayList();
		private List<ItemStack> view = Collections.unmodifiableList(types);
		
		public ItemContents(StorageAllocator alloc) {
			super(alloc);
		}

		@Override
		public boolean canHandle(Object t) {
			return t instanceof ItemStack;
		}
		
		@Override
		public boolean canHandle(Class<?> clazz) {
			return ItemStack.class.isAssignableFrom(clazz);
		}
		
		private boolean prototypesMatch(ItemStack a, ItemStack b) {
			return ItemStack.areItemsEqual(a, b) && ItemStack.areItemStackTagsEqual(a, b);
		}
		
		@Override
		public ItemStack insert(ItemStack t) {
			boolean foundMatching = false;
			for (ItemStack is : types) {
				if (prototypesMatch(is, t)) {
					int amt = alloc.allocateBits(t.stackSize);
					is.stackSize += amt;
					t.stackSize -= amt;
					foundMatching = true;
					break;
				}
			}
			if (!foundMatching) {
				int bits = alloc.allocateTypeAndBits(t.stackSize);
				if (bits > 0) {
					ItemStack proto = t.copy();
					proto.stackSize = bits;
					t.stackSize -= bits;
					types.add(proto);
				}
			}
			return t.stackSize <= 0 ? null : t;
		}

		@Override
		public RemoveResult<ItemStack> remove(ItemStack t, int _amount) {
			MutableInt amount = new MutableInt(_amount);
			Iterate.over(types, (i, is) -> {
				if (prototypesMatch(is, t)) {
					int amt = alloc.deallocateBits(Math.min(amount.intValue(), is.stackSize));
					amount.subtract(amt);;
					t.stackSize += amt;
					is.stackSize -= amt;
					if (is.stackSize == 0) {
						i.remove();
						alloc.assertDeallocateType();
					}
				}
			});
			return new RemoveResult<ItemStack>(t, amount.intValue());
		}

		@Override
		public List<ItemStack> getTypes() {
			return view;
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			NBTTagList li = new NBTTagList();
			for (ItemStack is : types) {
				NBTTagCompound stackTag = is.writeToNBT(new NBTTagCompound());
				stackTag.setInteger("Count", is.stackSize);
				li.appendTag(stackTag);
			}
			tag.setTag("Contents", li);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			types.clear();
			Iterate.overCompoundList(tag.getTagList("Contents", NBT.TAG_COMPOUND), (i, stackTag) -> {
				ItemStack stack = ItemStack.loadItemStackFromNBT(stackTag);
				stack.stackSize = stackTag.getInteger("Count");
				types.add(stack);
			});
		}


	}

	@Override
	public ItemContents createContents(StorageAllocator alloc) {
		return new ItemContents(alloc);
	}
}
