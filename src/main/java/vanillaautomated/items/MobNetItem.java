package vanillaautomated.items;

import blue.endless.jankson.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

public class MobNetItem extends Item {
    LivingEntity capturedEntity = null;

    public MobNetItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (capturedEntity != null){
            return false;
        }

        if (!(entity instanceof EnderDragonEntity) && !(entity instanceof WitherEntity) && !(entity instanceof PlayerEntity)) {
            // Capture mob
            capturedEntity = entity;
            entity.remove();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (capturedEntity == null) {
            return ActionResult.PASS;
        } else {
            // Release mob
            //capturedEntity.teleport(context.getBlockPos().getX(), context.getBlockPos().getY(), context.getBlockPos().getZ(), false);
            //capturedEntity.setInvulnerable(false);
            //capturedEntity.setInvisible(false);
            capturedEntity.teleport(context.getBlockPos().offset(context.getPlayerFacing()).getX(), context.getBlockPos().offset(context.getPlayerFacing()).getY(), context.getBlockPos().offset(context.getPlayerFacing()).getZ());
            capturedEntity.createSpawnPacket();
            context.getWorld().spawnEntity(capturedEntity);
            capturedEntity = null;
            return ActionResult.SUCCESS;
        }
    }

    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (capturedEntity != null) {
            tooltip.add((new TranslatableText("Captured mob: ").append(capturedEntity.getName())).formatted(Formatting.GRAY));
        }
    }
}
