package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class CobblestoneGeneratorBlockScreen extends CottonInventoryScreen<CobblestoneGeneratorBlockController> {
    public CobblestoneGeneratorBlockScreen(CobblestoneGeneratorBlockController container, PlayerEntity player, Text title) {
        super(container, player, title);
    }
}
