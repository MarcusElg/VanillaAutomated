package vanillaautomated;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.stat.Stat;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import vanillaautomated.blockentities.FisherBlockEntity;
import vanillaautomated.blocks.FisherBlock;
import vanillaautomated.gui.FisherBlockController;

public class VanillaAutomatedBlocks {

    public static final FabricBlockSettings machineBlockSettings = FabricBlockSettings.of(Material.METAL, MaterialColor.GRAY).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL);

    // Block entities
    public static BlockEntityType<FisherBlockEntity> fisherBlockEntity;

    // Blocks
    public static final Block machineBlock = new Block(machineBlockSettings);
    public static final FisherBlock fisherBlock = new FisherBlock(machineBlockSettings);

    // Stats
    public static Stat interact_with_fisher;

    public static void register() {
        // Block entities
        fisherBlockEntity = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(VanillaAutomated.prefix, "fisher_block"), BlockEntityType.Builder.create(FisherBlockEntity::new, fisherBlock).build(null));

        // Blocks
        registerBlock(machineBlock, "machine_block");
        registerBlock(fisherBlock, "fisher_block");

        // Inventories
        ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier(VanillaAutomated.prefix, "fisher_block"), (syncId, id, player, buf) -> new FisherBlockController(syncId, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos())));

        // Stats
        interact_with_fisher = registerStat("interact_with_fisher");
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
