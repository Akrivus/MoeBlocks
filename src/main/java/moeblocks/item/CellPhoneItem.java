package moeblocks.item;

import moeblocks.datingsim.DatingData;
import moeblocks.entity.AbstractNPCEntity;
import moeblocks.init.MoeItems;
import moeblocks.init.MoeMessages;
import moeblocks.message.SOpenCellPhone;
import moeblocks.message.SOpenYearbook;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class CellPhoneItem extends Item {
    public CellPhoneItem() {
        super(new Properties().group(MoeItems.CreativeTab.INSTANCE));
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        if (world.isRemote()) { return ActionResult.resultPass(player.getHeldItem(hand)); }
        MoeMessages.send(player, new SOpenCellPhone(DatingData.get(player.world, player.getUniqueID()).getNPCs(), hand));
        return ActionResult.resultSuccess(player.getHeldItem(hand));
    }
}
