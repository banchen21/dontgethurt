package com.lastimp.dgh.common.block;
import com.lastimp.dgh.common.utils.Utils;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.entry.register.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class OperatingBedBlock extends BedBlock {
    protected static final VoxelShape BASE = Block.box(0.0F, 5.0F, 0.0F, 16.0F, 9.0F, 16.0F);
    public static final float AVA_DISTANCE = 2.0f;

    public OperatingBedBlock(Properties properties) {
        super(DyeColor.RED, properties);
        this.registerDefaultState(this.defaultBlockState());
    }


    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        level.scheduleTick(pos, this, 20);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        var entities = Utils.getLivingWithHealth(level, pos.getCenter(), 10);
        for (var entity : entities) {
            if (!EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(entity)) continue;
            if (entity.distanceToSqr(pos.getCenter()) > AVA_DISTANCE * AVA_DISTANCE) continue;
            HealthCapability.getAndApply(entity, h -> h.setNearBedTick(40));
        }
        level.scheduleTick(pos, this, 20);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return BASE;
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new OperatingBedBlock.Entity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (state.getValue(BedBlock.PART) == BedPart.HEAD) return Collections.emptyList();
        return super.getDrops(state, params);
    }

    public static class Entity extends BlockEntity {
        public Entity(BlockPos pos, BlockState blockState) {
            super(ModBlocks.OPERATING_BED_ENTITY.get(), pos, blockState);
        }

    }
}

