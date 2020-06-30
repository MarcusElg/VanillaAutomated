package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;

public class CobblestoneGeneratorBlockController extends SyncedGuiDescription {
    public CobblestoneGeneratorBlockController(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(VanillaAutomatedBlocks.cobblestoneGeneratorBlockScreen, syncId, playerInventory, getBlockInventory(context, 4), getBlockPropertyDelegate(context, 4));

        WPlainPanel root = new WPlainPanel();
        root.setSize(160, 150);
        setRootPanel(root);

        WGridPanel machinePanel = new WGridPanel();
        machinePanel.setSize(9, 3);

        WSprite bucketOverlay = new WSprite(VanillaAutomated.bucket_slot);
        machinePanel.add(bucketOverlay, 3, 0);

        bucketOverlay = new WSprite(VanillaAutomated.bucket_slot);
        machinePanel.add(bucketOverlay, 5, 0);

        WItemSlot waterSlot = WItemSlot.of(blockInventory, 0);
        machinePanel.add(waterSlot, 3, 0);

        WItemSlot lavaSlot = WItemSlot.of(blockInventory, 1);
        machinePanel.add(lavaSlot, 5, 0);

        WBar fire = new WBar(VanillaAutomated.flames_background, VanillaAutomated.flames, 0, 2, WBar.Direction.UP);
        machinePanel.add(fire, 3, 1);

        WBar progress = new WBar(VanillaAutomated.progress_background, VanillaAutomated.progress, 1, 3, WBar.Direction.RIGHT);
        machinePanel.add(progress, 4, 1);

        WItemSlot fuelSlot = WItemSlot.of(blockInventory, 2);
        machinePanel.add(fuelSlot, 3, 2);

        WItemSlot outputSlot = WItemSlot.of(blockInventory, 3).setInsertingAllowed(false);
        machinePanel.add(outputSlot, 5, 1);

        root.add(machinePanel, 0, 10);

        root.add(this.createPlayerInventoryPanel(true), 0, 74);
        root.validate(this);
    }
}
