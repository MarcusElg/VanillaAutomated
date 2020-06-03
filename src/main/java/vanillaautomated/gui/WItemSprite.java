package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;

public class WItemSprite extends WWidget {
    ItemStack itemStack;

    public WItemSprite (ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setItem (ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void paintBackground(int x, int y) {
        MinecraftClient.getInstance().getItemRenderer().renderGuiItem(itemStack, x, y);
    }
}
