package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import vanillaautomated.VanillaAutomated;

import java.util.ArrayList;

public class CrafterBlockController extends CottonCraftingController {
    public ArrayList<WItemSprite> itemSprites = new ArrayList<WItemSprite>();

    public CrafterBlockController(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, Text title, String recipeItems) {
        super(RecipeType.SMELTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));

        String[] itemStrings = recipeItems.split(",");

        WMaxedPanel root = new WMaxedPanel();
        root.setSize(160, 150);
        setRootPanel(root);

        WGridPanel machinePanel = new WGridPanel();
        machinePanel.setSize(9, 3);

        WLabel label = new WLabel(title);
        label.setAlignment(Alignment.CENTER);
        root.add(label, 0, 0, 160, 10);

        WBar fire = new WBar(VanillaAutomated.flames_background, VanillaAutomated.flames, 0, 2, WBar.Direction.UP);
        machinePanel.add(fire, 1, 1);

        WItemSlot fuelSlot = WItemSlot.of(blockInventory, 0);
        machinePanel.add(fuelSlot, 1, 2);

        int lastSlotIndex = 1;
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                WItemSlot craftingSlot = WItemSlot.of(blockInventory, lastSlotIndex);
                machinePanel.add(craftingSlot, j + 3, i);
                WItemSprite item = new WItemSprite(new ItemStack(Registry.ITEM.get(Identifier.tryParse(itemStrings[i*3+j])), 1));
                itemSprites.add(item);
                machinePanel.add(item, j + 3, i);
                lastSlotIndex++;
            }
        }

        WBar progress = new WBar(VanillaAutomated.progress_background, VanillaAutomated.progress, 1, 3, WBar.Direction.RIGHT);
        machinePanel.add(progress, 6, 1);

        WItemSlot outputSlot = WItemSlot.of(blockInventory, 10);
        machinePanel.add(outputSlot, 7, 1);

        root.add(machinePanel, 0, 10);

        WLabel inventoryLabel = new WLabel(new TranslatableText("container.inventory"));
        inventoryLabel.setSize(256, 10);

        root.add(inventoryLabel, 0, 64);
        root.add(this.createPlayerInventoryPanel(), 0, 74);
        root.validate(this);
    }
}
