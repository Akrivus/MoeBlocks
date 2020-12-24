package moeblocks.entity;

import moeblocks.automata.Automaton;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.state.*;
import moeblocks.automata.state.Animation;
import moeblocks.datingsim.CacheNPC;
import moeblocks.datingsim.DatingData;
import moeblocks.datingsim.DatingSim;
import moeblocks.entity.ai.VoiceLines;
import moeblocks.entity.ai.goal.*;
import moeblocks.entity.ai.goal.attack.BasicAttackGoal;
import moeblocks.entity.ai.goal.items.ConsumeGoal;
import moeblocks.entity.ai.goal.target.RevengeTarget;
import moeblocks.init.MoeItems;
import moeblocks.init.MoeTags;
import moeblocks.util.sort.EntityDistance;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.controller.FlyingMovementController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;
import java.util.function.Consumer;

public abstract class AbstractNPCEntity extends CreatureEntity implements IInventoryChangedListener, INamedContainerProvider {
    public static final DataParameter<Optional<UUID>> PROTAGONIST = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    public static final DataParameter<Boolean> FOLLOWING = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.BOOLEAN);
    public static final DataParameter<String> ANIMATION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> BLOOD_TYPE = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> DERE = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> EMOTION = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> FAMILY_NAME = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    public static final DataParameter<String> GIVEN_NAME = EntityDataManager.createKey(AbstractNPCEntity.class, DataSerializers.STRING);
    protected Queue<Consumer<AbstractNPCEntity>> nextTickOps;
    protected HashMap<Class<? extends IStateEnum>, Automaton> states;
    protected ChunkPos lastRecordedPos;
    protected PlayerEntity playerInteracted;
    protected LivingEntity entityStaring;
    protected LivingEntity entityToAvoid;
    protected BlockState blockToMine;
    protected int timeSinceAvoid;
    protected int timeSinceInteraction;
    protected int timeSinceStare;
    protected int timeSinceSleep;
    protected int timeUntilHungry;
    protected int timeUntilLove;
    protected long age;
    private float foodLevel = 20.0F;
    private float saturation = 5.0F;
    private float exhaustion;
    private float love;
    private float affection;
    private float stress;
    private float relaxation;
    private float progress;

    protected AbstractNPCEntity(EntityType<? extends AbstractNPCEntity> type, World world) {
        super(type, world);
        this.setPathPriority(PathNodeType.DOOR_OPEN, 0.0F);
        this.setPathPriority(PathNodeType.DOOR_WOOD_CLOSED, 0.0F);
        this.setPathPriority(PathNodeType.TRAPDOOR, 0.0F);
        this.setHomePosAndDistance(this.getPosition(), 16);
        this.lastRecordedPos = new ChunkPos(0, 0);
        this.nextTickOps = new LinkedList<>();
        this.stepHeight = 1.0F;
        this.states = new HashMap<>();
        this.registerStates();
    }

    @Override
    public ITextComponent getName() {
        return new TranslationTextComponent("entity.moeblocks.generic", this.getGivenName(), this.getHonorific());
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return this.canFly() && source == DamageSource.FALL;
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    public boolean canFly() {
        return this.moveController instanceof FlyingMovementController;
    }

    public String getHonorific() {
        return this.getGender() == Gender.FEMININE ? "chan" : "kun";
    }

    public Gender getGender() {
        return Gender.FEMININE;
    }

    public String getGivenName() {
        return this.dataManager.get(GIVEN_NAME);
    }

    public void setGivenName(String name) {
        this.dataManager.set(GIVEN_NAME, name);
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(0x0, new OpenDoorGoal(this));
        this.goalSelector.addGoal(0x0, new SleepGoal(this));
        this.goalSelector.addGoal(0x0, new SwimGoal(this));
        this.goalSelector.addGoal(0x1, new LookAtInteractiveGoal(this));
        this.goalSelector.addGoal(0x2, new BasicAttackGoal(this));
        this.goalSelector.addGoal(0x3, new ConsumeGoal(this));
        this.goalSelector.addGoal(0x4, new AvoidTargetGoal(this));
        this.goalSelector.addGoal(0x5, new TryEquipItemGoal<>(this));
        this.goalSelector.addGoal(0x5, new TryEquipItemGoal<>(this, (stack) -> stack.isFood()));
        this.goalSelector.addGoal(0x6, new FollowTargetGoal(this));
        this.goalSelector.addGoal(0x8, new FindBedGoal(this));
        this.goalSelector.addGoal(0xA, new StayHomeGoal(this));
        this.goalSelector.addGoal(0xB, new WanderGoal(this));
        this.targetSelector.addGoal(0x1, new RevengeTarget(this));
    }

    public void registerStates() {
        this.states.put(Animation.class, new Automaton(this, Animation.DEFAULT).setCanRunOnClient());
        this.states.put(BloodType.class, new Automaton(this, BloodType.O).setCanUpdate(false));
        this.states.put(Dere.class, new Automaton(this, Dere.NYANDERE).setCanUpdate(false));
        this.states.put(Emotion.class, new Automaton(this, Emotion.NORMAL));
        this.states.put(Gender.class, new Automaton(this, Gender.FEMININE).setCanUpdate(false));
        this.states.put(HealthState.class, new Automaton(this, HealthState.PERFECT));
        this.states.put(HungerState.class, new Automaton(this, HungerState.SATISFIED));
        this.states.put(HeldItemState.class, new Automaton(this, HeldItemState.DEFAULT));
        this.states.put(LoveState.class, new Automaton(this, LoveState.FRIENDLY));
        this.states.put(MoonPhase.class, new Automaton(this, MoonPhase.FULL));
        this.states.put(PeriodOfTime.class, new Automaton(this, PeriodOfTime.ATTACHED));
        this.states.put(StoryPhase.class, new Automaton(this, StoryPhase.INTRODUCTION));
        this.states.put(StressState.class, new Automaton(this, StressState.RELAXED));
        this.states.put(TimeOfDay.class, new Automaton(this, TimeOfDay.MORNING));
    }

    @Override
    public void registerData() {
        this.dataManager.register(ANIMATION, Animation.DEFAULT.name());
        this.dataManager.register(BLOOD_TYPE, BloodType.O.name());
        this.dataManager.register(DERE, Dere.NYANDERE.name());
        this.dataManager.register(EMOTION, Emotion.NORMAL.name());
        this.dataManager.register(FAMILY_NAME, "Missing");
        this.dataManager.register(GIVEN_NAME, "Name");
        this.dataManager.register(PROTAGONIST, Optional.empty());
        this.dataManager.register(FOLLOWING, false);
        this.dataManager.register(SITTING, false);
        super.registerData();
    }

    @Override
    public int getTalkInterval() {
        return 150 + this.rand.nextInt(150);
    }

    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        switch (id) {
        case 100:
            this.setParticles(ParticleTypes.FLAME);
            return;
        case 101:
            this.setParticles(ParticleTypes.BUBBLE);
            return;
        case 102:
        case 109:
        case 111:
            this.setParticles(ParticleTypes.EFFECT);
            return;
        case 103:
            this.setParticles(ParticleTypes.SPLASH);
            return;
        case 104:
        case 105:
        case 114:
            this.setParticles(ParticleTypes.SMOKE);
            return;
        case 106:
        case 113:
            this.setParticles(ParticleTypes.HEART);
            return;
        case 107:
            return;
        case 108:
        case 110:
        case 112:
            this.setParticles(ParticleTypes.CRIT);
            return;
        default:
            super.handleStatusUpdate(id);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void setParticles(IParticleData particle) {
        for (int i = 0; i < 5; ++i) {
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            double d2 = this.rand.nextGaussian() * 0.02D;
            this.world.addParticle(particle, this.getPosXRandom(1.0D), this.getPosYRandom() + 1.0D, this.getPosZRandom(1.0D), d0, d1, d2);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isSleeping()) { return VoiceLines.SLEEPING.get(this); }
        return VoiceLines.NEUTRAL.get(this);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        if (this.hasProtagonist()) { compound.putUniqueId("Protagonist", this.getProtagonistUUID()); }
        super.writeAdditional(compound);
        compound.putLong("HomePosition", this.getHomePosition().toLong());
        compound.putLong("LastRecordedPosition", this.lastRecordedPos.asLong());
        compound.putInt("TimeSinceAvoid", this.timeSinceAvoid);
        compound.putInt("TimeSinceInteraction", this.timeSinceInteraction);
        compound.putInt("TimeSinceStare", this.timeSinceStare);
        compound.putInt("TimeSinceSleep", this.timeSinceSleep);
        compound.putInt("TimeUntilHungry", this.timeUntilHungry);
        compound.putInt("TimeUntilLove", this.timeUntilLove);
        this.writeCharacter(compound);
    }

    public void writeCharacter(CompoundNBT compound) {
        this.states.forEach((state, automaton) -> compound.putString(state.getSimpleName(), automaton.getToken().toToken()));
        compound.putString("FamilyName", this.getFamilyName());
        compound.putString("GivenName", this.getGivenName());
        compound.putFloat("Health", this.getHealth());
        compound.putFloat("Affection", this.affection);
        compound.putFloat("FoodLevel", this.foodLevel);
        compound.putFloat("Exhaustion", this.exhaustion);
        compound.putFloat("Love", this.love);
        compound.putFloat("Progress", this.progress);
        compound.putFloat("Relaxation", this.relaxation);
        compound.putFloat("Saturation", this.saturation);
        compound.putFloat("Stress", this.stress);
        compound.putLong("Age", this.age);
    }

    public String getFamilyName() {
        return this.dataManager.get(FAMILY_NAME);
    }

    public void setFamilyName(String name) {
        this.dataManager.set(FAMILY_NAME, name);
    }

    public Emotion getEmotion() {
        return Emotion.valueOf(this.dataManager.get(EMOTION));
    }

    public void setEmotion(Emotion emotion) {
        this.dataManager.set(EMOTION, emotion.name());
    }

    public Dere getDere() {
        return Dere.valueOf(this.dataManager.get(DERE));
    }

    public void setDere(Dere dere) {
        this.dataManager.set(DERE, dere.name());
    }

    public BloodType getBloodType() {
        return BloodType.valueOf(this.dataManager.get(BLOOD_TYPE));
    }

    public void setBloodType(BloodType bloodType) {
        this.dataManager.set(BLOOD_TYPE, bloodType.name());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        if (compound.hasUniqueId("Protagonist")) { this.setProtagonist(compound.getUniqueId("Protagonist")); }
        super.readAdditional(compound);
        this.setHomePosition(BlockPos.fromLong(compound.getLong("HomePosition")));
        this.lastRecordedPos = new ChunkPos(compound.getLong("LastRecordedPosition"));
        this.timeSinceAvoid = compound.getInt("TimeSinceAvoid");
        this.timeSinceInteraction = compound.getInt("TimeSinceInteraction");
        this.timeSinceStare = compound.getInt("TimeSinceStare");
        this.timeSinceSleep = compound.getInt("TimeSinceSleep");
        this.timeUntilHungry = compound.getInt("TimeUntilHungry");
        this.timeUntilLove = compound.getInt("TimeUntilLove");
        this.readCharacter(compound);
    }

    public void readCharacter(CompoundNBT compound) {
        System.out.println(compound.toString());
        this.states.forEach((state, automaton) -> automaton.fromToken(compound.getString(state.getSimpleName())));
        this.setBloodType(BloodType.valueOf(compound.getString("BloodType")));
        this.setDere(Dere.valueOf(compound.getString("Dere")));
        this.setEmotion(Emotion.valueOf(compound.getString("Emotion")));
        this.setFamilyName(compound.getString("FamilyName"));
        this.setGivenName(compound.getString("GivenName"));
        this.setHealth(compound.getFloat("Health"));
        this.affection = compound.getFloat("Affection");
        this.foodLevel = compound.getFloat("FoodLevel");
        this.exhaustion = compound.getFloat("Exhaustion");
        this.love = compound.getFloat("Love");
        this.progress = compound.getFloat("Progress");
        this.relaxation = compound.getFloat("Relaxation");
        this.saturation = compound.getFloat("Saturation");
        this.stress = compound.getFloat("Stress");
        this.age = compound.getInt("Age");
    }

    public void setHealth(float health) {
        super.setHealth(health);
        this.sync();
    }

    @Override
    public void onDeath(DamageSource cause) {
        this.setCharacter((npc) -> npc.setDead(true));
        super.onDeath(cause);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return VoiceLines.HURT.get(this);
    }

    @Override
    protected SoundEvent getDeathSound() {
        return VoiceLines.DEAD.get(this);
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        return !this.canFly() || super.onLivingFall(distance, damageMultiplier);
    }

    @Override
    public void swingArm(Hand hand) {
        this.addExhaustion(0.1F);
        super.swingArm(hand);
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    public void jump() {
        this.addExhaustion(this.isSprinting() ? 0.2F : 0.05F);
        super.jump();
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        if (ANIMATION.equals(key)) { this.setNextState(BloodType.class, this.getAnimation()); }
        if (BLOOD_TYPE.equals(key)) { this.setNextState(BloodType.class, this.getBloodType()); }
        if (DERE.equals(key)) { this.setNextState(Dere.class, this.getDere()); }
        if (EMOTION.equals(key)) { this.setNextState(Emotion.class, this.getEmotion()); }
        super.notifyDataManagerChange(key);
    }

    @Override
    public void onItemUseFinish() {
        this.consume(this.activeItemStack);
        super.onItemUseFinish();
    }

    @Override
    public void startSleeping(BlockPos pos) {
        this.setHomePosition(pos);
        this.addStress(-0.0001F);
        this.timeSinceSleep = 0;
        super.startSleeping(pos);
    }

    public void addStress(float stress) {
        this.setStress(this.stress + stress);
    }

    public void consume(ItemStack stack) {
        if (this.canConsume(stack)) {
            Food food = stack.getItem().getFood();
            this.addSaturation(food.getSaturation());
            this.addFoodLevel(food.getHealing());
            food.getEffects().forEach(pair -> {
                if (pair.getFirst() != null && this.world.rand.nextFloat() < pair.getSecond()) {
                    this.addPotionEffect(pair.getFirst());
                }
            });
        }
    }

    public void addFoodLevel(float foodLevel) {
        this.setFoodLevel(this.foodLevel + foodLevel);
    }

    public void addSaturation(float saturation) {
        this.saturation = Math.min(this.saturation + saturation, 20.0F);
    }

    public boolean canConsume(ItemStack stack) {
        return stack.isFood() && (this.isHungry() || stack.getItem().getFood().canEatWhenFull());
    }

    public boolean isHungry() {
        return this.foodLevel < 19.0F;
    }

    public void setNextState(Class<? extends IStateEnum> key, IStateEnum state) {
        this.addNextTickOp((entity) -> this.states.get(key).setNextState(state));
    }

    public void addNextTickOp(Consumer<AbstractNPCEntity> op) {
        this.nextTickOps.add(op);
    }

    public void addExhaustion(float exhaustion) {
        this.exhaustion += exhaustion;
    }

    public void sync() {
        this.setCharacter((npc) -> npc.sync(this));
    }

    public void setCharacter(Consumer<CacheNPC> transaction) {
        if (this.isLocal() && this.hasProtagonist()) {
            CacheNPC character = this.getDatingSim().getNPC(this.getUniqueID(), this);
            character.set(DatingData.get(this.world), transaction);
        }
    }

    public boolean isLocal() {
        return this.world instanceof ServerWorld;
    }

    public boolean hasProtagonist() {
        return this.dataManager.get(PROTAGONIST).isPresent();
    }

    public DatingSim getDatingSim() {
        return DatingData.get(this.world, this.getProtagonistUUID());
    }

    @Override
    protected void dropLoot(DamageSource cause, boolean player) {
        this.entityDropItem(MoeItems.MOE_DIE.get());
        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
            this.entityDropItem(this.getItemStackFromSlot(slot));
        }
    }

    @Override
    public void livingTick() {
        this.updateArmSwingProgress();
        super.livingTick();
        this.states.forEach((state, machine) -> machine.update());
        if (this.isLocal()) {
            if (++this.timeSinceSleep > 24000) { this.addStress(0.0005F); }
            this.updateStareState();
            this.updateHungerState();
            this.updateLoveState();
            ++this.age;
            ChunkPos pos = this.getChunkPosition();
            if (!pos.equals(this.lastRecordedPos)) {
                this.lastRecordedPos = pos;
                this.sync();
            }
        }
    }

    public BlockState getBlockToMine() {
        return this.blockToMine;
    }

    public void setBlockToMine(BlockState state) {
        this.blockToMine = state;
    }

    public void setHomePosition(BlockPos pos) {
        this.setHomePosAndDistance(pos, (int) this.getMaximumHomeDistance());
    }

    public Animation getAnimation() {
        return Animation.valueOf(this.dataManager.get(ANIMATION));
    }

    public void setAnimation(Animation animation) {
        this.dataManager.set(ANIMATION, animation.name());
    }

    @Override
    protected void updateFallState(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (!this.canFly()) { super.updateFallState(y, onGround, state, pos); }
    }

    public void addAffection(float love) {
        this.affection += love;
    }

    public void addLove(float love) {
        this.setLove(this.love + love);
    }

    public void attackEntityFromRange(LivingEntity victim, double factor) {
        AbstractArrowEntity arrow = ProjectileHelper.fireArrow(this, this.getHeldItem(Hand.OFF_HAND), (float) factor);
        double dX = victim.getPosX() - this.getPosX();
        double dY = victim.getPosYHeight(3 / 10) - arrow.getPosY();
        double dZ = victim.getPosZ() - this.getPosZ();
        double d = MathHelper.sqrt(dX * dX + dZ * dZ);
        arrow.shoot(dX, dY + d * 0.2, dZ, 1.6F, 1.0F);
        arrow.pickupStatus = AbstractArrowEntity.PickupStatus.ALLOWED;
        this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(arrow);
        this.getHeldItem(Hand.OFF_HAND).shrink(1);
    }

    public boolean canSee(Entity entity) {
        if (this.canBeTarget(entity) && this.getEntitySenses().canSee(entity)) {
            this.getLookController().setLookPositionWithEntity(entity, this.getHorizontalFaceSpeed(), this.getVerticalFaceSpeed());
            return true;
        }
        return false;
    }

    public boolean canBeTarget(Entity target) {
        return target != null && target.isAlive() && !target.equals(this);
    }

    public boolean canWander() {
        return this.isWithinHomingDistance() && !this.isOccupied();
    }

    public boolean isWithinHomingDistance() {
        return 16 > this.getHomeDistance() || this.getHomeDistance() > 256;
    }

    public double getHomeDistance() {
        return Math.sqrt(this.getDistanceSq(Vector3d.copyCentered(this.getHomePosition())));
    }

    public boolean isOccupied() {
        return this.isFighting() || this.isVengeful() || this.isSleeping() || this.isFollowing() || this.isInteracted() || this.hasPath();
    }

    public boolean isFollowing() {
        if (this.canBeTarget(this.getProtagonist())) { return this.dataManager.get(FOLLOWING); }
        return false;
    }

    public LivingEntity getEntityFromUUID(UUID uuid) {
        return getEntityFromUUID(LivingEntity.class, this.world, uuid);
    }

    public static <T extends LivingEntity> T getEntityFromUUID(Class<T> type, World world, UUID uuid) {
        if (uuid != null && world instanceof ServerWorld) {
            BlockPos moe = CacheNPC.positions.get(uuid);
            if (moe == null) { return null; }
            int eX, bX, x, eZ, bZ, z;
            eX = 16 + (bX = 16 * (x = moe.getX() << 4));
            eZ = 16 + (bZ = 16 * (z = moe.getZ() << 4));
            IChunk chunk = world.getChunk(x, z, ChunkStatus.FULL, false);
            if (chunk == null) { return null; }
            List<T> entities = world.getLoadedEntitiesWithinAABB(type, new AxisAlignedBB(bX, 0, bZ, eX, 255, eZ), (entity) -> entity.getUniqueID().equals(uuid));
            if (entities.size() > 0) { return entities.get(0); }
        }
        return null;
    }

    public void setFollowing(boolean following) {
        this.dataManager.set(FOLLOWING, following);
    }

    public boolean isFighting() {
        return this.canBeTarget(this.getAttackTarget()) || this.canBeTarget(this.getRevengeTarget()) || this.canBeTarget(this.getAvoidTarget());
    }

    public LivingEntity getAvoidTarget() {
        return this.entityToAvoid;
    }

    public void setAvoidTarget(LivingEntity target) {
        this.entityToAvoid = target;
        this.timeSinceAvoid = 0;
    }

    public void setInteractTarget(PlayerEntity player) {
        this.playerInteracted = player;
        this.timeSinceInteraction = 0;
    }

    public boolean isVengeful() {
        return this.ticksExisted - this.getRevengeTimer() < 500;
    }

    public boolean isInteracted() {
        return this.timeSinceInteraction < 20;
    }

    public BlockState getBlockState(BlockPos pos) {
        IChunk chunk = this.getChunk(new ChunkPos(pos));
        return chunk == null ? Blocks.AIR.getDefaultState() : chunk.getBlockState(pos);
    }

    public IChunk getChunk(ChunkPos pos) {
        return this.isThreadSafe() ? this.world.getChunk(pos.x, pos.z, ChunkStatus.FULL, false) : null;
    }

    public boolean isThreadSafe() {
        return this.world.getServer().getExecutionThread().equals(Thread.currentThread());
    }

    public double getGaussian(double factor) {
        return this.rand.nextGaussian() * factor;
    }

    public Automaton getState(Class<? extends IStateEnum> state) {
        return this.states.get(state);
    }

    public float getStrikingDistance(Entity target) {
        return this.getStrikingDistance(target.getWidth());
    }

    public float getStrikingDistance(float distance) {
        return (float) (Math.pow(this.getWidth() * 2.0F, 2) + distance);
    }

    public boolean hasAmmo() {
        return this.isAmmo(this.getHeldItem(Hand.OFF_HAND).getItem());
    }

    public boolean isAmmo(Item item) {
        return item == Items.ARROW || item == Items.SPECTRAL_ARROW || item == Items.TIPPED_ARROW;
    }

    public boolean isBeingWatchedBy(LivingEntity entity) {
        if (!this.canBeTarget(entity)) { return false; }
        Vector3d look = entity.getLook(1.0F).normalize();
        Vector3d cast = new Vector3d(this.getPosX() - entity.getPosX(), this.getPosYEye() - entity.getPosYEye(), this.getPosZ() - entity.getPosZ());
        double distance = cast.length();
        double sum = look.dotProduct(cast.normalize());
        return sum > 1.0D - 0.025D / distance && entity.canEntityBeSeen(this);
    }

    public boolean isCompatible(AbstractNPCEntity entity) {
        return BloodType.isCompatible(this.getBloodType(), entity.getBloodType());
    }

    public ActionResultType onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (stack.getItem().isIn(MoeTags.ADMIN)) { return ActionResultType.FAIL; }
        if (this.isRemote() || hand != Hand.MAIN_HAND) { return ActionResultType.PASS; }
        if (this.isProtagonist(player)) {
            //this.setFollowing(!this.isFollowing());
            return ActionResultType.SUCCESS;
        } else {
            return ActionResultType.FAIL;
        }
    }

    public boolean isProtagonist(PlayerEntity player) {
        if (this.hasProtagonist()) { return this.getProtagonistUUID().equals(player.getUniqueID()); }
        return false;
    }

    public void say(String key, Object... params) {
        this.world.getPlayers().forEach((player) -> {
            if (player.getDistance(this) < 8.0D) { this.say(player, key, params); }
        });
    }

    public void say(PlayerEntity player, String key, Object... params) {
        player.sendMessage(new TranslationTextComponent(key, params), this.getUniqueID());
    }

    public void setNextState(Class<? extends IStateEnum> key, IStateEnum state, int timeout) {
        this.addNextTickOp((entity) -> this.states.get(key).setNextState(state, timeout));
    }

    public boolean tryEquipItem(ItemStack stack) {
        EquipmentSlotType slot = this.getSlotForStack(stack);
        ItemStack shift = this.getItemStackFromSlot(slot);
        int max = shift.getMaxStackSize();
        if (ItemStack.areItemsEqual(shift, stack) && ItemStack.areItemStackTagsEqual(shift, stack) && shift.getCount() < max) {
            int total = shift.getCount() + stack.getCount();
            if (total > max) {
                this.entityDropItem(stack.split(total - max));
                shift.setCount(max);
                return false;
            } else {
                shift.setCount(total);
                stack.setCount(0);
                return true;
            }
        } else if (this.shouldExchangeEquipment(stack, shift)) {
            this.entityDropItem(shift);
            this.setItemStackToSlot(slot, stack.split(1));
            return true;
        }
        return false;
    }

    @Override
    protected boolean shouldExchangeEquipment(ItemStack candidate, ItemStack existing) {
        if (EnchantmentHelper.hasBindingCurse(existing)) { return false; }
        EquipmentSlotType slot = this.getSlotForStack(candidate);
        if (slot == EquipmentSlotType.OFFHAND) {
            if (this.canConsume(candidate)) { return true; }
            if (this.isWieldingBow()) { return candidate.getItem().isIn(ItemTags.ARROWS); }
            return false;
        }
        if (existing.getItem().getClass() == candidate.getItem().getClass()) {
            if (existing.getItem() instanceof TieredItem) {
                IItemTier a = ((TieredItem) candidate.getItem()).getTier();
                IItemTier b = ((TieredItem) existing.getItem()).getTier();
                return a.getAttackDamage() > b.getAttackDamage();
            }
            if (existing.getItem() instanceof ArmorItem) {
                ArmorItem a = (ArmorItem) candidate.getItem();
                ArmorItem b = (ArmorItem) existing.getItem();
                return a.getDamageReduceAmount() > b.getDamageReduceAmount();
            }
        }
        return existing.isEmpty();
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void updateAITasks() {
        Consumer<AbstractNPCEntity> op = this.nextTickOps.poll();
        if (op != null) { op.accept(this); }
    }

    @Override
    protected float getDropChance(EquipmentSlotType slot) {
        return 0.0F;
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        return;
    }

    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT compound) {
        ILivingEntityData data = super.onInitialSpawn(world, difficulty, reason, spawnData, compound);
        this.setHomePosAndDistance(this.getPosition(), 16);
        this.setBloodType(BloodType.weigh(this.rand));
        this.setGivenName(this.getGender().toString());
        this.resetAnimationState();
        return data;
    }

    @Override
    public boolean canPickUpItem(ItemStack stack) {
        EquipmentSlotType slot = this.getSlotForStack(stack);
        ItemStack shift = this.getItemStackFromSlot(slot);
        int max = shift.getMaxStackSize();
        if (ItemStack.areItemsEqual(shift, stack) && ItemStack.areItemStackTagsEqual(shift, stack) && shift.getCount() < max) {
            return shift.getCount() + stack.getCount() <= max;
        }
        return this.shouldExchangeEquipment(stack, shift);
    }

    @Override
    public ActionResultType func_230254_b_(PlayerEntity player, Hand hand) {
        ActionResultType result = this.onInteract(player, player.getHeldItem(hand), hand);
        if (result.isSuccessOrConsume()) { this.setInteractTarget(player); }
        this.sync();
        return result;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return this.canBeTarget(target);
    }

    @Override
    public boolean attackEntityAsMob(Entity entity) {
        if (super.attackEntityAsMob(entity)) {
            this.playSound(VoiceLines.ATTACK.get(this));
            return true;
        }
        return false;
    }

    public void playSound(SoundEvent sound) {
        this.playSound(sound, this.getSoundVolume(), this.getSoundPitch());
    }

    public void resetAnimationState() {
        this.setAnimation(Animation.DEFAULT);
    }

    public boolean isWieldingBow() {
        Item item = this.getHeldItem(Hand.MAIN_HAND).getItem();
        return item == Items.BOW || item == Items.CROSSBOW;
    }

    public EquipmentSlotType getSlotForStack(ItemStack stack) {
        EquipmentSlotType slot = MobEntity.getSlotForItemStack(stack);
        if (stack.getItem().isIn(MoeTags.OFFHAND) || stack.isFood()) {
            slot = EquipmentSlotType.OFFHAND;
        }
        return slot;
    }

    public void updateHungerState() {
        if (--this.timeUntilHungry < 0) {
            if (this.exhaustion > 4.0F) {
                this.exhaustion -= 4.0F;
                this.saturation -= 1.0F;
                if (this.saturation < 0) {
                    this.addFoodLevel(-1.0F);
                    this.saturation = 0.0F;
                }
            }
            if (this.saturation > 0.0F) {
                this.timeUntilHungry = 80;
                this.addExhaustion(6.0F);
                this.heal(1.0F);
            }
        }
    }

    public void updateLoveState() {
        if (--this.timeUntilLove < 0) {
            this.affection -= this.stress;
            this.addLove(this.affection);
            this.timeUntilLove = 1000;
            if (this.affection < 0) {
                this.affection = 0;
            }
        }
    }

    public PlayerEntity getInteractTarget() {
        if (this.isInteracted()) { return this.playerInteracted; }
        return null;
    }

    public void updateStareState() {
        List<LivingEntity> list = this.world.getLoadedEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(8.0));
        list.sort(new EntityDistance(this));
        for (LivingEntity entity : list) {
            if (this.isBeingWatchedBy(entity)) {
                this.entityStaring = entity;
                this.timeSinceStare = 0;
                return;
            }
        }
    }

    public LivingEntity getStareTarget() {
        if (this.isBeingStaredAt()) { return this.entityStaring; }
        return null;
    }

    public void setCanFly(boolean fly) {
        this.setMoveController(fly ? new FlyingMovementController(this, 10, false) : new MovementController(this));
        this.setNavigator(fly ? new FlyingPathNavigator(this, this.world) : new GroundPathNavigator(this, this.world));
    }

    public void setMoveController(MovementController moveController) {
        this.moveController = moveController;
    }

    public void setNavigator(PathNavigator navigator) {
        this.navigator = navigator;
        if (this.navigator instanceof GroundPathNavigator) {
            GroundPathNavigator ground = (GroundPathNavigator) this.navigator;
            ground.setBreakDoors(true);
        }
    }

    public int getAgeInYears() {
        return this.getBaseAge() + (int) (this.world.getGameTime() - this.age) / 24000 / 366;
    }

    public abstract int getBaseAge();

    public int getAttackCooldown() {
        return (int) (this.getAttribute(Attributes.ATTACK_SPEED).getValue() * 4.0F);
    }

    public BlockState getBlockData() {
        return Blocks.AIR.getDefaultState();
    }

    public float getBlockStrikingDistance() {
        return this.getStrikingDistance(1.0F);
    }

    public double getCenteredRandomPosX() {
        return this.getPosXRandom(this.getWidth() / 2.0F);
    }

    public double getCenteredRandomPosY() {
        return this.getPosYRandom() + this.getHeight() / 2.0F;
    }

    public double getCenteredRandomPosZ() {
        return this.getPosZRandom(this.getWidth() / 2.0F);
    }

    public ChunkPos getChunkPosition() {
        return new ChunkPos(this.getPosition());
    }

    public CompoundNBT getExtraBlockData() {
        return new CompoundNBT();
    }

    public float getFoodLevel() {
        return this.foodLevel;
    }

    public void setFoodLevel(float foodLevel) {
        this.foodLevel = Math.max(0.0F, Math.min(this.foodLevel, 20.0F));
        this.sync();
    }

    public String getFullName() {
        return String.format("%s %s", this.getFamilyName(), this.getGivenName());
    }

    public float getLove() {
        return this.love;
    }

    public void setLove(float love) {
        this.love = Math.max(0.0F, Math.min(love, 20.0F));
        this.sync();
    }

    public UUID getProtagonistUUID() {
        return this.dataManager.get(PROTAGONIST).orElse(null);
    }

    public PlayerEntity getProtagonist() {
        if (this.hasProtagonist()) { return this.world.getPlayerByUuid(this.getProtagonistUUID()); }
        return null;
    }

    public void setProtagonist(PlayerEntity player) {
        this.setCharacter((npc) -> npc.setEstranged(true));
        this.setProtagonist(player != null ? player.getUniqueID() : null);
        this.sync();
    }

    public void setProtagonist(UUID uuid) {
        this.dataManager.set(PROTAGONIST, Optional.of(uuid));
    }

    public float getScale() {
        return 1.0F;
    }

    public StoryPhase getStory() {
        int step = (int) this.getProgress();
        switch (step) {
        case 1:
            return StoryPhase.INFATUATION;
        case 2:
            return StoryPhase.CONFUSION;
        case 3:
            return StoryPhase.RESOLUTION;
        case 4:
            return StoryPhase.TRAGEDY;
        default:
            return StoryPhase.INTRODUCTION;
        }
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        this.sync();
    }

    public float getStress() {
        return this.stress;
    }

    public void setStress(float stress) {
        this.stress = Math.max(Math.min(stress, 20.0F), 0.0F);
        this.sync();
    }

    public int getTimeSinceInteraction() {
        return this.timeSinceInteraction;
    }

    public int getTimeSinceSleep() {
        return this.timeSinceSleep;
    }

    public boolean isAtEase() {
        return !this.isFighting();
    }

    public boolean isAttacking() {
        return this.canBeTarget(this.getAttackTarget());
    }

    public boolean isAvoiding() {
        return this.timeSinceAvoid < 500;
    }

    public boolean isFull() {
        return this.foodLevel > 19.0F;
    }

    public boolean isRemote() {
        return this.world.isRemote();
    }

    public boolean isSitting() {
        return this.dataManager.get(SITTING) || this.isPassenger() && (this.getRidingEntity() != null && this.getRidingEntity().shouldRiderSit());
    }

    public void setSitting(boolean sitting) {
        this.dataManager.set(SITTING, sitting);
    }

    public boolean isBeingStaredAt() {
        return this.timeSinceStare < 20;
    }

    public boolean isTimeToSleep() {
        if (this.canFight() && this.isNightWatch()) { return false; }
        return this.isOccupied() && !this.world.isDaytime();
    }

    public boolean canFight() {
        Item item = this.getHeldItem(Hand.MAIN_HAND).getItem();
        return item.isIn(MoeTags.WEAPONS);
    }

    public boolean isNightWatch() {
        return this.world.getGameTime() / 24000 % 4 == this.getBloodType().ordinal();
    }
}
