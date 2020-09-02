package moe.blocks.mod.entity.ai.behavior;

import moe.blocks.mod.entity.util.Behaviors;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class CoarseDirtBehavior extends BasicBehavior {
    @Override
    public boolean onInteract(PlayerEntity player, ItemStack stack, Hand hand) {
        if (stack.getItem() instanceof HoeItem) {
            this.moe.setBlockData(Blocks.DIRT.getDefaultState());
            stack.damageItem(1, player, entity -> {
                entity.sendBreakAnimation(hand);
            });
            return true;
        }
        return false;
    }

    @Override
    public Behaviors getKey() {
        return Behaviors.COARSE_DIRT;
    }
}