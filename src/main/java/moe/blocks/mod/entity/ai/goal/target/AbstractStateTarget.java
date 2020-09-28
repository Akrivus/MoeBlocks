package moe.blocks.mod.entity.ai.goal.target;

import moe.blocks.mod.entity.ai.automata.IStateGoal;
import moe.blocks.mod.entity.partial.NPCEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.EnumSet;
import java.util.List;

public abstract class AbstractStateTarget extends Goal implements IStateGoal {
    protected final NPCEntity entity;
    protected LivingEntity target;

    public AbstractStateTarget(NPCEntity entity) {
        this.entity = entity;
        this.setMutexFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean shouldExecute() {
        return this.setTarget() && this.entity.canSee(this.target);
    }

    @Override
    public boolean shouldContinueExecuting() {
        this.target = this.entity.getAttackTarget();
        return this.entity.canBeTarget(this.target);
    }

    @Override
    public void startExecuting() {
        this.entity.setAttackTarget(this.target);
    }

    @Override
    public void resetTask() {
        this.entity.setAttackTarget(null);
    }

    public abstract boolean setTarget();
}