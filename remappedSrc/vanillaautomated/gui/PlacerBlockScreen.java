package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class PlacerBlockScreen extends CottonInventoryScreen<PlacerBlockController> {
    public PlacerBlockScreen(PlacerBlockController container, PlayerEntity player) {
        super(container, player);
    }
}
