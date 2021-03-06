package block_party.blocks;

import block_party.client.particle.SakuraParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.fmllegacy.RegistryObject;

import java.util.Random;

public class SakuraBlossomsBlock extends LeavesBlock {
    public static final BooleanProperty BLOOMING = BooleanProperty.create("blooming");
    private final RegistryObject<SimpleParticleType> particle;

    public SakuraBlossomsBlock(RegistryObject<SimpleParticleType> particle, Properties properties) {
        super(properties.isValidSpawn((state, reader, pos, entity) -> false).isSuffocating((state, reader, pos) -> false).isViewBlocking((state, reader, pos) -> false));
        this.registerDefaultState(this.stateDefinition.any().setValue(DISTANCE, 7).setValue(PERSISTENT, false).setValue(BLOOMING, true));
        this.particle = particle;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos neighbor, boolean isMoving) {
        if (state.getValue(PERSISTENT)) { return; }
        this.bloomOrClose(state, pos, world);
    }

    private void bloomOrClose(BlockState state, BlockPos pos, Level world) {
        if (world.getMoonBrightness() == 1.0F) {
            world.setBlockAndUpdate(pos, state.setValue(BLOOMING, true));
        } else if (state.getValue(BLOOMING)) {
            world.setBlockAndUpdate(pos, state.setValue(BLOOMING, false));
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random random) {
        if (super.isRandomlyTicking(state)) {
            super.randomTick(state, world, pos, random);
        } else if (!state.getValue(PERSISTENT)) {
            this.bloomOrClose(state, pos, world);
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, Random random) {
        super.animateTick(state, world, pos, random);
        if (state.getValue(BLOOMING)) {
            SakuraParticle.add(this.particle, world, pos, random);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISTANCE, PERSISTENT, BLOOMING);
    }
}
