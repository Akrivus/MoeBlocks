package mod.moeblocks.entity;

import mod.moeblocks.entity.util.Deres;
import mod.moeblocks.register.BlocksMoe;
import mod.moeblocks.register.EntityTypesMoe;
import mod.moeblocks.register.ItemsMoe;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class MoeDieEntity extends DieEntity {
    protected BlockState blockStateForSpawn;
    protected Deres dere;
    protected int timeUntilSpawned;

    public MoeDieEntity(EntityType<MoeDieEntity> type, World world) {
        super(type, world);
    }

    public MoeDieEntity(World world, double x, double y, double z) {
        super(EntityTypesMoe.MOE_DIE.get(), world, x, y, z);
    }

    public MoeDieEntity(World world, LivingEntity thrower) {
        super(EntityTypesMoe.MOE_DIE.get(), world, thrower);
    }

    @Override
    public boolean onActionTick() {
        --this.timeUntilSpawned;
        if (this.timeUntilSpawned < 0) {
            BlockState state = this.world.getBlockState(this.getPositionUnderneath());
            TileEntity extra = this.world.getTileEntity(this.getPositionUnderneath());
            if (state.equals(this.blockStateForSpawn)) {
                MoeEntity moe = EntityTypesMoe.MOE.get().create(this.world);
                moe.setPositionAndRotation(this.getPosX(), this.getPosY(), this.getPosZ(), this.rotationYaw, this.rotationPitch);
                moe.setBlockData(state);
                moe.setExtraBlockData(extra != null ? extra.getTileData() : new CompoundNBT());
                moe.setDere(this.dere);
                moe.getRelationships().get(this.getPlayer() != null ? this.getPlayer().getUniqueID() : UUID.randomUUID()).addTrust(100);
                if (this.world.addEntity(moe)) {
                    this.world.setBlockState(this.getPositionUnderneath(), Blocks.AIR.getDefaultState());
                    return false;
                }
            } else {
                this.entityDropItem(this.getDefaultItem());
                return false;
            }
        }
        return true;
    }

    @Override
    protected Item getDefaultItem() {
        return ItemsMoe.MOE_DIE.get();
    }

    @Override
    public boolean onActionStart(BlockState state, BlockPos pos, Face face) {
        if (state.getBlock().isIn(BlocksMoe.Tags.MOEABLES)) {
            this.blockStateForSpawn = state;
            this.dere = Deres.from(face);
            this.timeUntilSpawned = 30;
            return true;
        }
        return false;
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("TimeUntilSpawned", this.timeUntilSpawned);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.timeUntilSpawned = compound.getInt("TimeUntilSpawned");
    }
}
