package moe.blocks.mod.entity.ai.goal;


import moe.blocks.mod.entity.partial.NPCEntity;
import moe.blocks.mod.init.MoeTags;
import net.minecraft.block.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OpenDoorGoal extends Goal {
    private final NPCEntity entity;
    private BlockPos pos = BlockPos.ZERO;
    private boolean hasStoppedDoorInteraction;
    private float x;
    private float z;
    private int timeUntilClosed;

    public OpenDoorGoal(NPCEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean shouldExecute() {
        Path path = this.entity.getNavigator().getPath();
        if (path == null || path.isFinished() || !this.entity.collidedHorizontally) { return false; }
        for (int i = 0; i < 2; ++i) {
            this.pos = this.entity.getPosition().offset(this.entity.getHorizontalFacing(), i);
            if (canOpenDoor(this.entity.world, this.pos)) { return true; }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !this.hasStoppedDoorInteraction && --this.timeUntilClosed > 0;
    }

    @Override
    public void startExecuting() {
        this.hasStoppedDoorInteraction = false;
        this.x = (float) ((this.pos.getX() + 0.5F) - this.entity.getPosX());
        this.z = (float) ((this.pos.getZ() + 0.5F) - this.entity.getPosZ());
        this.timeUntilClosed = 20;
        this.setDoorState(true);
    }

    protected void setDoorState(boolean open) {
        BlockState state = this.entity.world.getBlockState(this.pos);
        Block block = state.getBlock();
        if (block instanceof FenceGateBlock) {
            this.entity.world.setBlockState(this.pos, state.with(FenceGateBlock.OPEN, open));
            this.entity.world.playEvent(null, open ? 1008 : 1014, this.pos, 0);
        }
        if (block instanceof DoorBlock) {
            this.entity.world.setBlockState(this.pos, state.with(DoorBlock.OPEN, open));
            this.entity.world.playEvent(null, open ? 1006 : 1012, this.pos, 0);
        }
        if (block instanceof TrapDoorBlock) {
            this.entity.world.setBlockState(this.pos, state.with(TrapDoorBlock.OPEN, open));
            this.entity.world.playEvent(null, open ? 1007 : 1013, this.pos, 0);
        }
    }

    @Override
    public void resetTask() {
        this.setDoorState(false);
    }

    @Override
    public void tick() {
        float dX = (float) ((this.pos.getX() + 0.5F) - this.entity.getPosX());
        float dZ = (float) ((this.pos.getZ() + 0.5F) - this.entity.getPosZ());
        float dD = this.x * dX + this.z * dZ;
        this.hasStoppedDoorInteraction |= dD < 0.0F;
    }

    public static boolean canOpenDoor(World world, BlockPos pos) {
        return world.getBlockState(pos).isIn(MoeTags.DOORS);
    }
}