package toops.tsteelworks.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import toops.tsteelworks.api.highoven.IFuelRegistry;
import toops.tsteelworks.api.highoven.IMixAgentRegistry;
import toops.tsteelworks.api.highoven.IMixAgentRegistry.IMixAgent;
import toops.tsteelworks.api.highoven.ISmeltingRegistry;
import toops.tsteelworks.common.blocks.logic.HighOvenLogic;

public class HighOvenContainer extends Container {
	private HighOvenLogic logic;

	public HighOvenContainer(InventoryPlayer inventoryplayer, HighOvenLogic highoven) {
		logic = highoven;

		/* HighOven Misc inventory */
		addSlotToContainer(new Slot(highoven, HighOvenLogic.SLOT_OXIDIZER, 55, 16)); // oxidizer
		addSlotToContainer(new Slot(highoven, HighOvenLogic.SLOT_REDUCER, 55, 34)); // reducer
		addSlotToContainer(new Slot(highoven, HighOvenLogic.SLOT_PURIFIER, 55, 52)); // purifier
		addSlotToContainer(new Slot(highoven, HighOvenLogic.SLOT_FUEL, 126, 52)); // fuel

		/* HighOven Smeltable inventory */
		for (int y = 0; y < highoven.getSmeltableInventory().getSizeInventory(); y++)
			addSlotToContainer(new TSActiveSlot(highoven, HighOvenLogic.SLOT_FIRST_MELTABLE + y, 28, 7 + (y * 18), y < 6));

		/* Player inventory */
		for (int column = 0; column < 3; column++)
			for (int row = 0; row < 9; row++)
				addSlotToContainer(new Slot(inventoryplayer, row + (column * 9) + 9, 54 + (row * 18), 84 + (column * 18)));

		/* Player hotbar */
		for (int column = 0; column < 9; column++)
			addSlotToContainer(new Slot(inventoryplayer, column, 54 + (column * 18), 142));
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return logic.isUseableByPlayer(entityplayer);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int slotStart, int slotEnd, boolean startFromEnd) {
		return super.mergeItemStack(stack, slotStart, slotEnd, startFromEnd);
	}

	@Override
	/**
	 * Transfers stack from sourceSlot to any other
	 *
	 * @param
	 */
	public ItemStack transferStackInSlot(EntityPlayer player, int sourceSlot) {
		final Slot slot = (Slot) inventorySlots.get(sourceSlot);
		if (slot == null || !slot.getHasStack()) return null;

		ItemStack sourceStack = slot.getStack();
		final ItemStack stack = sourceStack.copy();

		if (sourceSlot < logic.getSizeInventory()) { // is from TE inventory
			if (!mergeItemStack(sourceStack, logic.getSizeInventory(), inventorySlots.size(), true))
				return null;
		} else { // is from player inventory
			if (!mergeToTE(sourceStack)) return null;
		}

		if (sourceStack.stackSize == 0)
			slot.putStack(null);
		else
			slot.onSlotChanged();

		return stack;
	}

	private boolean mergeToTE(ItemStack sourceStack) {
		boolean merged = false;

		if (IFuelRegistry.INSTANCE.getFuel(sourceStack) != null) { // is fuel
			merged = mergeItemStack(sourceStack, HighOvenLogic.SLOT_FUEL, HighOvenLogic.SLOT_FUEL + 1, false);
		}

		if (sourceStack.stackSize == 0) return merged;

		// is mixAgent
		IMixAgent agent = IMixAgentRegistry.INSTANCE.getAgentData(sourceStack);
		if (agent == null) return merged;

		switch (agent.getType()) {
			case OXIDIZER:
				merged |= mergeItemStack(sourceStack, HighOvenLogic.SLOT_OXIDIZER, HighOvenLogic.SLOT_OXIDIZER + 1, false);
				break;
			case PURIFIER:
				merged |= mergeItemStack(sourceStack, HighOvenLogic.SLOT_PURIFIER, HighOvenLogic.SLOT_PURIFIER + 1, false);
				break;
			case REDUCER:
				merged |= mergeItemStack(sourceStack, HighOvenLogic.SLOT_REDUCER, HighOvenLogic.SLOT_REDUCER + 1, false);
		}

		if (sourceStack.stackSize == 0) return merged;

		if (ISmeltingRegistry.INSTANCE.getMeltable(sourceStack) != null) { // is smeltable
			merged |= mergeItemStack(sourceStack,
					HighOvenLogic.SLOT_FIRST_MELTABLE,
					HighOvenLogic.SLOT_FIRST_MELTABLE + logic.getSmeltableInventory().getSizeInventory(),
					false);
		}

		return merged;
	}

	@Override
	public void updateProgressBar(int id, int value) {
		if (id == 0)
			logic.setFuelBurnTime(value / 12);
	}

	public HighOvenLogic getLogic() {
		return logic;
	}
}
