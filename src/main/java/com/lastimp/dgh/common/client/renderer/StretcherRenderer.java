package com.lastimp.dgh.common.client.renderer;

import com.lastimp.dgh.common.entity.StretcherEntity;
import com.lastimp.dgh.common.client.renderer.entityModel.StretcherEntityModel;
import com.lastimp.dgh.common.utils.ResourceHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class StretcherRenderer extends EntityRenderer<StretcherEntity> {
    private static final ResourceLocation TEXTURE = ResourceHelper.ModResource("textures/entity/stretcher.png");
    private final StretcherEntityModel model;

    public StretcherRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new StretcherEntityModel(context.bakeLayer(MyModelLayers.STRETCHER));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull StretcherEntity p_114482_) {
        return TEXTURE;
    }

    @Override
    public void render(StretcherEntity entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        // 水平躺放
        poseStack.translate(0, 1.5f, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - yaw));

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
    }
}
