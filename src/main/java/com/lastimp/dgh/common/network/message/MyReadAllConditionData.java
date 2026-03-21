
package com.lastimp.dgh.common.network.message;

import com.lastimp.dgh.common.capability.DiseaseCapability;
import com.lastimp.dgh.common.capability.HealthCapability;
import com.lastimp.dgh.common.enums.OperationType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public class MyReadAllConditionData {
    private long id_most;
    private long id_least;
    private int entityID;
    private CompoundTag tag;
    private CompoundTag extraTag;
    private String oper;

    public MyReadAllConditionData(FriendlyByteBuf buffer) {
        this.id_most = buffer.readLong();
        this.id_least = buffer.readLong();
        this.entityID = buffer.readInt();
        this.tag = buffer.readNbt();
        this.extraTag = buffer.readNbt();
        this.oper = buffer.readUtf();
    }

    public MyReadAllConditionData(UUID uuid, int entityID, CompoundTag tag, OperationType operation) {
        this(uuid, entityID, tag, null, operation);
    }

    public MyReadAllConditionData(UUID uuid, int entityID, CompoundTag tag, CompoundTag extraTag, OperationType operation) {
        this.id_most = uuid.getMostSignificantBits();
        this.id_least = uuid.getLeastSignificantBits();
        this.entityID = entityID;
        this.tag = tag;
        this.extraTag = extraTag;
        this.oper = operation.name();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeLong(this.id_most);
        buf.writeLong(this.id_least);
        buf.writeInt(this.entityID);
        buf.writeNbt(this.tag);
        buf.writeNbt(this.extraTag);
        buf.writeUtf(this.oper);
    }

    public static MyReadAllConditionData getInstance(UUID uuid, int entityID, CompoundTag tag, OperationType operation) {
        return new MyReadAllConditionData(uuid, entityID, tag, operation);
    }

    public static MyReadAllConditionData getInstance(UUID uuid, int entityID, CompoundTag tag, CompoundTag extraTag, OperationType operation) {
        return new MyReadAllConditionData(uuid, entityID, tag, extraTag, operation);
    }

    public static HealthCapability getHealthFromInstance(CompoundTag tag) {
        HealthCapability health = new HealthCapability();
        health.deserialize(tag);
        return health;
    }

    public static DiseaseCapability getDiseaseFromInstance(CompoundTag tag) {
        DiseaseCapability disease = new DiseaseCapability();
        if (tag != null) {
            disease.deserialize(tag);
        }
        return disease;
    }

    public long id_least() {
        return id_least;
    }

    public long id_most() {
        return id_most;
    }

    public int entityID() {
        return entityID;
    }

    public String oper() {
        return oper;
    }

    public CompoundTag tag() {
        return tag;
    }

    public CompoundTag extraTag() {
        return extraTag;
    }
}
