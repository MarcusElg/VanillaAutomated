package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class TimerScreen extends CottonInventoryScreen<TimerController> {
    public TimerScreen(TimerController container, PlayerEntity player) {
        super(container, player);
    }
}
