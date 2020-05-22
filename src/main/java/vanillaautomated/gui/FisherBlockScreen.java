package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class FisherBlockScreen extends CottonInventoryScreen<FisherBlockController> {
    public FisherBlockScreen(FisherBlockController container, PlayerEntity player) {
        super(container, player);
    }
}
