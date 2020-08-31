package mod.moeblocks.entity.ai;

import mod.moeblocks.entity.util.Emotions;
import mod.moeblocks.entity.util.VoiceLines;
import mod.moeblocks.init.MoeTags;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.IWorld;

import java.util.List;
import java.util.UUID;

public class Relationship extends AbstractState implements Comparable<Relationship> {
    protected UUID uuid;
    protected float affection;
    protected float infatuation;
    protected float trust;
    private int timeSinceSeen;
    private int timeUntilCheck;

    public Relationship() {
        this(null);
    }

    public Relationship(UUID uuid) {
        this(uuid, 0, 0);
    }

    public Relationship(UUID uuid, float affection, float trust) {
        this.uuid = uuid;
        this.affection = affection;
        this.trust = trust;
    }

    public static List<Relationship> sort(List<Relationship> list) {
        list.sort(Relationship::compareTo);
        return list;
    }

    @Override
    public int compareTo(Relationship other) {
        return Float.compare(this.getLoyalty(), other.getLoyalty());
    }

    public float getLoyalty() {
        return this.getTrust() - this.entity.getStressStats().getStress() + this.getAffection();
    }

    public float getAffection() {
        return this.affection + this.infatuation;
    }

    public float getTrust() {
        return this.trust;
    }

    @Override
    public void start() {
        this.entity.getRelationships().set(this.getUUID(), this);
    }

    @Override
    public void tick() {
        if (this.entity.isLocal() && --this.timeUntilCheck < 0) {
            this.timeUntilCheck = 20;
            LivingEntity host = this.getHost();
            if (host != null && host.getDistance(this.entity) < 16.0F) {
                this.entity.getStressStats().addStressSilently(this.getAnxiety() * -1.0F);
                if (this.timeSinceSeen > 300) {
                    this.timeSinceSeen = 0;
                    this.onHello(host);
                    this.addAffection(2.0F);
                } else if (this.entity.isBeingWatchedBy(host)) {
                    this.onStare(host);
                    this.addAffection(0.1F);
                } else if (host.getDistance(this.entity) < 4.0F) {
                    this.addTrust(0.005F);
                } else {
                    this.addTrust(0.001F);
                }
            } else {
                ++this.timeSinceSeen;
                this.entity.getStressStats().addStressSilently(this.getAnxiety());
                this.addAffection(-0.001F);
            }
            if (this.infatuation > 0.0F) {
                this.addInfatuation(-1.0F / this.infatuation);
            }
        }
    }

    public float getAnxiety() {
        return (this.getAffection() / 20.0F) / this.entity.getRelationships().size() * 0.001F;
    }

    @Override
    public void stop() {

    }

    @Override
    public void read(CompoundNBT compound) {
        this.timeSinceSeen = compound.getInt("TimeSinceSeen");
        this.timeUntilCheck = compound.getInt("TimeUntilCheck");
        this.uuid = compound.getUniqueId("Host");
        this.affection = compound.getFloat("Affection");
        this.infatuation = compound.getFloat("Infatuation");
        this.trust = compound.getFloat("Trust");
    }

    @Override
    public void write(CompoundNBT compound) {
        compound.putInt("TimeSinceSeen", this.timeSinceSeen);
        compound.putInt("TimeUntilCheck", this.timeUntilCheck);
        compound.putUniqueId("Host", this.uuid);
        compound.putFloat("Affection", this.affection);
        compound.putFloat("Infatuation", this.infatuation);
        compound.putFloat("Trust", this.trust);
    }

    @Override
    public void onDeath(DamageSource cause) {
        if (this.getHost() instanceof PlayerEntity && this.canDoChoresFor()) {
            this.getPlayer().sendMessage(cause.getDeathMessage(this.entity), this.entity.getUniqueID());
        }
    }

    @Override
    public void onSpawn(IWorld world) {

    }

    @Override
    public boolean onDamage(DamageSource cause, float amount) {
        return false;
    }

    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (this.entity.isLocal() && hand == Hand.MAIN_HAND) {
            Item item = stack.getItem();
            if (player.isSneaking()) {
                if (this.canFollow() && (stack.isEmpty() || item.isIn(MoeTags.EQUIPPABLES))) {
                    if (this.entity.tryEquipItem(stack)) {
                        this.entity.playSound(VoiceLines.THANK_YOU.get(this.entity));
                        this.entity.getRelationships().resetGiftTimer();
                        this.addTrust(this.entity.getDere().getGiftValue(stack));
                    } else if (stack.isEmpty()) {
                        for (EquipmentSlotType slot : EquipmentSlotType.values()) {
                            ItemStack drop = this.entity.getItemStackFromSlot(slot);
                            this.entity.entityDropItem(drop.split(drop.getCount()));
                        }
                    }
                    return true;
                }
                float value = this.entity.getDere().getGiftValue(stack);
                if (value > 0.0F) {
                    if (this.entity.getRelationships().isReadyForGifts()) {
                        this.entity.entityDropItem(this.entity.getHeldItem(Hand.OFF_HAND));
                        this.entity.setHeldItem(Hand.OFF_HAND, stack.split(1));
                        this.entity.getRelationships().resetGiftTimer();
                        this.entity.setEmotion(Emotions.SMITTEN, (int) (100 * value));
                        this.addAffection(value, value * 3.0F);
                        return true;
                    }
                }
            }
            if (this.canFollow()) {
                this.entity.playSound(VoiceLines.YES.get(this.entity));
                this.entity.toggleFollowTarget(player);
                return true;
            }
            this.entity.playSound(VoiceLines.NO.get(this.entity));
            return false;
        }
        return false;
    }

    @Override
    public boolean isArmed() {
        return false;
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return this.canAttack();
    }

    public boolean canAttack() { return this.getLoyalty() < 1; }

    public boolean canFollow() {
        return this.getLoyalty() > 2;
    }

    public void addAffection(float affection, float infatuation) {
        this.addAffection(affection);
        this.addInfatuation(infatuation);
    }

    public boolean canDoChoresFor() {
        return this.getLoyalty() > 4;
    }

    public PlayerEntity getPlayer() {
        LivingEntity entity = this.getHost();
        if (entity instanceof PlayerEntity) {
            return (PlayerEntity) entity;
        } else {
            return null;
        }
    }

    public void addAffection(float affection) {
        this.affection = Math.max(Math.min(this.affection + affection, 20.0F), -5.0F);
    }

    public void addInfatuation(float infatuation) {
        this.infatuation = Math.max(this.infatuation + infatuation, 0.0F);
    }

    public void addTrust(float trust) {
        this.trust = Math.max(Math.min(this.trust + trust, 20.0F), -5.0F);
    }

    public LivingEntity getHost() {
        return this.entity.getEntityFromUUID(this.getUUID());
    }

    public void onHello(LivingEntity host) {
        this.entity.getDere().onHello(host, this);
    }

    public void onStare(LivingEntity host) {
        this.entity.getDere().onStare(host, this);
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public float getDistance() {
        return 3.0F / (this.getLoyalty() / 20.0F);
    }

    public boolean canHate() {
        return this.getLoyalty() < 0;
    }

    public boolean canFightAlongside() {
        return this.getLoyalty() > 8;
    }

    public boolean canDefend() {
        return this.getLoyalty() > 12;
    }

    public boolean canDieFor() {
        return this.getLoyalty() > 16;
    }

    public boolean isPlayer() {
        return this.getPlayer() != null;
    }

    public int getLastSeen() {
        return this.timeSinceSeen;
    }
}
