package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class FarmerBlockScreen extends CottonInventoryScreen<FarmerBlockController> {
    public FarmerBlockScreen(FarmerBlockController container, PlayerEntity player, Text title) {
        super(container, player, title);
    }
}
