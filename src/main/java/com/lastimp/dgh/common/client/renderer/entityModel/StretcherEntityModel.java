package com.lastimp.dgh.common.client.renderer.entityModel;

import com.lastimp.dgh.common.entity.StretcherEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.*;

public class StretcherEntityModel extends EntityModel<StretcherEntity> {
    private final ModelPart root;

    public StretcherEntityModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    @Override
    public void setupAnim(StretcherEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float r, float g, float b, float a) {
        root.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
    }
}
