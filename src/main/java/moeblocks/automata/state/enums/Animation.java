package moeblocks.automata.state.enums;

import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.client.animation.AnimationState;
import moeblocks.client.animation.state.*;
import moeblocks.entity.AbstractNPCEntity;

import java.util.function.Supplier;

public enum Animation implements IStateEnum<AbstractNPCEntity> {
    AIM(AimBow::new),
    DEFAULT(Default::new),
    HAPPY_DANCE(HappyDance::new),
    SUMMONED(Summoned::new),
    YEARBOOK(Yearbook::new);
    
    private final Supplier<? extends AnimationState> animation;
    
    Animation(Supplier<? extends AnimationState> animation) {
        this.animation = animation;
    }
    
    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return this.animation.get();
    }
    
    @Override
    public String toKey() {
        return this.name();
    }
    
    @Override
    public IStateEnum<AbstractNPCEntity> fromKey(String key) {
        return Animation.get(key);
    }
    
    @Override
    public IStateEnum<AbstractNPCEntity>[] getKeys() {
        return Animation.values();
    }
    
    public static Animation get(String key) {
        try {
            return Animation.valueOf(key);
        } catch (IllegalArgumentException e) {
            return Animation.DEFAULT;
        }
    }
}