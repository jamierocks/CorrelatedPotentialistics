package io.github.elytra.copo.core.tile;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.google.common.base.Enums;
import com.google.common.collect.Maps;

import copo.api.Content;
import copo.api.DigitalStorage;
import copo.api.ManagedContent;
import copo.api.RemoveResult;
import gnu.trove.map.hash.TCustomHashMap;
import io.github.elytra.copo.core.CoCore;
import io.github.elytra.copo.core.IDigitalStorageHandler;
import io.github.elytra.copo.core.ProtoStackHashingStrategy;
import io.github.elytra.copo.items.CoItems;
import io.github.elytra.copo.items.ItemDigitalStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class TileEntityInterface extends TileEntityNetworkMember implements IInventory, ISidedInventory, ITickable, IDigitalStorageHandler {
	public enum FaceMode implements IStringSerializable {
		PASSIVE,
		ACTIVE_PULL,
		ACTIVE_PUSH,
		DISABLED;
		public boolean canInsert() {
			return this == FaceMode.PASSIVE || this == ACTIVE_PULL;
		}

		public boolean canExtract() {
			return this == FaceMode.PASSIVE || this == ACTIVE_PUSH;
		}

		@Override
		public String getName() {
			switch (this) {
				case ACTIVE_PULL: return "pull";
				case ACTIVE_PUSH: return "push";
				case PASSIVE: return "none";
				case DISABLED: return "disabled";
				default: return "unknown";
			}
		}

		@Override
		public String toString() {
			return getName();
		}
	}
	
	/*
	 * this is honestly kind of a hack, but it's better than hardcoded special
	 * cases for interfaces in the remove/add code
	 */
	public class InterfaceContent extends ManagedContent<ItemStack> {

		public InterfaceContent() {
			super(null, null);
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
		public ItemStack insert(ItemStack u) {
			return u; // no
		}

		@Override
		public RemoveResult<ItemStack> remove(ItemStack u, int amount) {
			int slot = findSlot(TileEntityInterface.this, u, 9, 18);
			if (slot == -1) {
				return new RemoveResult<ItemStack>(u, amount);
			}
			ItemStack content = getStackInSlot(slot);
			int amt = Math.min(amount, content.stackSize);
			content.stackSize -= amt;
			if (content.stackSize <= 0) {
				content = null;
			}
			u.stackSize += amt;
			setInventorySlotContents(slot, content);
			return new RemoveResult<ItemStack>(u, amount);
		}

		@Override
		public Collection<ItemStack> getTypes() {
			Map<ItemStack, ItemStack> prototypes = new TCustomHashMap<>(new ProtoStackHashingStrategy());
			for (int i = 9; i < 18; i++) {
				ItemStack content = getStackInSlot(i);
				if (content != null && content.stackSize > 0) {
					if (prototypes.containsKey(content)) {
						prototypes.get(content).stackSize += content.stackSize;
					} else {
						ItemStack proto = content.copy();
						proto.stackSize = 0;
						prototypes.put(proto, content);
					}
				}
			}
			return prototypes.values();
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {}

		@Override
		public void readFromNBT(NBTTagCompound tag) {}

		@Override
		public int getAmountStored(ItemStack u) {
			int amt = 0;
			for (int i = 9; i < 18; i++) {
				ItemStack content = getStackInSlot(i);
				if (content != null && content.stackSize > 0) {
					amt += content.stackSize;
				}
			}
			return amt;
		}

	}
	
	private final InventoryBasic inv = new InventoryBasic("container.interface", false, 18);
	private final ItemStack[] prototypes = new ItemStack[9];
	private final List<Content<ItemStack>> contents;
	
	private final Map<EnumFacing, IItemHandler> itemHandlers = Maps.newHashMap();
	
	private final FaceMode[] modes = new FaceMode[6];

	public TileEntityInterface() {
		contents = Collections.singletonList(new InterfaceContent());
		for (EnumFacing ef : EnumFacing.VALUES) {
			itemHandlers.put(ef, new SidedInvWrapper(this, ef));
		}
		itemHandlers.put(null, new InvWrapper(this));
	}
	
	public FaceMode getModeForFace(EnumFacing face) {
		if (face == null) return null;
		FaceMode mode = modes[face.ordinal()];
		if (mode == null) {
			modes[face.ordinal()] = FaceMode.PASSIVE;
			mode = FaceMode.PASSIVE;
			markDirty();
		}
		return mode;
	}

	public void setModeForFace(EnumFacing face, FaceMode mode) {
		if (face == null) return;
		modes[face.ordinal()] = mode;
		markDirty();
	}

	public ItemStack getOutputPrototype(int i) {
		return prototypes[i];
	}

	public void setOutputPrototype(int i, ItemStack prototype) {
		prototypes[i] = prototype;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		writeFacesToNBT(compound);
		NBTTagList inv = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); i++) {
			ItemStack is = getStackInSlot(i);
			if (is != null) {
				NBTTagCompound nbt = is.writeToNBT(new NBTTagCompound());
				nbt.setInteger("Slot", i);
				inv.appendTag(nbt);
			}
		}
		compound.setTag("Buffer", inv);
		NBTTagList proto = new NBTTagList();
		for (int i = 0; i < prototypes.length; i++) {
			ItemStack is = prototypes[i];
			if (is != null) {
				NBTTagCompound nbt = is.writeToNBT(new NBTTagCompound());
				nbt.setInteger("Slot", i);
				nbt.removeTag("Count");
				proto.appendTag(nbt);
			}
		}
		compound.setTag("Prototypes", proto);
		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readFacesFromNBT(compound);
		NBTTagList inv = compound.getTagList("Buffer", NBT.TAG_COMPOUND);
		for (int i = 0; i < inv.tagCount(); i++) {
			NBTTagCompound nbt = inv.getCompoundTagAt(i);
			setInventorySlotContents(nbt.getInteger("Slot"), ItemStack.loadItemStackFromNBT(nbt));
		}
		NBTTagList proto = compound.getTagList("Prototypes", NBT.TAG_COMPOUND);
		for (int i = 0; i < proto.tagCount(); i++) {
			NBTTagCompound nbt = proto.getCompoundTagAt(i);
			nbt.setInteger("Count", 1);
			prototypes[nbt.getInteger("Slot")] = ItemStack.loadItemStackFromNBT(nbt);
		}
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("x", getPos().getX());
		nbt.setInteger("y", getPos().getY());
		nbt.setInteger("z", getPos().getZ());
		writeFacesToNBT(nbt);
		return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		readFacesFromNBT(pkt.getNbtCompound());
		worldObj.markBlockRangeForRenderUpdate(getPos(), getPos());
	}

	private void writeFacesToNBT(NBTTagCompound nbt) {
		for (EnumFacing face : EnumFacing.VALUES) {
			nbt.setString("Mode-"+face.getName(), getModeForFace(face).name());
		}
	}

	private void readFacesFromNBT(NBTTagCompound nbt) {
		for (EnumFacing face : EnumFacing.VALUES) {
			setModeForFace(face, Enums.getIfPresent(FaceMode.class, nbt.getString("Mode-"+face.getName())).or(FaceMode.PASSIVE));
		}
	}

	@Override
	public void update() {
		if (hasController() && hasWorldObj() && !worldObj.isRemote && worldObj.getTotalWorldTime() % 16 == 0) {
			TileEntityController controller = getController();
			if (!controller.isPowered() || controller.booting || controller.error) return;
			for (int i = 0; i <= 8; i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if (stack != null) {
					inv.setInventorySlotContents(i, controller.addToNetwork(stack));
				}
			}
			for (int i = 9; i <= 17; i++) {
				ItemStack prototype = prototypes[i-9];
				if (prototype != null) {
					ItemStack cur = inv.getStackInSlot(i);
					int needed;
					if (cur == null) {
						needed = prototype.getMaxStackSize();
						cur = prototype.copy();
						cur.stackSize = 0;
					} else {
						if (!ItemStack.areItemsEqual(cur, prototype) || !ItemStack.areItemStackTagsEqual(cur, prototype)) {
							continue;
						}
						needed = prototype.getMaxStackSize()-cur.stackSize;
					}
					if (needed > 0) {
						RemoveResult<ItemStack> result = controller.removeFromNetwork(prototype, needed);
						if (result.getThing() != null) {
							cur.stackSize += result.getThing().stackSize;
						}
						if (cur.stackSize <= 0) {
							inv.setInventorySlotContents(i, null);
						} else {
							inv.setInventorySlotContents(i, cur);
						}
					}
				}
			}
			for (EnumFacing face : EnumFacing.VALUES) {
				FaceMode mode = getModeForFace(face);
				if (mode == FaceMode.DISABLED || mode == FaceMode.PASSIVE) continue;
				TileEntity other = worldObj.getTileEntity(getPos().offset(face));
				if (!(other instanceof IInventory)) continue;
				if (other instanceof ISidedInventory) {
					ISidedInventory sided = (ISidedInventory)other;
					int[] slots = sided.getSlotsForFace(face.getOpposite());
					if (slots != null && slots.length > 0) {
						if (mode == FaceMode.ACTIVE_PUSH) {
							for (int i = 9; i < 18; i++) {
								ItemStack content = getStackInSlot(i);
								if (content != null) {
									int slot = findSlot(sided, content, slots);
									if (slot != -1) {
										if (sided.canInsertItem(slot, content, face.getOpposite())) {
											transfer(this, i, sided, slot);
										}
									}
								}
							}
						} else if (mode == FaceMode.ACTIVE_PULL) {
							for (int s : slots) {
								ItemStack content = sided.getStackInSlot(s);
								if (content != null) {
									int slot = findSlot(this, content, 0, 9);
									if (slot != -1) {
										if (sided.canExtractItem(s, content, face.getOpposite())) {
											transfer(sided, s, this, slot);
										}
									}
								}
							}
						}
					}
				} else if (other instanceof IInventory) {
					IInventory inv = (IInventory)other;
					if (mode == FaceMode.ACTIVE_PUSH) {
						for (int i = 9; i < 18; i++) {
							ItemStack content = getStackInSlot(i);
							if (content != null) {
								int slot = findSlot(inv, content, 0, inv.getSizeInventory());
								if (slot != -1) {
									transfer(this, i, inv, slot);
								}
							}
						}
					} else if (mode == FaceMode.ACTIVE_PULL) {
						for (int s = 0; s < inv.getSizeInventory(); s++) {
							ItemStack content = inv.getStackInSlot(s);
							if (content != null) {
								int slot = findSlot(this, content, 0, 9);
								if (slot != -1) {
									transfer(inv, s, this, slot);
								}
							}
						}
					}
				}
			}
		}
	}

	private static void transfer(IInventory fromInv, int fromSlot, IInventory toInv, int toSlot) {
		ItemStack fromStack = fromInv.getStackInSlot(fromSlot);
		if (fromStack == null) return;
		int toTake = Math.min(fromStack.stackSize, Math.min(toInv.getInventoryStackLimit(), fromStack.getMaxStackSize()));
		ItemStack toStack = toInv.getStackInSlot(toSlot);
		if (toStack == null) {
			toStack = fromStack.splitStack(toTake);
		} else {
			toStack.stackSize += toTake;
			fromStack.stackSize -= toTake;
		}
		if (fromStack.stackSize <= 0) {
			fromInv.setInventorySlotContents(fromSlot, null);
		}
		toInv.setInventorySlotContents(toSlot, toStack);
	}

	private static int findSlot(IInventory inv, ItemStack a, int start, int end) {
		for (int i = start; i < end; i++) {
			ItemStack b = inv.getStackInSlot(i);
			if (b == null) {
				return i;
			} else if (b.stackSize < b.getMaxStackSize() && b.stackSize < inv.getInventoryStackLimit()
					&& ItemStack.areItemsEqual(a, b) && ItemStack.areItemStackTagsEqual(a, b)) {
				return i;
			}
		}
		return -1;
	}

	private static int findSlot(IInventory inv, ItemStack a, int[] slots) {
		for (int i : slots) {
			ItemStack b = inv.getStackInSlot(i);
			if (b == null) {
				return i;
			} else if (b.stackSize < b.getMaxStackSize() && b.stackSize < inv.getInventoryStackLimit()
					&& ItemStack.areItemsEqual(a, b) && ItemStack.areItemStackTagsEqual(a, b)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (facing == null && capability == CoCore.DIGITAL_STORAGE) {
			return true;
		} else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (facing == null & capability == CoCore.DIGITAL_STORAGE) {
			return (T)this;
		} else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T)itemHandlers.get(facing);
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	public int getEnergyConsumedPerTick() {
		return 12;
	}

	@Override
	public String getName() {
		return "container.correlated.interface";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentTranslation(getName());
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (getModeForFace(side) == FaceMode.DISABLED) return new int[0];
		if (getModeForFace(side) == FaceMode.ACTIVE_PULL) return new int[] {
				0, 1, 2, 3, 4, 5, 6, 7, 8
		};
		if (getModeForFace(side) == FaceMode.ACTIVE_PUSH) return new int[] {
				9, 10, 11, 12, 13, 14, 15, 16, 17
		};
		return new int[] {
				0,  1,  2,  3,  4,  5,  6,  7,  8, // in
				9, 10, 11, 12, 13, 14, 15, 16, 17 // out
		};
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return getModeForFace(direction).canInsert() && index >= 0 && index <= 8;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return getModeForFace(direction).canExtract() && index >= 9 && index <= 17;
	}

	@Override
	public int getSizeInventory() {
		return 18;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return inv.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return index < 9;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public Iterable<Content<?>> getContents() {
		return (Iterable<Content<?>>)contents;
	}

	@Override
	public <T> Iterable<Content<T>> getContent(DigitalStorage<T> storage) {
		// TODO decouple
		// the Interface should be a dummy block with capabilities added by
		// Correlated modules; so the Items module would add IItemHandler caps
		// onto the interface using a hook in DigitalStorage
		return storage == CoItems.itemDigitalStorage ? getRealContent(CoItems.itemDigitalStorage) : null;
	}

	private Iterable<Content<ItemStack>> getRealContent(DigitalStorage<ItemStack> storage) {
		return contents;
	}

}
