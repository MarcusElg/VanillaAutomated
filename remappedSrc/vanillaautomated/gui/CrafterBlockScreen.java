package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class CrafterBlockScreen extends CottonInventoryScreen<CrafterBlockController> {
    public CrafterBlockScreen(CrafterBlockController container, PlayerEntity player) {
        super(container, player);
    }
}
