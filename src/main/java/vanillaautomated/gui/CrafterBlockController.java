package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.blockentities.CrafterBlockEntity;

import java.util.ArrayList;

public class CrafterBlockController extends SyncedGuiDescription {
    public ArrayList<WItemSprite> itemSprites = new ArrayList<WItemSprite>();

    public CrafterBlockController(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, BlockPos blockPos, String recipeItems) {
        super(VanillaAutomatedBlocks.crafterBlockScreen, syncId, playerInventory, getBlockInventory(context, 11), getBlockPropertyDelegate(context, 4));

        String[] itemStrings = recipeItems.split(",");

        setTitleAlignment(HorizontalAlignment.LEFT);
        WPlainPanel root = new WPlainPanel();
        root.setSize(176, 170);
        setRootPanel(root);

        WGridPanel machinePanel = new WGridPanel();
        machinePanel.setSize(9, 3);

        WBar fire = new WBar(VanillaAutomated.flames_background, VanillaAutomated.flames, 0, 2, WBar.Direction.UP);
        machinePanel.add(fire, 1, 1);

        WItemSlot fuelSlot = WItemSlot.of(blockInventory, 0);
        machinePanel.add(fuelSlot, 1, 2);

        int lastSlotIndex = 1;
        itemSprites.clear();
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                WItemSlot craftingSlot = WItemSlot.of(blockInventory, lastSlotIndex);
                machinePanel.add(craftingSlot, i + 3, j);

                WItemSprite item = new WItemSprite(new ItemStack(Registry.ITEM.get(new Identifier(itemStrings[i + j * 3].replace(".", ":"))), 1));
                itemSprites.add(item);
                machinePanel.add(item, i + 3, j);

                lastSlotIndex++;
            }
        }

        WBar progress = new WBar(VanillaAutomated.progress_background, VanillaAutomated.progress, 1, 3, WBar.Direction.RIGHT);
        machinePanel.add(progress, 6, 1);

        WButton resetButton = new WButton(new LiteralText("X"));
        resetButton.setOnClick(() -> sendPacket(-10, blockPos));
        machinePanel.add(resetButton, 6, 2);

        WItemSlot outputSlot = WItemSlot.of(blockInventory, 10);
        machinePanel.add(outputSlot, 7, 1);

        root.add(machinePanel, 0, 20);

        root.add(this.createPlayerInventoryPanel(true), 7, 76);
        root.validate(this);

    }

    private void sendPacket(int change, BlockPos blockPos) {
        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeBlockPos(blockPos);
        // Send packet to server to change the block for us
        ClientSidePacketRegistry.INSTANCE.sendToServer(VanillaAutomated.crafter_reset_packet, passedData);
        ((CrafterBlockEntity) world.getBlockEntity(blockPos)).resetRecipeClient();
    }
}
