package vanillaautomated;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import vanillaautomated.blockentities.*;
import vanillaautomated.blocks.*;
import vanillaautomated.gui.*;

public class VanillaAutomatedBlocks {

    public static final FabricBlockSettings machineBlockSettings = FabricBlockSettings.of(Material.METAL, MaterialColor.GRAY).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL);

    // Block entities
    public static BlockEntityType<FisherBlockEntity> fisherBlockEntity;
    public static BlockEntityType<CobblestoneGeneratorBlockEntity> cobblestoneGeneratorBlockEntity;
    public static BlockEntityType<MagnetBlockEntity> magnetBlockEntity;
    public static BlockEntityType<NullifierBlockEntity> nullifierBlockEntity;
    public static BlockEntityType<TimerBlockEntity> timerBlockEntity;
    public static BlockEntityType<MobFarmBlockEntity> mobFarmBlockEntity;
    public static BlockEntityType<FarmerBlockEntity> farmerBlockEntity;
    public static BlockEntityType<CrusherBlockEntity> crusherBlockEntity;
    public static BlockEntityType<BreakerBlockEntity> breakerBlockEntity;
    public static BlockEntityType<PlacerBlockEntity> placerBlockEntity;
    public static BlockEntityType<CrafterBlockEntity> crafterBlockEntity;

    // Blocks
    public static final Block machineBlock = new Block(machineBlockSettings);
    public static final FisherBlock fisherBlock = new FisherBlock(machineBlockSettings);
    public static final CobblestoneGeneratorBlock cobblestoneGeneratorBlock = new CobblestoneGeneratorBlock(machineBlockSettings);
    public static final MagnetBlock magnetBlock = new MagnetBlock(machineBlockSettings);
    public static final NullifierBlock nullifierBlock = new NullifierBlock(machineBlockSettings);
    public static final TimerBlock timerBlock = new TimerBlock(FabricBlockSettings.copy(Blocks.REPEATER));
    public static final MobFarmBlock mobFarmBlock = new MobFarmBlock(machineBlockSettings);
    public static final FarmerBlock farmerBlock = new FarmerBlock(machineBlockSettings);
    public static final CrusherBlock crusherBlock = new CrusherBlock(machineBlockSettings);
    public static final BreakerBlock breakerBlock = new BreakerBlock(machineBlockSettings);
    public static final PlacerBlock placerBlock = new PlacerBlock(machineBlockSettings);
    public static final CrafterBlock crafterBlock = new CrafterBlock(machineBlockSettings);

    // Screens
    public static ScreenHandlerType<FisherBlockController> fisherBlockScreen;
    public static ScreenHandlerType<CobblestoneGeneratorBlockController> cobblestoneGeneratorBlockScreen;
    public static ScreenHandlerType<NullifierController> nullifierBlockScreen;
    public static ScreenHandlerType<TimerController> timerBlockScreen;
    public static ScreenHandlerType<MobFarmBlockController> mobFarmBlockScreen;
    public static ScreenHandlerType<FarmerBlockController> farmerBlockScreen;
    public static ScreenHandlerType<CrusherBlockController> crusherBlockScreen;
    public static ScreenHandlerType<BreakerBlockController> breakerBlockScreen;
    public static ScreenHandlerType<PlacerBlockController> placerBlockScreen;
    public static ScreenHandlerType<CrafterBlockController> crafterBlockScreen;

    // Stats
    public static Stat interactWithFisher;
    public static Stat interactWithCobblestoneGenerator;
    public static Stat interactWithNullifier;
    public static Stat interactWithTimer;
    public static Stat interactWithMobFarm;
    public static Stat interactWithFarmer;
    public static Stat interactWithCrusher;
    public static Stat interactWithBreaker;
    public static Stat interactWithPlacer;
    public static Stat interactWithCrafter;

    public static void register() {
        // Block entities
        fisherBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "fisher_block"), BlockEntityType.Builder.create(FisherBlockEntity::new, fisherBlock).build(null));
        cobblestoneGeneratorBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "cobblestone_generator_block"), BlockEntityType.Builder.create(CobblestoneGeneratorBlockEntity::new, cobblestoneGeneratorBlock).build(null));
        magnetBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "magnet_block"), BlockEntityType.Builder.create(MagnetBlockEntity::new, magnetBlock).build(null));
        nullifierBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "nullifier"), BlockEntityType.Builder.create(NullifierBlockEntity::new, nullifierBlock).build(null));
        timerBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "timer"), BlockEntityType.Builder.create(TimerBlockEntity::new, timerBlock).build(null));
        mobFarmBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "mob_farm_block"), BlockEntityType.Builder.create(MobFarmBlockEntity::new, mobFarmBlock).build(null));
        farmerBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "farmer_block"), BlockEntityType.Builder.create(FarmerBlockEntity::new, farmerBlock).build(null));
        crusherBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "crusher_block"), BlockEntityType.Builder.create(CrusherBlockEntity::new, crusherBlock).build(null));
        breakerBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "breaker_block"), BlockEntityType.Builder.create(BreakerBlockEntity::new, breakerBlock).build(null));
        placerBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "placer_block"), BlockEntityType.Builder.create(PlacerBlockEntity::new, placerBlock).build(null));
        crafterBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "crafter_block"), BlockEntityType.Builder.create(CrafterBlockEntity::new, crafterBlock).build(null));

        // Blocks
        registerBlock(machineBlock, "machine_block");
        registerBlock(fisherBlock, "fisher_block");
        registerBlock(cobblestoneGeneratorBlock, "cobblestone_generator_block");
        registerBlock(magnetBlock, "magnet_block");
        registerBlock(nullifierBlock, "nullifier");
        registerBlock(timerBlock, "timer");
        registerBlock(mobFarmBlock, "mob_farm_block");
        registerBlock(farmerBlock, "farmer_block");
        registerBlock(crusherBlock, "crusher_block");
        registerBlock(breakerBlock, "breaker_block");
        registerBlock(placerBlock, "placer_block");
        registerBlock(crafterBlock, "crafter_block");

        // Inventories
        fisherBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "fisher_block"), (syncId, inventory) -> new FisherBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
        cobblestoneGeneratorBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "cobblestone_generator_block"), (syncId, inventory) -> new CobblestoneGeneratorBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
        nullifierBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "nullifier_block"), (syncId, inventory) -> new NullifierController(syncId, inventory, ScreenHandlerContext.EMPTY));
        timerBlockScreen = ScreenHandlerRegistry.registerExtended(new Identifier(VanillaAutomated.prefix, "timer_block"), (syncId, inventory, buf) -> new TimerController(syncId, inventory, ScreenHandlerContext.EMPTY, buf.readBlockPos(), buf.readInt()));
        mobFarmBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "mob_farm_block"), (syncId, inventory) -> new MobFarmBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
        farmerBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "farmer_block"), (syncId, inventory) -> new FarmerBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
        crusherBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "crusher_block"), (syncId, inventory) -> new CrusherBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
        breakerBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "breaker_block"), (syncId, inventory) -> new BreakerBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
        placerBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "placer_block"), (syncId, inventory) -> new PlacerBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
        crafterBlockScreen = ScreenHandlerRegistry.registerExtended(new Identifier(VanillaAutomated.prefix, "crafter_block"), (syncId, inventory, buf) -> new CrafterBlockController(syncId, inventory, ScreenHandlerContext.EMPTY, buf.readBlockPos(), buf.readString()));

        // Stats
        interactWithFisher = registerStat("interact_with_fisher");
        interactWithCobblestoneGenerator = registerStat("interact_with_cobblestone_generator");
        interactWithNullifier = registerStat("interact_with_nullifier");
        interactWithTimer = registerStat("interact_with_timer");
        interactWithMobFarm = registerStat("interact_with_mob_farm");
        interactWithFarmer = registerStat("interact_with_farmer");
        interactWithCrusher = registerStat("interact_with_crusher");
        interactWithBreaker = registerStat("interact_with_breaker");
        interactWithPlacer = registerStat("interact_with_placer");
        interactWithCrafter = registerStat("interact_with_crafter");
    }

    private static void registerBlock(Block block, String name) {
        Registry.register(Registry.BLOCK, new Identifier(VanillaAutomated.prefix, name), block);
        Registry.register(Registry.ITEM, new Identifier(VanillaAutomated.prefix, name), new BlockItem(block, new Item.Settings().group(VanillaAutomated.ITEM_GROUP)));
    }

    private static Stat registerStat(String name) {
        Registry.register(Registry.CUSTOM_STAT, name, new Identifier(VanillaAutomated.prefix, name));
        return Stats.CUSTOM.getOrCreateStat(new Identifier(VanillaAutomated.prefix, name), StatFormatter.DEFAULT);
    }

}
