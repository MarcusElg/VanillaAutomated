package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import vanillaautomated.VanillaAutomated;

public class TimerController extends CottonCraftingController {
    public TimerController(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, Text title) {
        super(RecipeType.SMELTING, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));

        WMaxedPanel root = new WMaxedPanel();
        root.setSize(160, 150);
        setRootPanel(root);

        WLabel label = new WLabel(title);
        label.setAlignment(Alignment.CENTER);
        root.add(label, 0, 0, 160, 10);

        int rowHeight = 25;

        WLabel speedTitle = new WLabel(new TranslatableText("vanillaautomated.container.timer.speed"));
        speedTitle.setAlignment(Alignment.CENTER);
        root.add(speedTitle, 0, 15, 160, 10);

        WLabel speedLabel = new WLabel("10");
        speedLabel.setAlignment(Alignment.CENTER);
        speedLabel.setSize(160, 30);
        root.add(speedLabel, 72, rowHeight + 5);

        WButton speedButton = new WButton(new LiteralText("-10"));
        //speedButton.setOnClick();
        root.add(speedButton, 18, rowHeight, 27, 18);

        speedButton = new WButton(new LiteralText("-1"));
        root.add(speedButton, 45, rowHeight, 27, 18);

        speedButton = new WButton(new LiteralText("+1"));
        root.add(speedButton, 90, rowHeight, 27, 18);

        speedButton = new WButton(new LiteralText("+10"));
        root.add(speedButton, 117, rowHeight, 27, 18);

        WLabel inventoryLabel = new WLabel(new TranslatableText("container.inventory"));
        inventoryLabel.setSize(256, 10);

        root.add(inventoryLabel, 0, 64);
        root.add(this.createPlayerInventoryPanel(), 0, 74);
        root.validate(this);
    }
}
