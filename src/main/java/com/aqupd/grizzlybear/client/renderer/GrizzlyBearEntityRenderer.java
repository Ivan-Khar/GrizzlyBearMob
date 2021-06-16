package com.aqupd.grizzlybear.client.renderer;

import com.aqupd.grizzlybear.client.model.GrizzlyBearEntityModel;
import com.aqupd.grizzlybear.entities.GrizzlyBearEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class GrizzlyBearEntityRenderer extends MobEntityRenderer<GrizzlyBearEntity, GrizzlyBearEntityModel<GrizzlyBearEntity>> {
    private static final Identifier TEXTURE = new Identifier("aqupd", "textures/entity/grizzly_bear.png");

    public GrizzlyBearEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new GrizzlyBearEntityModel(), 0.9F);
    }

    public Identifier getTexture(GrizzlyBearEntity grizzlyBearEntity) {
        return TEXTURE;
    }

    protected void scale(GrizzlyBearEntity grizzlyBearEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(1.2F, 1.2F, 1.2F);
        super.scale(grizzlyBearEntity, matrixStack, f);
    }
}
