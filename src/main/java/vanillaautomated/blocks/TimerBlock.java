package vanillaautomated.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.blockentities.TimerBlockEntity;

import java.util.Random;

public class TimerBlock extends BlockWithEntity {
    public static final BooleanProperty ENABLED;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    static {
        ENABLED = Properties.ENABLED;
    }

    public TimerBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(ENABLED, false));
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TimerBlockEntity) {
                ((TimerBlockEntity) blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TimerBlockEntity) {
                world.updateComparators(pos, this);
            }
        }

        super.onStateReplaced(state, world, pos, newState, notify);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        BlockEntity be = world.getBlockEntity(pos);
        if (be != null && be instanceof TimerBlockEntity) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(VanillaAutomatedBlocks.interactWithTimer);
        }

        return ActionResult.SUCCESS;
    }

    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.getWeakRedstonePower(world, pos, direction);
    }

    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (!(Boolean) state.get(ENABLED)) {
            return 0;
        } else {
            return 15;
        }
    }

    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if ((Boolean) state.get(ENABLED)) {
            double d = (double) ((float) pos.getX() + 0.5F) + (double) (random.nextFloat() - 0.5F) * 0.02D;
            double e = (double) ((float) pos.getY() + 0.4F) + (double) (random.nextFloat() - 0.5F) * 0.02D;
            double f = (double) ((float) pos.getZ() + 0.5F) + (double) (random.nextFloat() - 0.5F) * 0.02D;
            int randomInt = random.nextInt(4);

            switch (randomInt) {
                case 0:
                    world.addParticle(DustParticleEffect.DEFAULT, d + 0.25f, e, f, 0.0D, 0.0D, 0.0D);
                    break;
                case 1:
                    world.addParticle(DustParticleEffect.DEFAULT, d, e, f + 0.25f, 0.0D, 0.0D, 0.0D);
                    break;
                case 2:
                    world.addParticle(DustParticleEffect.DEFAULT, d - 0.25f, e, f, 0.0D, 0.0D, 0.0D);
                    break;
                case 3:
                    world.addParticle(DustParticleEffect.DEFAULT, d, e, f - 0.25f, 0.0D, 0.0D, 0.0D);
                    break;
            }
        }
    }

    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return hasTopRim(world, pos.down());
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        if (state.canPlaceAt(world, pos)) {
            super.neighborUpdate(state, world, pos, block, fromPos, notify);
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            dropStacks(state, world, pos, blockEntity);
            world.removeBlock(pos, false);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TimerBlockEntity(pos, state);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ENABLED);
    }
}
