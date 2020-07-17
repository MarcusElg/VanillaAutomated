package vanillaautomated.items;

import blue.endless.jankson.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class MobNetItem extends Item {

    public MobNetItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (stack.hasTag() && stack.getTag().contains("EntityId")) {
            return ActionResult.PASS;
        }

        if (!(entity instanceof LivingEntity) || entity instanceof EnderDragonEntity || entity instanceof WitherEntity || entity instanceof PlayerEntity || entity instanceof FishEntity) {
            return ActionResult.PASS;
        }

        // Capture mob
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("EntityId", Registry.ENTITY_TYPE.getId(entity.getType()).toString());

        CompoundTag entityData = new CompoundTag();
        entity.saveSelfToTag(entityData);
        entityData.remove("UUID");

        tag.put("EntityData", entityData);
        stack.setTag(tag);
        user.setStackInHand(hand, stack);
        entity.remove();
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack itemStack = context.getStack();

        if (!itemStack.hasTag() || !itemStack.getTag().contains("EntityId")) {
            return ActionResult.PASS;
        } else {
            // Release mob
            CompoundTag tag = itemStack.getTag();
            CompoundTag entityData = tag.getCompound("EntityData");
            entityData.remove("Passengers");
            entityData.remove("Leash");
            entityData.remove("UUID");

            Entity entity = EntityType.loadEntityWithPassengers(entityData, context.getWorld(), (entityx) -> {
                return entityx;
            });

            // Get position (copied from Spawn Egg Item)
            BlockPos blockPos = context.getBlockPos();
            Direction direction = context.getSide();
            BlockState blockState = context.getWorld().getBlockState(blockPos);

            BlockPos position;
            if (blockState.getCollisionShape(context.getWorld(), blockPos).isEmpty()) {
                position = blockPos;
            } else {
                position = blockPos.offset(direction);
            }

            // Spawn entity
            entity.resetPosition(position.getX() + 0.5f, position.getY() + 0.5f, position.getZ() + 0.5f);
            entity.updatePosition(position.getX() + 0.5f, position.getY() + 0.5f, position.getZ() + 0.5f);
            context.getWorld().spawnEntity(entity);

            // Reset mob net
            tag.remove("EntityId");
            tag.remove("EntityData");
            itemStack.setTag(tag);
            context.getPlayer().setStackInHand(context.getHand(), itemStack);

            return ActionResult.SUCCESS;
        }
    }

    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (stack.hasTag() && stack.getTag().contains("EntityId")) {
            tooltip.add((new TranslatableText("Captured mob: ").append(new TranslatableText("entity." + stack.getTag().getString("EntityId").replace(":", "."))).formatted(Formatting.GRAY)));
        }
    }
}
