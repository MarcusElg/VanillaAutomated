package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import vanillaautomated.VanillaAutomated;

public class CobblestoneGeneratorBlockController extends CottonCraftingController {
    public CobblestoneGeneratorBlockController(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, Text title) {
        super(RecipeType.SMELTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));

        WMaxedPanel root = new WMaxedPanel();
        root.setSize(160, 150);
        setRootPanel(root);

        WGridPanel machinePanel = new WGridPanel();
        machinePanel.setSize(9, 3);

        WLabel label = new WLabel(title);
        label.setAlignment(Alignment.CENTER);
        root.add(label, 0, 0, 160, 10);

        BackgroundPainter bucketSlot = new BackgroundPainter() {
            @Override
            public void paintBackground(int i, int i1, WWidget wWidget) {
                WSprite sprite = new WSprite(VanillaAutomated.bucket_slot);
            }
        };

        WItemSlot waterSlot = WItemSlot.of(blockInventory, 0);
        waterSlot.setBackgroundPainter(bucketSlot);
        machinePanel.add(waterSlot, 3, 0);

        WItemSlot lavaSlot = WItemSlot.of(blockInventory, 1);
        lavaSlot.setBackgroundPainter(bucketSlot);
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

        WLabel inventoryLabel = new WLabel(new TranslatableText("container.inventory"));
        inventoryLabel.setSize(256, 10);

        root.add(inventoryLabel, 0, 64);
        root.add(this.createPlayerInventoryPanel(), 0, 74);
        root.validate(this);
    }
}
