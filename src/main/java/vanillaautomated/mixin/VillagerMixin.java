package vanillaautomated.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vanillaautomated.VanillaAutomatedItems;

@Mixin(VillagerEntity.class)
public abstract class VillagerMixin {

    // Infinite trades
    @Inject(method="onInteractionWith", at=@At("HEAD"))
    private void onInteractionWith(EntityInteraction interaction, Entity entity, CallbackInfo ci) {
        ((VillagerEntity)(Object)this).restock();
    }

    // Be able to use mob net on villagers
    @Inject(method="interactMob", at=@At("HEAD"))
    public void interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable ci) {
        if (player.getStackInHand(hand).getItem() == VanillaAutomatedItems.mobNet) {
            return;
        }
    }

}
