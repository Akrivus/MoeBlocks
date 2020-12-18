package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.item.*;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MoeMod.ID);
    public static final RegistryObject<Item> BENTO_BOX = REGISTRY.register("bento_box", BentoBoxItem::new);
    public static final RegistryObject<Item> CELL_PHONE = REGISTRY.register("cell_phone", CellPhoneItem::new);
    public static final RegistryObject<Item> CUPCAKE = REGISTRY.register("cupcake", CupcakeItem::new);
    public static final RegistryObject<Item> INVITE = REGISTRY.register("invite", InviteItem::new);
    public static final RegistryObject<Item> KNAIFU = REGISTRY.register("knaifu", KnaifuItem::new);
    public static final RegistryObject<Item> LETTER = REGISTRY.register("letter", LetterItem::new);
    public static final RegistryObject<Item> MOE_DIE = REGISTRY.register("moe_die", MoeDieItem::new);
    public static final RegistryObject<Item> MOE_SPAWN_EGG = REGISTRY.register("moe_spawn_egg", MoeSpawnItem::new);
    public static final RegistryObject<Item> ONIGIRI = REGISTRY.register("onigiri", OnigiriItem::new);
    public static final RegistryObject<Item> PINK_BOW = REGISTRY.register("pink_bow", PinkBowItem::new);
    public static final RegistryObject<Item> YEARBOOK_PAGE = REGISTRY.register("yearbook_page", YearbookPageItem::new);
    public static final RegistryObject<Item> YEARBOOK = REGISTRY.register("yearbook", YearbookItem::new);

    public static void registerDispenserBehaviors() {
        DispenserBlock.registerDispenseBehavior(MoeItems.MOE_DIE.get(), MoeDieItem.DISPENSER_BEHAVIOR);
    }

    public static void registerOverrides() {
        ItemModelsProperties.registerProperty(MoeItems.INVITE.get(), LetterItem.CLOSED_PROPERTY, LetterItem.CLOSED_PROPERTY_GETTER);
        ItemModelsProperties.registerProperty(MoeItems.LETTER.get(), LetterItem.CLOSED_PROPERTY, LetterItem.CLOSED_PROPERTY_GETTER);
    }

    public static class CreativeTab extends ItemGroup {
        public static final CreativeTab INSTANCE = new CreativeTab();

        public CreativeTab() {
            super(MoeMod.ID);
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(MoeItems.MOE_DIE.get());
        }
    }
}