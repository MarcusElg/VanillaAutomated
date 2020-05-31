package vanillaautomated;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import vanillaautomated.blockentities.TimerBlockEntity;
import vanillaautomated.recipes.CrusherRecipe;
import vanillaautomated.recipes.CrusherRecipeSerializer;
import vanillaautomated.recipes.FarmerRecipe;
import vanillaautomated.recipes.FarmerRecipeSerializer;

import java.util.Optional;

public class VanillaAutomated implements ModInitializer {

    public static String prefix = "vanillaautomated";
    public static Identifier flames = new Identifier(prefix, "textures/gui/flames.png");
    public static Identifier flames_background = new Identifier(prefix, "textures/gui/flames_background.png");
    public static Identifier progress = new Identifier(prefix, "textures/gui/progress.png");
    public static Identifier progress_background = new Identifier(prefix, "textures/gui/progress_background.png");
    public static Identifier bucket_slot = new Identifier(prefix, "textures/gui/bucket_slot.png");
    public static Identifier tool_slot = new Identifier(prefix, "textures/gui/tool_slot.png");
    public static Identifier timer_configuration_packet = new Identifier(prefix, "timer_configuration");

    public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier(prefix, "machines"),
            () -> new ItemStack(VanillaAutomatedBlocks.machineBlock));

    public static RecipeType farmerRecipeType;
    public static RecipeType crusherRecipeType;

    @Override
    public void onInitialize() {
        VanillaAutomatedBlocks.register();
        VanillaAutomatedItems.register();

        // Recipe type
        Registry.register(Registry.RECIPE_SERIALIZER, FarmerRecipeSerializer.ID, FarmerRecipeSerializer.INSTANCE);
        farmerRecipeType = Registry.register(Registry.RECIPE_TYPE, new Identifier(prefix, FarmerRecipe.Type.ID), FarmerRecipe.Type.INSTANCE);
        Registry.register(Registry.RECIPE_SERIALIZER, CrusherRecipeSerializer.ID, CrusherRecipeSerializer.INSTANCE);
        crusherRecipeType = Registry.register(Registry.RECIPE_TYPE, new Identifier(prefix, CrusherRecipe.Type.ID), CrusherRecipe.Type.INSTANCE);

        // Packets
        registerTimerPacket();
    }

    private void registerTimerPacket() {
        ServerSidePacketRegistry.INSTANCE.register(timer_configuration_packet, (packetContext, attachedData) -> {
            BlockPos blockPos = attachedData.readBlockPos();
            int time = attachedData.readInt();
            packetContext.getTaskQueue().execute(() -> {
                if (packetContext.getPlayer().world.canPlayerModifyAt(packetContext.getPlayer(), blockPos)) {
                    // Turn to diamond
                    ((TimerBlockEntity) packetContext.getPlayer().world.getBlockEntity(blockPos)).modifyTime(time);
                }
            });
        });
    }
}
