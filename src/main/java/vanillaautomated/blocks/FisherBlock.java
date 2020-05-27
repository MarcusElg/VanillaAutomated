package vanillaautomated.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.blockentities.FisherBlockEntity;

import java.util.Random;

public class FisherBlock extends MachineBlock {

    public FisherBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new FisherBlockEntity();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FisherBlockEntity) {
                ((FisherBlockEntity) blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FisherBlockEntity) {
                ItemScatterer.spawn(world, pos, (Inventory) ((FisherBlockEntity) blockEntity));
                world.updateComparators(pos, this);
            }
        }

        super.onBlockRemoved(state, world, pos, newState, notify);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.PASS;
        BlockEntity be = world.getBlockEntity(pos);
        if (be != null && be instanceof FisherBlockEntity) {
            ContainerProviderRegistry.INSTANCE.openContainer(new Identifier(VanillaAutomated.prefix, "fisher_block"), player, (packetByteBuf -> {
                packetByteBuf.writeBlockPos(pos);
                packetByteBuf.writeText(((FisherBlockEntity) be).getDisplayName());
            } ));
            player.incrementStat(VanillaAutomatedBlocks.interact_with_fisher);
        }

        return ActionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (((FisherBlockEntity) world.getBlockEntity(pos)).isBurning()) {
            super.particles(state, world, pos, random);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        FisherBlockEntity blockEntity = (FisherBlockEntity)world.getBlockEntity(pos);
        Direction direction1 = (Direction) state.get(FACING);
        pos = new BlockPos(pos.getX() + direction1.getOffsetX(), pos.getY() + direction1.getOffsetY(), pos.getZ() + direction1.getOffsetZ());

        if (world.getBlockState(pos) != null && world.getBlockState(pos).getBlock() == Blocks.WATER) {
            blockEntity.hasWater = true;
        } else {
            blockEntity.hasWater = false;
        }

        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }
}
