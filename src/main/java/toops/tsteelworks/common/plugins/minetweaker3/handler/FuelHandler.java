package toops.tsteelworks.common.plugins.minetweaker3.handler;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import toops.tsteelworks.api.highoven.IFuelRegistry;

@ZenClass("mods.tsteelworks.highoven.fuels")
public class FuelHandler {
	@ZenMethod
	public static void addFuel(IItemStack fuel, int burnTime, int heatValue) {
		MineTweakerAPI.apply(new Add(fuel, burnTime, heatValue));
	}

	@ZenMethod
	public static void removeFuel(IItemStack fuel) {
		MineTweakerAPI.apply(new Remove(fuel));
	}

	private static class Add implements IUndoableAction {
		private final ItemStack fuel;
		private final int burnTime;
		private final int heatRate;

		public Add(IItemStack fuel, int burnTime, int heatRate) {
			this.fuel = (ItemStack) fuel.getInternal();
			this.burnTime = burnTime;
			this.heatRate = heatRate;
		}

		@Override
		public void apply() {
			IFuelRegistry.INSTANCE.addFuel(fuel, burnTime, heatRate);
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			IFuelRegistry.INSTANCE.removeFuel(fuel);
		}

		@Override
		public String describe() {
			return "Added " + fuel.getDisplayName() + " as valid High Oven fuel.";
		}

		@Override
		public String describeUndo() {
			return "Removed " + fuel.getDisplayName() + " as valid High Oven fuel.";
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}

	private static class Remove implements IUndoableAction {
		private final ItemStack fuel;
		private int burnTime;
		private int heatRate;

		public Remove(IItemStack stack) {
			this.fuel = (ItemStack) stack.getInternal();
		}

		@Override
		public void apply() {
			IFuelRegistry.IFuelData data = IFuelRegistry.INSTANCE.removeFuel(fuel);

			burnTime = data.getBurnTime();
			heatRate = data.getHeatRate();
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			IFuelRegistry.INSTANCE.addFuel(fuel, burnTime, heatRate);
		}

		@Override
		public String describeUndo() {
			return "Added " + fuel.getDisplayName() + " as valid High Oven fuel.";
		}

		@Override
		public String describe() {
			return "Removed " + fuel.getDisplayName() + " as valid High Oven fuel.";
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}
}