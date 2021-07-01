package vanillaautomated.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import vanillaautomated.VanillaAutomated;
import vanillaautomated.VanillaAutomatedBlocks;
import vanillaautomated.blockentities.FarmerBlockEntity;

import java.util.Random;

public class FarmerBlock extends MachineBlock {
    public FarmerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FarmerBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof FarmerBlockEntity)) {
            return;
        }

        if (itemStack.hasCustomName()) {
            ((FarmerBlockEntity) blockEntity).setCustomName(itemStack.getName());
        }

        ((FarmerBlockEntity) blockEntity).setSpeed = VanillaAutomated.config.farmerTime;
        ((FarmerBlockEntity) blockEntity).spedUpSpeed = VanillaAutomated.config.bonemealedFarmerTime;
        ((FarmerBlockEntity) blockEntity).speed = ((FarmerBlockEntity) blockEntity).setSpeed;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FarmerBlockEntity) {
                ItemScatterer.spawn(world, pos, (Inventory) ((FarmerBlockEntity) blockEntity));
                world.updateComparators(pos, this);
            }
        }

        super.onStateReplaced(state, world, pos, newState, notify);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        BlockEntity be = world.getBlockEntity(pos);
        if (be != null && be instanceof FarmerBlockEntity) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            player.incrementStat(VanillaAutomatedBlocks.interactWithFarmer);
        }

        return ActionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (((FarmerBlockEntity) world.getBlockEntity(pos)).isBurning()) {
            super.particles(state, world, pos, random);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, VanillaAutomatedBlocks.farmerBlockEntity, FarmerBlockEntity::tick);
    }
}
