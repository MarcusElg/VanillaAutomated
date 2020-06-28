package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class PlacerBlockScreen extends CottonInventoryScreen<PlacerBlockController> {
    public PlacerBlockScreen(PlacerBlockController container, PlayerEntity player, Text title) {
        super(container, player, title);
    }
}
