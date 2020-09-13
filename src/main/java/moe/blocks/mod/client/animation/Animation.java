package moe.blocks.mod.client.animation;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.client.model.MoeModel;
import moe.blocks.mod.entity.MoeEntity;
import moe.blocks.mod.entity.partial.InteractEntity;

public abstract class Animation {

    public abstract void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks);

    public void render() {

    }

    public void tick(InteractEntity entity) {

    }

    @Override
    public String toString() {
        return this.getKey().name();
    }

    public abstract Animations getKey();
}
