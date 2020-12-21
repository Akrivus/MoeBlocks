package moeblocks.automata.state;

import moeblocks.automata.GoalState;
import moeblocks.automata.IState;
import moeblocks.automata.IStateEnum;
import moeblocks.automata.IStateGoal;
import moeblocks.entity.AbstractNPCEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public enum MoonPhases implements IStateEnum<AbstractNPCEntity> {
    NEW((npc, list) -> {

    }, (npc) -> npc.world.getMoonFactor(), 0.00F, 0.25F),
    CRESCENT((npc, list) -> {

    }, (npc) -> npc.world.getMoonFactor(), 0.25F, 0.50F),
    QUARTER((npc, list) -> {

    }, (npc) -> npc.world.getMoonFactor(), 0.50F, 0.75F),
    GIBBOUS((npc, list) -> {

    }, (npc) -> npc.world.getMoonFactor(), 0.75F, 1.00F),
    FULL((npc, list) -> {

    }, (npc) -> npc.world.getMoonFactor(), 1.00F, 1.00F);

    private final BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator;
    private final Function<AbstractNPCEntity, Float> valuator;
    private final float start;
    private final float end;

    MoonPhases(BiConsumer<AbstractNPCEntity, List<IStateGoal>> generator, Function<AbstractNPCEntity, Float> valuator, float start, float end) {
        this.generator = generator;
        this.valuator = valuator;
        this.start = start;
        this.end = end;
    }

    @Override
    public IState getState(AbstractNPCEntity applicant) {
        return new GoalState.ValueBased(this, this.generator, this.valuator, this.start, this.end);
    }
}
