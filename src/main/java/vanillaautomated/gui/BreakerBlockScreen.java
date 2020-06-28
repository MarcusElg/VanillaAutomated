package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class BreakerBlockScreen extends CottonInventoryScreen<BreakerBlockController> {
    public BreakerBlockScreen(BreakerBlockController container, PlayerEntity player, Text title) {
        super(container, player, title);
    }
}
