package vanillaautomated.gui;

import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.item.ItemStack;

public class WItemSprite extends WWidget {
    ItemStack itemStack;

    public WItemSprite (ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public void setItem (ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItem () {
        return itemStack;
    }
   /*
    @Override
    @Environment(EnvType.CLIENT)
    public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) { // ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed
        renderGuiItemModel(itemStack, x + 1, y + 1, MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(itemStack, (World)null, null, 0));
    }


    // Modified method from ItemRenderer
    @Environment(EnvType.CLIENT)
    private void renderGuiItemModel(ItemStack stack, int x, int y, BakedModel model) {
        RenderSystem.pushMatrix();
        MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.blendColor(1, 1, 1, 0.5f);
        RenderSystem.color4f(1.0F, 1.0F, 0F, 0F);
        RenderSystem.translatef((float)x, (float)y, 100.0F + 50);
        RenderSystem.translatef(8.0F, 8.0F, 0.0F);
        RenderSystem.scalef(1.0F, -1.0F, 1.0F);
        RenderSystem.scalef(16.0F, 16.0F, 16.0F);
        MatrixStack matrixStack = new MatrixStack();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }

        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack, immediate, 2000, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }

        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
    }

     */
}
