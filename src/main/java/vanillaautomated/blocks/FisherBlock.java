package vanillaautomated.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.blockentities.FisherBlockEntity;

import java.util.Random;

public class FisherBlock extends MachineBlock {

    public static final DirectionProperty FACING;

    public FisherBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(POWERED, false));
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
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FisherBlockEntity) {
                ItemScatterer.spawn(world, pos, (Inventory) ((FisherBlockEntity) blockEntity));
                world.updateComparators(pos, this);
            }
        }

        super.onStateReplaced(state, world, pos, newState, notify);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        BlockEntity be = world.getBlockEntity(pos);
        if (be != null && be instanceof FisherBlockEntity) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(VanillaAutomatedBlocks.interactWithFisher);
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

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(POWERED);
    }

    static {
        FACING = HorizontalFacingBlock.FACING;
    }
}
