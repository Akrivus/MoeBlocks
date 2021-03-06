package block_party.db.sql;

import block_party.mob.automata.ITrait;
import block_party.util.DimBlockPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Function;

public abstract class Column<I> {
    protected final String type;
    protected final String column;
    protected final String extra;
    protected I value;
    private long prev;
    private long time;

    public Column(Table table, String type, String column, String extra) {
        this.prev = this.time = System.nanoTime();
        this.type = type;
        this.column = column;
        this.extra = extra;
    }

    public Column(Table table, String type, String column) {
        this(table, type, column, "");
    }

    public abstract void forSet(int i, PreparedStatement statement) throws SQLException;

    public abstract I fromSet(int i, ResultSet set) throws SQLException;

    public abstract void read(CompoundTag compound);

    public abstract void write(CompoundTag compound);

    public abstract void afterAdd(Table table);

    public I get() {
        return this.value == null ? this.getDefault() : this.value;
    }

    public void setFromSet(int i, ResultSet set) throws SQLException {
        this.set(this.fromSet(i, set));
    }

    public void setValue(I value) {
        this.value = value;
    }

    public void set(I value) {
        if (this.value != value) {
            this.prev = this.time;
            this.time = System.nanoTime();
            this.setValue(value);
        }
    }

    public boolean isDirty() {
        return this.time > this.prev;
    }

    public String getType() {
        return this.type;
    }

    public String getColumn() {
        return this.column;
    }

    public String getExtra() {
        return this.extra;
    }

    public I getDefault() {
        return null;
    }

    public static class AsInteger extends Column<Integer> {
        public AsInteger(Table table, String column, String extra) {
            super(table, "INTEGER", column, extra);
        }

        public AsInteger(Table table, String column) {
            this(table, column, "NOT NULL DEFAULT 0");
        }

        @Override
        public void forSet(int i, PreparedStatement statement) throws SQLException {
            statement.setInt(i, this.get());
        }

        @Override
        public Integer fromSet(int i, ResultSet set) throws SQLException {
            return set.getInt(i);
        }

        @Override
        public void read(CompoundTag compound) {
            this.set(compound.getInt(this.getColumn()));
        }

        @Override
        public void write(CompoundTag compound) {
            compound.putInt(this.getColumn(), this.get());
        }

        @Override
        public void afterAdd(Table table) { }

        @Override
        public Integer getDefault() {
            return 0;
        }
    }

    public static class AsLong extends Column<Long> {
        public AsLong(Table table, String column, String extra) {
            super(table, "INTEGER", column, extra);
        }

        public AsLong(Table table, String column) {
            this(table, column, "NOT NULL DEFAULT 0");
        }

        @Override
        public void forSet(int i, PreparedStatement statement) throws SQLException {
            statement.setLong(i, this.get());
        }

        @Override
        public Long fromSet(int i, ResultSet set) throws SQLException {
            return set.getLong(i);
        }

        @Override
        public void read(CompoundTag compound) {
            this.set(compound.getLong(this.getColumn()));
        }

        @Override
        public void write(CompoundTag compound) {
            compound.putLong(this.getColumn(), this.get());
        }

        @Override
        public void afterAdd(Table table) { }

        @Override
        public Long getDefault() {
            return 0L;
        }
    }

    public static class AsBoolean extends Column<Boolean> {
        public AsBoolean(Table table, String column, String extra) {
            super(table, "INTEGER", column, extra);
        }

        public AsBoolean(Table table, String column) {
            this(table, column, "NOT NULL DEFAULT 0");
        }

        @Override
        public void forSet(int i, PreparedStatement statement) throws SQLException {
            statement.setBoolean(i, this.get());
        }

        @Override
        public Boolean fromSet(int i, ResultSet set) throws SQLException {
            return set.getBoolean(i);
        }

        @Override
        public void read(CompoundTag compound) {
            this.set(compound.getBoolean(this.getColumn()));
        }

        @Override
        public void write(CompoundTag compound) {
            compound.putBoolean(this.getColumn(), this.get());
        }

        @Override
        public void afterAdd(Table table) { }

        @Override
        public Boolean getDefault() {
            return false;
        }
    }

