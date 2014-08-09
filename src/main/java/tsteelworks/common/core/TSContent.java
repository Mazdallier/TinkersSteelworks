package tsteelworks.common.core;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.oredict.OreDictionary;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.LiquidCasting;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.modifiers.tools.ModInteger;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.tools.TinkerTools;
import tconstruct.world.TinkerWorld;
import tsteelworks.TSteelworks;
import tsteelworks.blocks.*;
import tsteelworks.blocks.logic.*;
import tsteelworks.entity.HighGolem;
import tsteelworks.entity.SteelGolem;
import tsteelworks.entity.projectile.EntityLimestoneBrick;
import tsteelworks.entity.projectile.EntityScorchedBrick;
import tsteelworks.items.TSArmorBasic;
import tsteelworks.items.TSFilledBucket;
import tsteelworks.items.TSManual;
import tsteelworks.items.TSMaterialItem;
import tsteelworks.items.blocks.*;
import tsteelworks.lib.ConfigCore;
import tsteelworks.lib.TSteelworksRegistry;
import tsteelworks.lib.crafting.AdvancedSmelting;
import tsteelworks.modifiers.tools.TSActiveOmniMod;

import java.util.List;

public class TSContent {
	public static Item materialsTS;
	public static Item bucketsTS;
	public static Item bookManual;
	public static Item helmetSteel;
	public static Item chestplateSteel;
	public static Item leggingsSteel;
	public static Item bootsSteel;

	public static Block highoven;
	public static Block scorchedSlab;
	public static Block limestoneBlock;
	public static Block limestoneSlab;
	public static Block cementBlock;
	public static Block machine;
	public static Block tsCharcoalBlock;
	public static Block dustStorageBlock;
	public static Block steamBlock;
	public static Block moltenLimestone;
	public static Block liquidCement;

	public static Fluid steamFluid;
	public static Fluid moltenLimestoneFluid;
	public static Fluid liquidCementFluid;

	public static ItemStack charcoalBlock;
	public static ItemStack thaumcraftAlumentum;
	public static ItemArmor.ArmorMaterial materialSteel;

	/**
	 * Content Constructor
	 */
	public TSContent() {
		registerItems();
		registerBlocks();
		registerFluids();
		setupCreativeTabs();
		registerModifiers();
	}

	/**
	 * Register Items
	 */
	public void registerItems() {
		materialsTS = new TSMaterialItem(ConfigCore.materials).setUnlocalizedName("tsteelworks.Materials");
		GameRegistry.registerItem(materialsTS, "Materials");
		TSteelworksRegistry.addItemStackToDirectory("scorchedBrick", new ItemStack(materialsTS, 1, 0));

		bookManual = new TSManual(ConfigCore.manual);
		GameRegistry.registerItem(bookManual, "tsteelManual");

		bucketsTS = new TSFilledBucket(ConfigCore.buckets);
		GameRegistry.registerItem(bucketsTS, "buckets");

		if (ConfigCore.enableSteelArmor) {
			materialSteel = EnumHelper.addArmorMaterial("STEEL", 25, new int[] {3, 7, 5, 3}, 10);
			materialSteel.customCraftingMaterial = TConstructRegistry.getItemStack("ingotSteel").getItem();
			helmetSteel = new TSArmorBasic(ConfigCore.steelHelmet, materialSteel, 0, "steel").setUnlocalizedName("tsteelworks.helmetSteel");
			chestplateSteel = new TSArmorBasic(ConfigCore.steelChestplate, materialSteel, 1, "steel").setUnlocalizedName("tsteelworks.chestplateSteel");
			leggingsSteel = new TSArmorBasic(ConfigCore.steelLeggings, materialSteel, 2, "steel").setUnlocalizedName("tsteelworks.leggingsSteel");
			bootsSteel = new TSArmorBasic(ConfigCore.steelBoots, materialSteel, 3, "steel").setUnlocalizedName("tsteelworks.bootsSteel");
			GameRegistry.registerItem(helmetSteel, "helmetSteel");
			GameRegistry.registerItem(chestplateSteel, "chestplateSteel");
			GameRegistry.registerItem(leggingsSteel, "leggingsSteel");
			GameRegistry.registerItem(bootsSteel, "bootsSteel");
		}
	}

