package vanillaautomated.blocks;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
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
import vanillaautomated.blockentities.FarmerBlockEntity;

import java.util.Random;

public class FarmerBlock extends MachineBlock {
    public FarmerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new FarmerBlockEntity();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FarmerBlockEntity) {
                ((FarmerBlockEntity) blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof FarmerBlockEntity) {
                ItemScatterer.spawn(world, pos, (Inventory) ((FarmerBlockEntity) blockEntity));
                world.updateComparators(pos, this);
            }
        }

        super.onBlockRemoved(state, world, pos, newState, notify);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.PASS;
        BlockEntity be = world.getBlockEntity(pos);
        if (be != null && be instanceof FarmerBlockEntity) {
            ContainerProviderRegistry.INSTANCE.openContainer(new Identifier(VanillaAutomated.prefix, "farmer_block"), player, (packetByteBuf -> {
                packetByteBuf.writeBlockPos(pos);
                packetByteBuf.writeText(((FarmerBlockEntity) be).getDisplayName());
            }));
            player.incrementStat(VanillaAutomatedBlocks.interact_with_farmer);
        }

        return ActionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (((FarmerBlockEntity) world.getBlockEntity(pos)).isBurning()) {
            super.particles(state, world, pos, random);
        }
    }
}
