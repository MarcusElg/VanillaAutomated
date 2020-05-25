package vanillaautomated.blockentities;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import vanillaautomated.VanillaAutomatedBlocks;

import java.util.List;
import java.util.function.Predicate;

public class MagnetBlockEntity extends BlockEntity implements Tickable {
    public MagnetBlockEntity() {
        super(VanillaAutomatedBlocks.magnetBlockEntity);
    }

    @Override
    public void tick() {
        Vec3d currentPos = Vec3d.of(getPos());
        currentPos.add(0.5f, 0.5f, 0.5f);
        List<ItemEntity> entities = world.getEntities(EntityType.ITEM, new Box(currentPos.getX() - 5, currentPos.getY() - 5, currentPos.getZ() - 5, currentPos.getX() + 6, currentPos.getY() + 6, currentPos.getZ() + 6), new Predicate<ItemEntity>() {
            @Override
            public boolean test(ItemEntity itemEntity) {
                return true;
            }
        });

        for (int i = 0; i < entities.size(); i++){
            ItemEntity item = entities.get(i);

            if (!item.isOnGround()) {
                continue;
            }

            item.requestTeleport(currentPos.getX(), currentPos.getY(), currentPos.getZ());
        }
    }
}
