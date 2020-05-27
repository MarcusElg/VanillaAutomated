package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class CrusherBlockScreen extends CottonInventoryScreen<CrusherBlockController> {
    public CrusherBlockScreen(CrusherBlockController container, PlayerEntity player) {
        super(container, player);
    }
}
