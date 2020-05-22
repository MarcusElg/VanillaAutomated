package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class CobblestoneGeneratorBlockScreen extends CottonInventoryScreen<CobblestoneGeneratorBlockController> {
    public CobblestoneGeneratorBlockScreen(CobblestoneGeneratorBlockController container, PlayerEntity player) {
        super(container, player);
    }
}
