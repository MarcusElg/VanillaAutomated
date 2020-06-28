package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class CrusherBlockScreen extends CottonInventoryScreen<CrusherBlockController> {
    public CrusherBlockScreen(CrusherBlockController container, PlayerEntity player, Text title) {
        super(container, player, title);
    }
}
