package moe.blocks.mod.entity.behavior;

import moe.blocks.mod.entity.util.Behaviors;

public class BeeNestBehavior extends BeehiveBehavior {
    @Override
    public void start() {
        super.start();
        this.moe.setCanFly(true);
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.BEE_NEST;
    }
}