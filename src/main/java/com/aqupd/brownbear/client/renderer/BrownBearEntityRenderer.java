package com.aqupd.brownbear.client.renderer;

import com.aqupd.brownbear.client.model.BrownBearEntityModel;
import com.aqupd.brownbear.entities.BrownBearEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BrownBearEntityRenderer extends MobEntityRenderer<BrownBearEntity, BrownBearEntityModel<BrownBearEntity>> {
    private static final Identifier TEXTURE = new Identifier("aqupd", "textures/entity/brownbear.png");

    public BrownBearEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new BrownBearEntityModel(), 0.9F);
    }

    public Identifier getTexture(BrownBearEntity brownBearEntity) {
        return TEXTURE;
    }

    protected void scale(BrownBearEntity brownBearEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(1.2F, 1.2F, 1.2F);
        super.scale(brownBearEntity, matrixStack, f);
    }
}
