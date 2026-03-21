package com.lastimp.dgh.forge.capability.provider;

import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.forge.entry.register.ModCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiseaseProvider implements ICapabilitySerializable<CompoundTag> {
    private final DiseaseCapability impl = new DiseaseCapability();
    private final LazyOptional<DiseaseCapability> optional = LazyOptional.of(() -> impl);

    @Override
    public CompoundTag serializeNBT() {
        return impl.serialize();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        impl.deserialize(nbt);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ModCapabilities.DISEASE) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }
}