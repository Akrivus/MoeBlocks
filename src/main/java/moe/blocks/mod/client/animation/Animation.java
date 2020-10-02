package moe.blocks.mod.client.animation;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.client.model.MoeModel;
import moe.blocks.mod.entity.AbstractNPCEntity;
import moe.blocks.mod.entity.MoeEntity;

public abstract class Animation {

    public String name() {
        return this.getKey().name();
    }

    public abstract Animations getKey();

    public void render() {

    }

    public abstract void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks);

    public void tick(AbstractNPCEntity entity) {

    }
}
