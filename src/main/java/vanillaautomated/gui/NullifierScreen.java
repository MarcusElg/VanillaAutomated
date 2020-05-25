package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class NullifierScreen extends CottonInventoryScreen<NullifierController> {
    public NullifierScreen(NullifierController container, PlayerEntity player) {
        super(container, player);
    }
}
