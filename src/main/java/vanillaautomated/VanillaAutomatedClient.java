package vanillaautomated;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import vanillaautomated.gui.FisherBlockController;
import vanillaautomated.gui.FisherBlockScreen;

public class VanillaAutomatedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Inventories
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(VanillaAutomated.prefix, "fisher_block"), (syncId, identifier, player, buf) -> new FisherBlockScreen(new FisherBlockController(syncId, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos()), buf.readText()), player));
    }
}
