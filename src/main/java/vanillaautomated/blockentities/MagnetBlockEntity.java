package vanillaautomated.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vanillaautomated.VanillaAutomatedBlocks;

import java.util.List;

public class MagnetBlockEntity extends BlockEntity {

    int cooldown = 0;

    public MagnetBlockEntity(BlockPos pos, BlockState state) {
        super(VanillaAutomatedBlocks.magnetBlockEntity, pos, state);
    }

    private static boolean insert(World world, BlockPos pos, BlockState state, ItemStack stack) {
        Inventory inventory2 = (Inventory) world.getBlockEntity(pos);

        if (inventory2 != null) {
            for (int i = 0; i < inventory2.size(); i++) {
                ItemStack curr = inventory2.getStack(i);
                if (inventory2.getStack(i).isEmpty()) {
                    inventory2.setStack(i, stack.copy());
                    inventory2.markDirty();
                    return true;
                }
                if (curr.isItemEqual(stack) && curr.isStackable() && curr.getCount() < curr.getMaxCount()) {
                    curr.setCount(curr.getCount() + 1);
                    inventory2.setStack(i, curr);
                    inventory2.markDirty();
                    return true;
                }
            }
        }
        return false;
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, MagnetBlockEntity t) {
        if (world.isClient()) {
            return;
        }
        // only pick up items every 8 ticks
        t.cooldown++;
        if (t.cooldown % 8 != 0) {
            return;
        }

        Vec3d currentPos = Vec3d.of(blockPos);
        List<ItemEntity> entities = world.getEntitiesByType(EntityType.ITEM, new Box(currentPos.getX() - 5, currentPos.getY() - 5, currentPos.getZ() - 5, currentPos.getX() + 6, currentPos.getY() + 6, currentPos.getZ() + 6), itemEntity -> true);

        for (int i = 0; i < entities.size(); i++) {
            ItemEntity item = entities.get(i);

            if (!item.isOnGround()) {
                continue;
            }

            if (insert(world, blockPos.down(), world.getBlockState(blockPos.down()), item.getStack())) {
                item.getStack().setCount(0);
            }
        }
    }
}
