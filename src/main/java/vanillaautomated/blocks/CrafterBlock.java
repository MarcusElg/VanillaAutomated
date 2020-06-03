package vanillaautomated.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.blockentities.CrafterBlockEntity;

import java.util.Random;
public class CrafterBlock extends MachineBlock {

    public CrafterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CrafterBlockEntity) {
                ((CrafterBlockEntity) blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CrafterBlockEntity) {
                ItemScatterer.spawn(world, pos, (Inventory) ((CrafterBlockEntity) blockEntity));
                world.updateComparators(pos, this);
            }
        }

        super.onBlockRemoved(state, world, pos, newState, notify);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.PASS;
        BlockEntity be = world.getBlockEntity(pos);
        if (be != null && be instanceof CrafterBlockEntity) {
            ContainerProviderRegistry.INSTANCE.openContainer(new Identifier(VanillaAutomated.prefix, "crafter_block"), player, (packetByteBuf -> {
                packetByteBuf.writeBlockPos(pos);
                packetByteBuf.writeText(((CrafterBlockEntity) be).getDisplayName());
                packetByteBuf.writeString(((CrafterBlockEntity)be).getRecipeItems().toString().replace(" ", "").replace("[", "").replace("]", ""));
            } ));
            player.incrementStat(VanillaAutomatedBlocks.interact_with_crafter);
        }

        return ActionResult.SUCCESS;
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new CrafterBlockEntity();
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (((CrafterBlockEntity) world.getBlockEntity(pos)).isBurning()) {
            super.particles(state, world, pos, random);
        }
    }

}
