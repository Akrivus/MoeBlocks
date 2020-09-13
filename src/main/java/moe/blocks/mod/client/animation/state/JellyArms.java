package moe.blocks.mod.client.animation.state;

import moe.blocks.mod.client.Animations;
import moe.blocks.mod.client.animation.ActionAnimation;
import moe.blocks.mod.client.model.MoeModel;
import moe.blocks.mod.entity.MoeEntity;
import net.minecraft.util.math.MathHelper;

public class JellyArms extends ActionAnimation {
    @Override
    public int getInterval() {
        return 20;
    }

    @Override
    public void setMoeRotationAngles(MoeModel model, MoeEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks) {
        model.rightArm.rotateAngleZ -= 0.7853981633974483F * MathHelper.sin(ageInTicks);
        model.leftArm.rotateAngleZ -= -0.7853981633974483F * MathHelper.sin(ageInTicks);
        model.head.rotateAngleX = 0.2F;
        model.head.rotateAngleZ = 0.1F * MathHelper.sin(ageInTicks);
    }

    @Override
    public Animations getKey() {
        return Animations.JELLY_ARMS;
    }
}
