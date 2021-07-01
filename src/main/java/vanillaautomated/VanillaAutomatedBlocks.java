package vanillaautomated;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
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

    public static final FabricBlockSettings machineBlockSettings = FabricBlockSettings.of(Material.METAL, MapColor.GRAY).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL);
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
    public static Stat<Identifier> interactWithFisher;
    public static Stat<Identifier> interactWithCobblestoneGenerator;
    public static Stat<Identifier> interactWithNullifier;
    public static Stat<Identifier> interactWithTimer;
    public static Stat<Identifier> interactWithMobFarm;
    public static Stat<Identifier> interactWithFarmer;
    public static Stat<Identifier> interactWithCrusher;
    public static Stat<Identifier> interactWithBreaker;
    public static Stat<Identifier> interactWithPlacer;
    public static Stat<Identifier> interactWithCrafter;

    public static void register() {

        if (VanillaAutomated.config.enableBreaker) {
            breakerBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "breaker_block"), FabricBlockEntityTypeBuilder.create(BreakerBlockEntity::new, breakerBlock).build(null));
            registerBlock(breakerBlock, "breaker_block");
            breakerBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "breaker_block"), (syncId, inventory) -> new BreakerBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
            interactWithBreaker = registerStat("interact_with_breaker");
        }
        if (VanillaAutomated.config.enableCobblegenerator) {
            cobblestoneGeneratorBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "cobblestone_generator_block"), FabricBlockEntityTypeBuilder.create(CobblestoneGeneratorBlockEntity::new, cobblestoneGeneratorBlock).build(null));
            registerBlock(cobblestoneGeneratorBlock, "cobblestone_generator_block");
            cobblestoneGeneratorBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "cobblestone_generator_block"), (syncId, inventory) -> new CobblestoneGeneratorBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
            interactWithCobblestoneGenerator = registerStat("interact_with_cobblestone_generator");
        }
        if (VanillaAutomated.config.enableCrafter) {
            crafterBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "crafter_block"), FabricBlockEntityTypeBuilder.create(CrafterBlockEntity::new, crafterBlock).build(null));
            registerBlock(crafterBlock, "crafter_block");
            crafterBlockScreen = ScreenHandlerRegistry.registerExtended(new Identifier(VanillaAutomated.prefix, "crafter_block"), (syncId, inventory, buf) -> new CrafterBlockController(syncId, inventory, ScreenHandlerContext.EMPTY, buf.readBlockPos(), buf.readString()));
            interactWithCrafter = registerStat("interact_with_crafter");
        }
        if (VanillaAutomated.config.enableCrusher) {
            crusherBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "crusher_block"), FabricBlockEntityTypeBuilder.create(CrusherBlockEntity::new, crusherBlock).build(null));
            registerBlock(crusherBlock, "crusher_block");
            crusherBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "crusher_block"), (syncId, inventory) -> new CrusherBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
            interactWithCrusher = registerStat("interact_with_crusher");
        }
        if (VanillaAutomated.config.enableFarmer) {
            farmerBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "farmer_block"), FabricBlockEntityTypeBuilder.create(FarmerBlockEntity::new, farmerBlock).build(null));
            registerBlock(farmerBlock, "farmer_block");
            farmerBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "farmer_block"), (syncId, inventory) -> new FarmerBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
            interactWithFarmer = registerStat("interact_with_farmer");
        }
        if (VanillaAutomated.config.enableFisher) {
            fisherBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "fisher_block"), FabricBlockEntityTypeBuilder.create(FisherBlockEntity::new, fisherBlock).build(null));
            registerBlock(fisherBlock, "fisher_block");
            fisherBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "fisher_block"), (syncId, inventory) -> new FisherBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
            interactWithFisher = registerStat("interact_with_fisher");
        }
        if (VanillaAutomated.config.enableMobFarm) {
            mobFarmBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "mob_farm_block"), FabricBlockEntityTypeBuilder.create(MobFarmBlockEntity::new, mobFarmBlock).build(null));
            registerBlock(mobFarmBlock, "mob_farm_block");
            mobFarmBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "mob_farm_block"), (syncId, inventory) -> new MobFarmBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
            interactWithMobFarm = registerStat("interact_with_mob_farm");
        }
        if (VanillaAutomated.config.enablePlacer) {
            placerBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "placer_block"), FabricBlockEntityTypeBuilder.create(PlacerBlockEntity::new, placerBlock).build(null));
            registerBlock(placerBlock, "placer_block");
            placerBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "placer_block"), (syncId, inventory) -> new PlacerBlockController(syncId, inventory, ScreenHandlerContext.EMPTY));
            interactWithPlacer = registerStat("interact_with_placer");
        }
        if (VanillaAutomated.config.enableMagnet) {
            magnetBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "magnet_block"), FabricBlockEntityTypeBuilder.create(MagnetBlockEntity::new, magnetBlock).build(null));
            registerBlock(magnetBlock, "magnet_block");
        }
        if (VanillaAutomated.config.enableNullifier) {
            nullifierBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "nullifier"), FabricBlockEntityTypeBuilder.create(NullifierBlockEntity::new, nullifierBlock).build(null));
            registerBlock(nullifierBlock, "nullifier");
            nullifierBlockScreen = ScreenHandlerRegistry.registerSimple(new Identifier(VanillaAutomated.prefix, "nullifier_block"), (syncId, inventory) -> new NullifierController(syncId, inventory, ScreenHandlerContext.EMPTY));
            interactWithNullifier = registerStat("interact_with_nullifier");
        }
        if (VanillaAutomated.config.enableTimer) {
            timerBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "timer"), FabricBlockEntityTypeBuilder.create(TimerBlockEntity::new, timerBlock).build(null));
            registerBlock(timerBlock, "timer");
            timerBlockScreen = ScreenHandlerRegistry.registerExtended(new Identifier(VanillaAutomated.prefix, "timer_block"), (syncId, inventory, buf) -> new TimerController(syncId, inventory, ScreenHandlerContext.EMPTY, buf.readBlockPos(), buf.readInt()));
            interactWithTimer = registerStat("interact_with_timer");
        }
        registerBlock(machineBlock, "machine_block");
    }

    private static void registerBlock(Block block, String name) {
        Registry.register(Registry.BLOCK, new Identifier(VanillaAutomated.prefix, name), block);
        Registry.register(Registry.ITEM, new Identifier(VanillaAutomated.prefix, name), new BlockItem(block, new Item.Settings().group(VanillaAutomated.ITEM_GROUP)));
    }

    private static Stat<Identifier> registerStat(String name) {
        Registry.register(Registry.CUSTOM_STAT, name, new Identifier(VanillaAutomated.prefix, name));
        return Stats.CUSTOM.getOrCreateStat(new Identifier(VanillaAutomated.prefix, name), StatFormatter.DEFAULT);
    }

}
