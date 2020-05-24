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
        List<ItemEntity> entities = world.getEntities(EntityType.ITEM, new Box(currentPos.getX() - 3, currentPos.getY() - 3, currentPos.getZ() - 3, currentPos.getX() + 3, currentPos.getY() + 3, currentPos.getZ() + 3), null);

        for (int i = 0; i < entities.size(); i++){
            ItemEntity item = entities.get(i);
            double x = MathHelper.lerp(item.getPos().getX(), currentPos.getX(), 0.03f);
            double y = MathHelper.lerp(item.getPos().getY(), currentPos.getY(), 0.03f);
            double z = MathHelper.lerp(item.getPos().getZ(), currentPos.getZ(), 0.03f);
            item.requestTeleport(x, y, z);
        }
    }
}
