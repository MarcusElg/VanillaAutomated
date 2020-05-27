package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class FarmerBlockScreen extends CottonInventoryScreen<FarmerBlockController> {
    public FarmerBlockScreen(FarmerBlockController container, PlayerEntity player) {
        super(container, player);
    }
}
