package io.github.elytra.copo.items;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import copo.api.DigitalStorage;
import copo.api.ManagedContent;
import copo.api.RemoveResult;
import copo.api.StorageAllocator;
import gnu.trove.map.hash.TCustomHashMap;
import io.github.elytra.copo.core.ProtoStackHashingStrategy;
import io.github.elytra.copo.core.helper.Iterate;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemDigitalStorage extends DigitalStorage<ItemStack> {
	/**
	 * Package-private because Content implementations are a, well,
	 * implementation detail. Nobody should rely on something being a specific
	 * instance of ItemContents — Use ManagedContent&lt;ItemStack&gt; instead.
	 */
	final static class ItemContents extends ManagedContent<ItemStack> {

		private final Map<ItemStack, ItemStack> types;
		private final Collection<ItemStack> view;
		
		public ItemContents(ItemDigitalStorage owner, StorageAllocator alloc) {
			super(owner, alloc);
			types = new TCustomHashMap<>(new ProtoStackHashingStrategy());
			view = Collections.unmodifiableCollection(types.values());
		}

		private ItemStack makePrototype(ItemStack in) {
			ItemStack out = in.copy();
			out.stackSize = 0;
			return out;
		}
		
		@Override
		public boolean canHandle(Object t) {
			return t instanceof ItemStack;
		}
		
		@Override
		public boolean canHandle(Class<?> clazz) {
			return ItemStack.class.isAssignableFrom(clazz);
		}
		
		@Override
		public ItemStack insert(ItemStack t) {
			ItemStack existing = types.get(t);
			if (existing != null) {
				int amt = alloc.allocateBits(t.stackSize);
				existing.stackSize += amt;
				t.stackSize -= amt;
			} else {
				int bits = alloc.allocateTypeAndBits(t.stackSize);
				if (bits > 0) {
					ItemStack copy = t.copy();
					copy.stackSize = bits;
					t.stackSize -= bits;
					types.put(makePrototype(copy), copy);
				}
			}
			return t.stackSize <= 0 ? null : t;
		}

		@Override
		public RemoveResult<ItemStack> remove(ItemStack t, int amount) {
			ItemStack existing = types.get(t);
			if (existing != null) {
				int amt = alloc.deallocateBits(Math.min(amount, existing.stackSize));
				amount -= amt;
				t.stackSize += amt;
				existing.stackSize -= amt;
				if (existing.stackSize <= 0) {
					types.remove(existing);
					alloc.assertDeallocateType();
				}
			}
			return new RemoveResult<ItemStack>(t, amount);
		}

		@Override
		public Collection<ItemStack> getTypes() {
			return view;
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			NBTTagList li = new NBTTagList();
			for (ItemStack is : types.values()) {
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
				types.put(makePrototype(stack), stack);
			});
		}

		@Override
		public int getAmountStored(ItemStack u) {
			return types.containsKey(u) ? types.get(u).stackSize : 0;
		}


	}

	@Override
	public ManagedContent<ItemStack> createContents(StorageAllocator alloc) {
		return new ItemContents(this, alloc);
	}
}
