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

public class FarmerBlockController extends SyncedGuiDescription {
    public FarmerBlockController(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(VanillaAutomatedBlocks.farmerBlockScreen, syncId, playerInventory, getBlockInventory(context, 4), getBlockPropertyDelegate(context, 4));

        setTitleAlignment(HorizontalAlignment.CENTER);
        WPlainPanel root = new WPlainPanel();
        root.setSize(160, 150);
        setRootPanel(root);

        WGridPanel machinePanel = new WGridPanel();
        machinePanel.setSize(9, 3);

        WItemSlot inputSlot = WItemSlot.of(blockInventory, 0);
        machinePanel.add(inputSlot, 3, 0);

        WItemSlot bonemealSlot = WItemSlot.of(blockInventory, 1);
        machinePanel.add(bonemealSlot, 4, 0);

        WBar fire = new WBar(VanillaAutomated.flames_background, VanillaAutomated.flames, 0, 2, WBar.Direction.UP);
        machinePanel.add(fire, 3, 1);

        WItemSlot fuelSlot = WItemSlot.of(blockInventory, 2);
        machinePanel.add(fuelSlot, 3, 2);

        WBar progress = new WBar(VanillaAutomated.progress_background, VanillaAutomated.progress, 1, 3, WBar.Direction.RIGHT);
        machinePanel.add(progress, 4, 2);

        WItemSlot outputSlot = WItemSlot.of(blockInventory, 3);
        machinePanel.add(outputSlot, 5, 2);

        root.add(machinePanel, 0, 10);

        root.add(this.createPlayerInventoryPanel(true), 0, 74);
        root.validate(this);
    }
}
