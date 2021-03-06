package block_party.blocks.entity;

import block_party.BlockPartyDB;
import block_party.db.records.Sapling;
import block_party.init.BlockPartyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SakuraSaplingBlockEntity extends AbstractDataBlockEntity<Sapling> {
    public SakuraSaplingBlockEntity(BlockPos pos, BlockState state) {
        super(BlockPartyBlockEntities.SAKURA_SAPLING.get(), pos, state);
    }

    @Override
    public Sapling getRow() {
        return BlockPartyDB.Saplings.find(this.getDatabaseID());
    }

    @Override
    public Sapling getNewRow() {
        return new Sapling(this);
    }
}
