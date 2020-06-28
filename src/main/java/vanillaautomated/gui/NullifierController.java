package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import vanillaautomated.VanillaAutomatedBlocks;

public class NullifierController extends SyncedGuiDescription {
    public NullifierController(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(VanillaAutomatedBlocks.nullifierBlockScreen, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));

        WMaxedPanel root = new WMaxedPanel();
        root.setSize(160, 150);
        setRootPanel(root);

        WGridPanel machinePanel = new WGridPanel();
        machinePanel.setSize(9, 3);

        WItemSlot slot = WItemSlot.of(blockInventory, 0);
        machinePanel.add(slot, 4, 1);

        root.add(machinePanel, 0, 10);

        root.add(this.createPlayerInventoryPanel(true), 0, 74);
        root.validate(this);
    }
}
