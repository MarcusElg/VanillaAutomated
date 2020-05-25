package vanillaautomated.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityInteraction;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class InfiniteTradesMixin {

    @Inject(method="onInteractionWith", at=@At("HEAD"))
    private void onInteractionWith(EntityInteraction interaction, Entity entity, CallbackInfo ci) {
        ((VillagerEntity)(Object)this).restock();
    }

}