	/**
	 * Register Blocks and TileEntities (Logic)
	 */
	public void registerBlocks() {
	    /* High Oven */
		highoven = new HighOvenBlock().setBlockName("HighOven");
		GameRegistry.registerBlock(highoven, HighOvenItemBlock.class, "HighOven");
		GameRegistry.registerTileEntity(HighOvenLogic.class, "TSteelworks.HighOven");
		GameRegistry.registerTileEntity(HighOvenDrainLogic.class, "TSteelworks.HighOvenDrain");
		GameRegistry.registerTileEntity(HighOvenDuctLogic.class, "TSteelworks.HighOvenDuct");
		GameRegistry.registerTileEntity(DeepTankLogic.class, "TSteelworks.DeepTank");
		GameRegistry.registerTileEntity(TSMultiServantLogic.class, "TSteelworks.Servants");

		/* Slabs */
		scorchedSlab = new ScorchedSlab().setBlockName("ScorchedSlab");
		scorchedSlab.stepSound = Block.soundTypeStone;
		GameRegistry.registerBlock(scorchedSlab, ScorchedSlabItemBlock.class, "ScorchedSlab");

        /* Machines */
		machine = new MachineBlock().setBlockName("Machine");
		GameRegistry.registerBlock(machine, MachineItemBlock.class, "Machine");
		GameRegistry.registerTileEntity(TurbineLogic.class, "TSteelworks.Machine");

        /* Raw Vanilla Materials */
		List<ItemStack> charcoalBlocks = OreDictionary.getOres("blockCharcoal");

		if (charcoalBlocks.isEmpty()) {
			tsCharcoalBlock = new TSBaseBlock(Material.rock, 5.0f, new String[] {"charcoal_block"}).setBlockName("tsteelworks.blocks.charcoal");
			Blocks.fire.setFireInfo(tsCharcoalBlock, 15, 30);

			GameRegistry.registerBlock(tsCharcoalBlock, "blockCharcoal");

			OreDictionary.registerOre("blockCharcoal", tsCharcoalBlock);

			charcoalBlock = new ItemStack(tsCharcoalBlock);
			GameRegistry.registerFuelHandler(new FuelHandler(charcoalBlock, 15000));
		} else {
			charcoalBlock = charcoalBlocks.get(0);
		}

		dustStorageBlock = new DustStorageBlock().setBlockName("tsteelworks.dustblock");
		GameRegistry.registerBlock(dustStorageBlock, DustStorageItemBlock.class, "dustStorage");

		limestoneBlock = new LimestoneBlock().setBlockName("Limestone");
		GameRegistry.registerBlock(limestoneBlock, LimestoneItemBlock.class, "Limestone");

		limestoneSlab = new LimestoneSlab().setBlockName("LimestoneSlab").setStepSound(Block.soundTypeStone);
		GameRegistry.registerBlock(limestoneSlab, LimestoneSlabItemBlock.class, "LimestoneSlab");

		cementBlock = new CementBlock().setBlockName("tsteelworks.cement").setStepSound(Block.soundTypeStone);
		GameRegistry.registerBlock(cementBlock, CementItemBlock.class, "Cement");
	}

