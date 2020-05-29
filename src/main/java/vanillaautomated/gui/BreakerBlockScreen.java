package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class BreakerBlockScreen extends CottonInventoryScreen<BreakerBlockController> {
    public BreakerBlockScreen(BreakerBlockController container, PlayerEntity player) {
        super(container, player);
    }
}
