package mod.moeblocks.item;

import mod.moeblocks.entity.MoeDieEntity;
import mod.moeblocks.init.MoeItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class MoeDieItem extends Item {

    public MoeDieItem() {
        super(new Item.Properties().group(MoeItems.Group.INSTANCE));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote) {
            MoeDieEntity die = new MoeDieEntity(world, player);
            die.func_234612_a_(player, player.rotationPitch, player.rotationYaw, 0.0F, 0.8F, 1.0F);
            die.setItem(stack);
            world.addEntity(die);
        }
        player.addStat(Stats.ITEM_USED.get(this));
        if (!player.abilities.isCreativeMode) {
            stack.shrink(1);
        }
        return ActionResult.resultSuccess(stack);
    }
}