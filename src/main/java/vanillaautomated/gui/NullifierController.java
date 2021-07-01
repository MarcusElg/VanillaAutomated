package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import vanillaautomated.VanillaAutomatedBlocks;

public class NullifierController extends SyncedGuiDescription {
    public NullifierController(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(VanillaAutomatedBlocks.nullifierBlockScreen, syncId, playerInventory, getBlockInventory(context, 1), getBlockPropertyDelegate(context, 0));

        setTitleAlignment(HorizontalAlignment.LEFT);
        WPlainPanel root = new WPlainPanel();
        root.setSize(176, 170);
        setRootPanel(root);

        WGridPanel machinePanel = new WGridPanel();
        machinePanel.setSize(9, 3);

        WItemSlot slot = WItemSlot.of(blockInventory, 0);
        machinePanel.add(slot, 4, 1);

        root.add(machinePanel, 0, 20);

        root.add(this.createPlayerInventoryPanel(true), 7, 76);
        root.validate(this);
    }
}