    public static class AsFloat extends Column<Float> {
        public AsFloat(Table table, String column, String extra) {
            super(table, "REAL", column, extra);
        }

        public AsFloat(Table table, String column) {
            this(table, column, "NOT NULL DEFAULT 0.0");
        }

        @Override
        public void forSet(int i, PreparedStatement statement) throws SQLException {
            statement.setDouble(i, this.get());
        }

        @Override
        public Float fromSet(int i, ResultSet set) throws SQLException {
            return set.getFloat(i);
        }

        @Override
        public void read(CompoundTag compound) {
            this.set(compound.getFloat(this.getColumn()));
        }

        @Override
        public void write(CompoundTag compound) {
            compound.putFloat(this.getColumn(), this.get());
        }

        @Override
        public void afterAdd(Table table) { }

        @Override
        public Float getDefault() {
            return 0.0F;
        }
    }

    public static class AsBlockState extends Column<BlockState> {
        public AsBlockState(Table table, String column, String extra) {
            super(table, "INTEGER", column, extra);
        }

        public AsBlockState(Table table, String column) {
            this(table, column, "NOT NULL DEFAULT 0");
        }

        @Override
        public void forSet(int i, PreparedStatement statement) throws SQLException {
            statement.setInt(i, Block.getId(this.get()));
        }

        @Override
        public BlockState fromSet(int i, ResultSet set) throws SQLException {
            return Block.stateById(set.getInt(i));
        }

        @Override
        public void read(CompoundTag compound) {
            this.set(Block.stateById(compound.getInt(this.getColumn())));
        }

        @Override
        public void write(CompoundTag compound) {
            compound.putInt(this.getColumn(), Block.getId(this.get()));
        }

        @Override
        public void afterAdd(Table table) { }

        @Override
        public BlockState getDefault() {
            return Blocks.AIR.defaultBlockState();
        }
    }

    public static class AsItem extends Column<Item> {
        public AsItem(Table table, String column, String extra) {
            super(table, "TEXT", column, extra);
        }

        public AsItem(Table table, String column) {
            this(table, column, "NOT NULL DEFAULT 'minecraft:air'");
        }

        @Override
        public void forSet(int i, PreparedStatement statement) throws SQLException {
            statement.setString(i, this.get().getRegistryName().toString());
        }

        @Override
        public Item fromSet(int i, ResultSet set) throws SQLException {
            return ForgeRegistries.ITEMS.getValue(new ResourceLocation(set.getString(i)));
        }

        @Override
        public void read(CompoundTag compound) {
            this.set(ForgeRegistries.ITEMS.getValue(new ResourceLocation(compound.getString(this.getColumn()))));
        }

        @Override
        public void write(CompoundTag compound) {
            compound.putString(this.getColumn(), this.get().getRegistryName().toString());
        }

        @Override
        public void afterAdd(Table table) {

        }

        @Override
        public Item getDefault() {
            return Items.AIR;
        }
    }

    public static class AsString extends Column<String> {
        public AsString(Table table, String column, String extra) {
            super(table, "TEXT", column, extra);
        }

        public AsString(Table table, String column) {
            this(table, column, "NOT NULL DEFAULT ''");
        }

        @Override
        public void forSet(int i, PreparedStatement statement) throws SQLException {
            statement.setString(i, this.get());
        }

        @Override
        public String fromSet(int i, ResultSet set) throws SQLException {
            return set.getString(i);
        }

        @Override
        public void read(CompoundTag compound) {
            this.set(compound.getString(this.getColumn()));
        }

        @Override
        public void write(CompoundTag compound) {
            compound.putString(this.getColumn(), this.get());
        }

        @Override
        public void afterAdd(Table table) { }

        @Override
        public String getDefault() {
            return "";
        }
    }

    public static class AsUUID extends Column<UUID> {
        public AsUUID(Table table, String column, String extra) {
            super(table, "TEXT", column, extra);
        }

        public AsUUID(Table table, String column) {
            this(table, column, "NOT NULL DEFAULT '00000000-0000-0000-0000-000000000000'");
        }

        @Override
        public void forSet(int i, PreparedStatement statement) throws SQLException {
            statement.setString(i, this.get().toString());
        }

