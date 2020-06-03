package vanillaautomated;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import vanillaautomated.gui.*;

public class VanillaAutomatedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Inventories
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(VanillaAutomated.prefix, "fisher_block"), (syncId, identifier, player, buf) -> new FisherBlockScreen(new FisherBlockController(syncId, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos()), buf.readText()), player));
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(VanillaAutomated.prefix, "cobblestone_generator_block"), (syncId, identifier, player, buf) -> new CobblestoneGeneratorBlockScreen(new CobblestoneGeneratorBlockController(syncId, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos()), buf.readText()), player));
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(VanillaAutomated.prefix, "nullifier"), (syncId, identifier, player, buf) -> new NullifierScreen(new NullifierController(syncId, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos()), buf.readText()), player));
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(VanillaAutomated.prefix, "timer"), (syncId, identifier, player, buf) -> {
            BlockPos blockPos = buf.readBlockPos();
            return new TimerScreen(new TimerController(syncId, player.inventory, ScreenHandlerContext.create(player.world, blockPos), buf.readText(), blockPos, buf.readInt()), player);
        });
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(VanillaAutomated.prefix, "mob_farm_block"), (syncId, identifier, player, buf) -> new MobFarmBlockScreen(new MobFarmBlockController(syncId, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos()), buf.readText()), player));
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(VanillaAutomated.prefix, "farmer_block"), (syncId, identifier, player, buf) -> new FarmerBlockScreen(new FarmerBlockController(syncId, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos()), buf.readText()), player));
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(VanillaAutomated.prefix, "crusher_block"), (syncId, identifier, player, buf) -> new CrusherBlockScreen(new CrusherBlockController(syncId, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos()), buf.readText()), player));
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(VanillaAutomated.prefix, "breaker_block"), (syncId, identifier, player, buf) -> new BreakerBlockScreen(new BreakerBlockController(syncId, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos()), buf.readText()), player));
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(VanillaAutomated.prefix, "placer_block"), (syncId, identifier, player, buf) -> new PlacerBlockScreen(new PlacerBlockController(syncId, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos()), buf.readText()), player));
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(VanillaAutomated.prefix, "crafter_block"), (syncId, identifier, player, buf) -> new CrafterBlockScreen(new CrafterBlockController(syncId, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos()), buf.readText(), buf.readString()), player));

        // Rendering
        BlockRenderLayerMap.INSTANCE.putBlock(VanillaAutomatedBlocks.timerBlock, RenderLayer.getCutout());
    }
}
