package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class FisherBlockScreen extends CottonInventoryScreen<FisherBlockController> {
    public FisherBlockScreen(FisherBlockController container, PlayerEntity player, Text title) {
        super(container, player, title);
    }
}