        @Override
        public UUID fromSet(int i, ResultSet set) throws SQLException {
            return UUID.fromString(set.getString(i));
        }

        @Override
        public void read(CompoundTag compound) {
            this.set(compound.getUUID(this.getColumn()));
        }

        @Override
        public void write(CompoundTag compound) {
            compound.putUUID(this.getColumn(), this.get());
        }

        @Override
        public void afterAdd(Table table) { }

        @Override
        public UUID getDefault() {
            return new UUID(0, 0);
        }
    }

    public static class AsPosition extends Column<DimBlockPos> {
        private Column x;
        private Column y;
        private Column z;

        public AsPosition(Table table, String column) {
            super(table, "TEXT", String.format("%sDimension", column));
        }

        @Override
        public void set(DimBlockPos value) {
            super.set(value);
            if (this.isDirty()) {
                this.x.set(value.getPos().getX());
                this.y.set(value.getPos().getY());
                this.z.set(value.getPos().getZ());
            }
        }

        @Override
        public void forSet(int i, PreparedStatement statement) throws SQLException {
            statement.setString(i, this.get().getDim().location().toString());
            statement.setInt(i + 1, this.get().getPos().getX());
            statement.setInt(i + 2, this.get().getPos().getY());
            statement.setInt(i + 3, this.get().getPos().getZ());
        }

        @Override
        public DimBlockPos fromSet(int i, ResultSet set) throws SQLException {
            return new DimBlockPos(
                    ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(set.getString(i))),
                    new BlockPos(
                            set.getInt(i + 1),
                            set.getInt(i + 2),
                            set.getInt(i + 3)
                    )
            );
        }

        @Override
        public void read(CompoundTag compound) {
            this.set(DimBlockPos.fromNBT(compound.getCompound(this.getColumn())));
        }

        @Override
        public void write(CompoundTag compound) {
            compound.put(this.getColumn(), this.get().write());
        }

        @Override
        public void afterAdd(Table table) {
            table.addColumn(this.x = new AsInteger(table, String.format("%sX", this.column)));
            table.addColumn(this.y = new AsInteger(table, String.format("%sY", this.column)));
            table.addColumn(this.z = new AsInteger(table, String.format("%sZ", this.column)));
        }

        @Override
        public DimBlockPos getDefault() {
            return new DimBlockPos(Level.OVERWORLD, BlockPos.ZERO);
        }
    }

    public static class AsTrait<T extends ITrait> extends Column<T> {
        private final T trait;

        public AsTrait(Table table, String column, T trait) {
            super(table, "TEXT", column, String.format("NOT NULL DEFAULT '%s'", trait.getValue()));
            this.trait = trait;
        }

        @Override
        public void forSet(int i, PreparedStatement statement) throws SQLException {
            statement.setString(i, this.value == null ? this.trait.getValue() : this.value.getValue());
        }

        @Override
        public T fromSet(int i, ResultSet set) throws SQLException {
            return (T) this.trait.fromValue(set.getString(i));
        }

        @Override
        public void read(CompoundTag compound) {
            this.set((T) this.get().read(compound));
        }

        @Override
        public void write(CompoundTag compound) {
            this.get().write(compound);
        }

        @Override
        public void afterAdd(Table table) { }

        @Override
        public T getDefault() {
            return this.trait;
        }
    }

    public static class AsReference<R extends Record> extends Column<R> {
        private final Function<UUID, R> query;

        public AsReference(Table table, String column, Function<UUID, R> query) {
            super(table, "TEXT", column);
            this.query = query;
        }

        @Override
        public void forSet(int i, PreparedStatement statement) throws SQLException {
            statement.setString(i, this.get() == null ? "00000000-0000-0000-0000-000000000000" : this.get().getID().toString());
        }

        @Override
        public R fromSet(int i, ResultSet set) throws SQLException {
            UUID uuid = UUID.fromString(set.getString(i));
            if (uuid.toString().equals("00000000-0000-0000-0000-000000000000")) { return null; }
            return this.query.apply(uuid);
        }

        @Override
        public void read(CompoundTag compound) { }

        @Override
        public void write(CompoundTag compound) { }

        @Override
        public void afterAdd(Table table) { }

        @Override
        public R getDefault() {
            return null;
        }
    }
}
