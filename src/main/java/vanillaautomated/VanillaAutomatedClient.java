package vanillaautomated;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
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
        ScreenRegistry.<FisherBlockController, FisherBlockScreen>register(VanillaAutomatedBlocks.fisherBlockScreen, (gui, inventory, title) -> new FisherBlockScreen(gui, inventory.player, title));
        ScreenRegistry.<CobblestoneGeneratorBlockController, CobblestoneGeneratorBlockScreen>register(VanillaAutomatedBlocks.cobblestoneGeneratorBlockScreen, (gui, inventory, title) -> new CobblestoneGeneratorBlockScreen(gui, inventory.player, title));
        ScreenRegistry.<NullifierController, NullifierScreen>register(VanillaAutomatedBlocks.nullifierBlockScreen, (gui, inventory, title) -> new NullifierScreen(gui, inventory.player, title));
        ScreenRegistry.<TimerController, TimerScreen>register(VanillaAutomatedBlocks.timerBlockScreen, (gui, inventory, title) -> new TimerScreen(gui, inventory.player, title));
        ScreenRegistry.<MobFarmBlockController, MobFarmBlockScreen>register(VanillaAutomatedBlocks.mobFarmBlockScreen, (gui, inventory, title) -> new MobFarmBlockScreen(gui, inventory.player, title));
        ScreenRegistry.<FarmerBlockController, FarmerBlockScreen>register(VanillaAutomatedBlocks.farmerBlockScreen, (gui, inventory, title) -> new FarmerBlockScreen(gui, inventory.player, title));
        ScreenRegistry.<CrusherBlockController, CrusherBlockScreen>register(VanillaAutomatedBlocks.crusherBlockScreen, (gui, inventory, title) -> new CrusherBlockScreen(gui, inventory.player, title));
        ScreenRegistry.<BreakerBlockController, BreakerBlockScreen>register(VanillaAutomatedBlocks.breakerBlockScreen, (gui, inventory, title) -> new BreakerBlockScreen(gui, inventory.player, title));
        ScreenRegistry.<PlacerBlockController, PlacerBlockScreen>register(VanillaAutomatedBlocks.placerBlockScreen, (gui, inventory, title) -> new PlacerBlockScreen(gui, inventory.player, title));
        ScreenRegistry.<CrafterBlockController, CrafterBlockScreen>register(VanillaAutomatedBlocks.crafterBlockScreen, (gui, inventory, title) -> new CrafterBlockScreen(gui, inventory.player, title));

        // Rendering
        BlockRenderLayerMap.INSTANCE.putBlock(VanillaAutomatedBlocks.timerBlock, RenderLayer.getCutout());
    }
}
