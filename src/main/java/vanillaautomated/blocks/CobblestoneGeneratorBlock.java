package vanillaautomated.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.blockentities.CobblestoneGeneratorBlockEntity;

import java.util.Random;
import java.util.logging.Logger;

public class CobblestoneGeneratorBlock extends MachineBlock {

    public CobblestoneGeneratorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new CobblestoneGeneratorBlockEntity();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CobblestoneGeneratorBlockEntity) {
                ((CobblestoneGeneratorBlockEntity) blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CobblestoneGeneratorBlockEntity) {
                ItemScatterer.spawn(world, pos, (Inventory) ((CobblestoneGeneratorBlockEntity) blockEntity));
                world.updateComparators(pos, this);
            }
        }

        super.onStateReplaced(state, world, pos, newState, notify);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        BlockEntity be = world.getBlockEntity(pos);
        if (be != null && be instanceof CobblestoneGeneratorBlockEntity) {
            Logger.getAnonymousLogger().warning("TEST");
            Logger.getAnonymousLogger().warning(state.createScreenHandlerFactory(world, pos) + "");
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(VanillaAutomatedBlocks.interactWithCobblestoneGenerator);
        }

        return ActionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (((CobblestoneGeneratorBlockEntity) world.getBlockEntity(pos)).isBurning()) {
            super.particles(state, world, pos, random);
        }
    }
}
