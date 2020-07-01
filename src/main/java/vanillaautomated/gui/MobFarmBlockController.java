package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;

public class MobFarmBlockController extends SyncedGuiDescription {
    public MobFarmBlockController(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(VanillaAutomatedBlocks.mobFarmBlockScreen, syncId, playerInventory, getBlockInventory(context, 11), getBlockPropertyDelegate(context, 4));

        setTitleAlignment(HorizontalAlignment.CENTER);
        WPlainPanel root = new WPlainPanel();
        root.setSize(160, 150);
        setRootPanel(root);

        WGridPanel machinePanel = new WGridPanel();
        machinePanel.setSize(9, 3);

        WItemSlot mobSlot = WItemSlot.of(blockInventory, 0);
        machinePanel.add(mobSlot, 2, 0);

        WBar fire = new WBar(VanillaAutomated.flames_background, VanillaAutomated.flames, 0, 2, WBar.Direction.UP);
        machinePanel.add(fire, 2, 1);

        WItemSlot fuelSlot = WItemSlot.of(blockInventory, 1);
        machinePanel.add(fuelSlot, 2, 2);

        WBar progress = new WBar(VanillaAutomated.progress_background, VanillaAutomated.progress, 1, 3, WBar.Direction.RIGHT);
        machinePanel.add(progress, 3, 2);

        int lastSlotIndex = 2;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                WItemSlot outputSlot = WItemSlot.of(blockInventory, lastSlotIndex).setInsertingAllowed(false);
                machinePanel.add(outputSlot, j + 4, i);
                lastSlotIndex++;
            }
        }

        root.add(machinePanel, 0, 10);

        root.add(this.createPlayerInventoryPanel(true), 0, 74);
        root.validate(this);
    }
}
