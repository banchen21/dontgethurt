package com.lastimp.dgh.common.client.renderer;

import com.lastimp.dgh.common.client.renderer.entityModel.StretcherEntityModel;
import com.lastimp.dgh.common.utils.ResourceHelper;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MyModelLayers {
    public static final ModelLayerLocation OPERATING_BED_HEAD = register("operating_bed_head", OperatingBedRenderer::createHeadLayer);
    public static final ModelLayerLocation OPERATING_BED_FOOT = register("operating_bed_foot", OperatingBedRenderer::createFootLayer);
    public static final ModelLayerLocation STRETCHER = register("stretcher", StretcherEntityModel::createBodyLayer);

    public static Map<ModelLayerLocation, Supplier<LayerDefinition>> layers;

    public static ModelLayerLocation register(String path, Supplier<LayerDefinition> supplier) {
        if (layers == null)
            layers = new HashMap<>();
        var newLayer = new ModelLayerLocation(ResourceHelper.ModResource(path), "main");
        layers.put(newLayer, supplier);
        return newLayer;
    }
}