	public void registerFluids() {
		LiquidCasting tableCasting = TConstructRegistry.getTableCasting();
		ItemStack bucket = new ItemStack(Items.bucket);

		/* Steam */
		steamFluid = FluidRegistry.getFluid("steam");
		if (steamFluid == null) {
			steamFluid = new Fluid("steam");
			steamFluid.setDensity(-1).setViscosity(5).setTemperature(1300).setGaseous(true);

			FluidRegistry.registerFluid(steamFluid);
		}

		if (!steamFluid.canBePlacedInWorld()) {
			steamBlock = new TSBaseFluid(steamFluid, Material.air, "liquid_steam").setBlockName("steam").setLightOpacity(0);
			GameRegistry.registerBlock(steamBlock, "steam");
		} else {
			steamBlock = steamFluid.getBlock();
		}

		ItemStack filledBucket = FluidContainerRegistry.fillFluidContainer(new FluidStack(steamFluid, 1000), bucket);
		if (filledBucket == null) {
			filledBucket = new ItemStack(bucketsTS, 1, 0);
			FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(steamFluid, 1000), filledBucket, bucket));
		}

		tableCasting.addCastingRecipe(filledBucket, new FluidStack(steamFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);

		/* Limestone */
		moltenLimestoneFluid = FluidRegistry.getFluid("limestone.molten");
		if (moltenLimestoneFluid == null) {
			moltenLimestoneFluid = new Fluid("limestone.molten").setLuminosity(12).setDensity(3000).setViscosity(6000).setTemperature(1300);

			FluidRegistry.registerFluid(moltenLimestoneFluid);
		}

		if (!moltenLimestoneFluid.canBePlacedInWorld()) {
			moltenLimestone = new TSBaseFluid(moltenLimestoneFluid, Material.lava, "liquid_limestone").setBlockName("molten.limestone");
			GameRegistry.registerBlock(moltenLimestone, "molten.limestone");

			FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenLimestoneFluid, 1000), new ItemStack(bucketsTS, 1, 1), bucket));
		} else {
			moltenLimestone = moltenLimestoneFluid.getBlock();
		}

		filledBucket = FluidContainerRegistry.fillFluidContainer(new FluidStack(moltenLimestoneFluid, 1000), bucket);
		if (filledBucket == null) {
			filledBucket = new ItemStack(bucketsTS, 1, 1);
			FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(moltenLimestoneFluid, 1000), filledBucket, bucket));
		}

		tableCasting.addCastingRecipe(filledBucket, new FluidStack(moltenLimestoneFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);

		/* Cement */
		liquidCementFluid = FluidRegistry.getFluid("cement.liquid");
		if (liquidCementFluid == null) {
			liquidCementFluid = new Fluid("cement.liquid").setLuminosity(0).setDensity(6000).setViscosity(6000).setTemperature(20);

			FluidRegistry.registerFluid(liquidCementFluid);
		}

		if (!liquidCementFluid.canBePlacedInWorld()) {
			liquidCement = new CementFluidBlock(liquidCementFluid, Material.air, "liquid_cement").setBlockName("liquid.cement");
			GameRegistry.registerBlock(liquidCement, "liquid.cement");

			FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(liquidCementFluid, 1000), new ItemStack(bucketsTS, 1, 2), bucket));
		} else {
			liquidCement = liquidCementFluid.getBlock();
		}

		filledBucket = FluidContainerRegistry.fillFluidContainer(new FluidStack(liquidCementFluid, 1000), bucket);
		if (filledBucket == null) {
			filledBucket = new ItemStack(bucketsTS, 1, 2);
			FluidContainerRegistry.registerFluidContainer(new FluidContainerData(new FluidStack(liquidCementFluid, 1000), filledBucket, bucket));
		}

		tableCasting.addCastingRecipe(filledBucket, new FluidStack(liquidCementFluid, FluidContainerRegistry.BUCKET_VOLUME), bucket, true, 10);
	}

	public void oreRegistry() {
		// Vanilla
		ensureOreIsRegistered("blockSand", new ItemStack(Blocks.sand));
		ensureOreIsRegistered("dustRedstone", new ItemStack(Items.redstone));
		ensureOreIsRegistered("dustGunpowder", new ItemStack(Items.gunpowder));
		ensureOreIsRegistered("dustSugar", new ItemStack(Items.sugar));
		ensureOreIsRegistered("coal", new ItemStack(Items.coal, 1, 0));
		OreDictionary.registerOre("fuelCoal", new ItemStack(Items.coal, 1, 0));
		ensureOreIsRegistered("fuelCharcoal", new ItemStack(Items.coal, 1, 1));
		ensureOreIsRegistered("itemClay", new ItemStack(Items.clay_ball));

		// TSteelworks
		OreDictionary.registerOre("blockGunpowder", new ItemStack(dustStorageBlock, 1, 0));
		OreDictionary.registerOre("blockSugar", new ItemStack(dustStorageBlock, 1, 1));
		OreDictionary.registerOre("blockLimestone", new ItemStack(limestoneBlock, 1, 0));

		// TConstuct
		OreDictionary.registerOre("blockGraveyardDirt", new ItemStack(TinkerTools.craftedSoil, 1, 3));
		// * Dual registry for smelting (slag) purposes (we need the ore prefix)
		OreDictionary.registerOre("oreberryIron", new ItemStack(TinkerWorld.oreBerries, 1, 0));
		OreDictionary.registerOre("oreberryCopper", new ItemStack(TinkerWorld.oreBerries, 1, 2));
		OreDictionary.registerOre("oreberryTin", new ItemStack(TinkerWorld.oreBerries, 1, 3));
		OreDictionary.registerOre("oreberryAluminum", new ItemStack(TinkerWorld.oreBerries, 1, 4));
		OreDictionary.registerOre("oreberryAluminium", new ItemStack(TinkerWorld.oreBerries, 1, 4));
		OreDictionary.registerOre("oreberryEssence", new ItemStack(TinkerWorld.oreBerries, 1, 5));
	}

	void ensureOreIsRegistered(String oreName, ItemStack is) {
		final int oreId = OreDictionary.getOreID(is);
		if (oreId == -1)
			OreDictionary.registerOre(oreName, is);
	}

	/**
	 * Register mixer materials
	 */
	public void registerMixerMaterials() {
		AdvancedSmelting.registerMixItem("dustGunpowder", HighOvenLogic.SLOT_OXIDIZER, 1, 33);
		AdvancedSmelting.registerMixItem("dustSulphur", HighOvenLogic.SLOT_OXIDIZER, 1, 29);
		AdvancedSmelting.registerMixItem("dustSaltpeter", HighOvenLogic.SLOT_PURIFIER, 1, 30);
		AdvancedSmelting.registerMixItem("dustSaltpetre", HighOvenLogic.SLOT_PURIFIER, 1, 30);
		AdvancedSmelting.registerMixItem("dustSugar", HighOvenLogic.SLOT_OXIDIZER, 1, 62);
		AdvancedSmelting.registerMixItem("fuelCoal", HighOvenLogic.SLOT_OXIDIZER, 1, 43);
		AdvancedSmelting.registerMixItem("coal", HighOvenLogic.SLOT_OXIDIZER, 1, 43);
		AdvancedSmelting.registerMixItem("dustCoal", HighOvenLogic.SLOT_OXIDIZER, 1, 37);
		AdvancedSmelting.registerMixItem("dyeLime", HighOvenLogic.SLOT_OXIDIZER, 1, 37);

		AdvancedSmelting.registerMixItem("dustRedstone", HighOvenLogic.SLOT_PURIFIER, 1, 65);
		AdvancedSmelting.registerMixItem("dustManganese", HighOvenLogic.SLOT_PURIFIER, 1, 47);
		AdvancedSmelting.registerMixItem("oreManganese", HighOvenLogic.SLOT_PURIFIER, 1, 51);
		AdvancedSmelting.registerMixItem("dustAluminum", HighOvenLogic.SLOT_PURIFIER, 1, 60);
		AdvancedSmelting.registerMixItem("dustAluminium", HighOvenLogic.SLOT_PURIFIER, 1, 60);
		AdvancedSmelting.registerMixItem("dyeWhite", HighOvenLogic.SLOT_PURIFIER, 1, 37);
		AdvancedSmelting.registerMixItem("oreberryEssence", HighOvenLogic.SLOT_PURIFIER, 1, 27);

		AdvancedSmelting.registerMixItem("blockSand", HighOvenLogic.SLOT_REDUCER, 1, 100);
		AdvancedSmelting.registerMixItem("hambone", HighOvenLogic.SLOT_REDUCER, 1, 73);
		AdvancedSmelting.registerMixItem("blockGraveyardDirt", HighOvenLogic.SLOT_REDUCER, 1, 59);
	}

	/**
	 * Initialize the Steelworks creative tab with an icon.
	 */
	private void setupCreativeTabs() {
		TSteelworksRegistry.SteelworksCreativeTab.init(TConstructRegistry.getItemStack("ingotSteel"));
	}

	public void createEntities() {
		EntityRegistry.registerModEntity(EntityScorchedBrick.class, "ScorchedBrick", 0, TSteelworks.instance, 32, 3, true);
		EntityRegistry.registerModEntity(EntityLimestoneBrick.class, "LimestoneBrick", 1, TSteelworks.instance, 32, 3, true);
		// TODO: Register with registerModEntity instead. We do this because registerModEntity does not seemingly add a mob spawner egg.
		EntityRegistry.registerGlobalEntityID(HighGolem.class, "HighGolem", EntityRegistry.findGlobalUniqueEntityId(), 0x171717, 0x614D3C);
		EntityRegistry.registerGlobalEntityID(SteelGolem.class, "SteelGolem", EntityRegistry.findGlobalUniqueEntityId(), 0x171717, 0x614D3C);
	}

	/**
	 * Make TSRecipes add all crafting recipes
	 */
	public void addCraftingRecipes() {
		TSRecipes.setupCrafting();
	}

	public void modIntegration() {
		if (TContent.thaumcraftAvailable) {
			Object objResource = TContent.getStaticItem("itemResource", "thaumcraft.common.config.ConfigItems");
			if (objResource != null) {
				TSteelworks.loginfo("Thaumcraft detected. Registering fuels.");
				thaumcraftAlumentum = new ItemStack((Item) objResource, 1, 0);
			}
		}
		// BlockCube and ItemCube not detected. :/ WTF railcraft?
        /*if (TSteelworks.railcraftAvailable)
        {
            Object objBlockCube = TContent.getStaticItem("BlockCube", "mods.railcraft.common.blocks.aesthetics.cube");
            if (objBlockCube != null)
            {
                TSteelworks.logger.info("Railcraft detected. Registering fuels.");
                railcraftBlockCoalCoke = new ItemStack((Item) objBlockCube, 1, 0);
            }
        }*/
	}

	@SuppressWarnings("static-access")
	void registerModifiers() {
		ToolBuilder tb = ToolBuilder.instance;
		ItemStack hopper = new ItemStack(Block.hopperBlock);
		ItemStack enderpearl = new ItemStack(Item.enderPearl);
		String local = StatCollector.translateToLocal("modifier.tool.vacuous");
		tb.registerToolMod(new ModInteger(new ItemStack[] {hopper, enderpearl}, 50, "Vacuous", 5, "\u00A7a", local));

		TConstructRegistry.registerActiveToolMod(new TSActiveOmniMod());
	}
}
