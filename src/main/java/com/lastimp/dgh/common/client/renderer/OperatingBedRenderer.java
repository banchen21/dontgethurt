package com.lastimp.dgh.common.client.renderer;

import com.lastimp.dgh.common.block.OperatingBedBlock;
import com.lastimp.dgh.common.utils.ResourceHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BrightnessCombiner;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

import static net.minecraft.client.renderer.Sheets.BED_SHEET;

public class OperatingBedRenderer implements BlockEntityRenderer<OperatingBedBlock.Entity> {
    private static final Material material = new Material(BED_SHEET, ResourceHelper.ModResource("entity/bed/operating_bed"));
    private final ModelPart headRoot;
    private final ModelPart footRoot;

    public OperatingBedRenderer(BlockEntityRendererProvider.Context context) {
        this.headRoot = context.bakeLayer(MyModelLayers.OPERATING_BED_HEAD);
        this.footRoot = context.bakeLayer(MyModelLayers.OPERATING_BED_FOOT);
    }

    @Override
    public void render(OperatingBedBlock.Entity entity, float v, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level level = entity.getLevel();
        if (level != null) {
            BlockState blockstate = entity.getBlockState();
            DoubleBlockCombiner.NeighborCombineResult<? extends BedBlockEntity> neighborcombineresult = DoubleBlockCombiner.combineWithNeigbour(BlockEntityType.BED, BedBlock::getBlockType, BedBlock::getConnectedDirection, ChestBlock.FACING, blockstate, level, entity.getBlockPos(), (p_112202_, p_112203_) -> false);
            Int2IntFunction brightness = neighborcombineresult.apply(new BrightnessCombiner<>());
            int i = brightness.get(packedLight);
            this.renderPiece(poseStack, buffer, blockstate.getValue(BedBlock.PART) == BedPart.HEAD ? this.headRoot : this.footRoot, blockstate.getValue(BedBlock.FACING), material, i, packedOverlay, false);
        } else {
            this.renderPiece(poseStack, buffer, this.headRoot, Direction.SOUTH, material, packedLight, packedOverlay, false);
            this.renderPiece(poseStack, buffer, this.footRoot, Direction.SOUTH, material, packedLight, packedOverlay, true);
        }
    }

    private void renderPiece(PoseStack poseStack, MultiBufferSource bufferSource, ModelPart modelPart, Direction direction, Material material, int packedLight, int packedOverlay, boolean foot) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.translate(-0.5F, -1.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F + direction.toYRot()));
        VertexConsumer vertexconsumer = material.buffer(bufferSource, RenderType::entitySolid);
        modelPart.render(poseStack, vertexconsumer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    public static LayerDefinition createHeadLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createFootLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}
