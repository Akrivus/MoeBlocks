package block_party.init;

import block_party.blocks.*;
import block_party.blocks.tree.GinkgoTree;
import block_party.blocks.tree.SakuraTree;
import block_party.blocks.tree.WhiteSakuraTree;
import block_party.blocks.tree.WisteriaTree;
import block_party.mob.automata.Condition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class BlockPartyBlocks {
    public static final Supplier<BlockBehaviour.Properties> SOLID_PROPERTY = () -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).sound(SoundType.WOOD).strength(3.0F);
    public static final Supplier<BlockBehaviour.Properties> STONE_PROPERTY = () -> BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).sound(SoundType.STONE).strength(6.0F);
    public static final Supplier<BlockBehaviour.Properties> SHOJI_PROPERTY = () -> BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_PINK).sound(SoundType.CROP).strength(0.5F).noOcclusion();
    public static final Supplier<BlockBehaviour.Properties> PLANT_PROPERTY = () -> BlockBehaviour.Properties.of(Material.PLANT).sound(SoundType.CROP).strength(0.2F).randomTicks().noOcclusion();
    public static final Supplier<BlockBehaviour.Properties> NONSOLID_PROPERTY = () -> SOLID_PROPERTY.get().noOcclusion();
    public static final Supplier<BlockBehaviour.Properties> TRANSPARENT_PROPERTY = () -> NONSOLID_PROPERTY.get().isRedstoneConductor((state, reader, pos) -> false);
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, block_party.BlockParty.ID);
    public static final RegistryObject<Block> BLANK_HANGING_SCROLL = REGISTRY.register("blank_hanging_scroll", () -> new HangingScrollBlock(SHOJI_PROPERTY.get(), Condition.NEVER));
    public static final RegistryObject<Block> BLACK_PAPER_LANTERN = REGISTRY.register("black_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.COLOR_BLACK));
    public static final RegistryObject<Block> DAWN_HANGING_SCROLL = REGISTRY.register("dawn_hanging_scroll", () -> new HangingScrollBlock(SHOJI_PROPERTY.get(), Condition.DAWN));
    public static final RegistryObject<Block> EVENING_HANGING_SCROLL = REGISTRY.register("evening_hanging_scroll", () -> new HangingScrollBlock(SHOJI_PROPERTY.get(), Condition.EVENING));
    public static final RegistryObject<Block> MIDNIGHT_HANGING_SCROLL = REGISTRY.register("midnight_hanging_scroll", () -> new HangingScrollBlock(SHOJI_PROPERTY.get(), Condition.MIDNIGHT));
    public static final RegistryObject<Block> MORNING_HANGING_SCROLL = REGISTRY.register("morning_hanging_scroll", () -> new HangingScrollBlock(SHOJI_PROPERTY.get(), Condition.MORNING));
    public static final RegistryObject<Block> NIGHT_HANGING_SCROLL = REGISTRY.register("night_hanging_scroll", () -> new HangingScrollBlock(SHOJI_PROPERTY.get(), Condition.NIGHT));
    public static final RegistryObject<Block> NOON_HANGING_SCROLL = REGISTRY.register("noon_hanging_scroll", () -> new HangingScrollBlock(SHOJI_PROPERTY.get(), Condition.NOON));
    public static final RegistryObject<Block> BLUE_PAPER_LANTERN = REGISTRY.register("blue_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_BLUE));
    public static final RegistryObject<Block> BROWN_PAPER_LANTERN = REGISTRY.register("brown_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_BROWN));
    public static final RegistryObject<Block> CYAN_PAPER_LANTERN = REGISTRY.register("cyan_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_CYAN));
    public static final RegistryObject<Block> GARDEN_LANTERN = REGISTRY.register("garden_lantern", () -> new GardenLanternBlock(STONE_PROPERTY.get()));
    public static final RegistryObject<Block> GRAY_PAPER_LANTERN = REGISTRY.register("gray_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_GRAY));
    public static final RegistryObject<Block> GINKGO_BUTTON = REGISTRY.register("ginkgo_button", () -> new WoodButtonBlock(NONSOLID_PROPERTY.get()));
    public static final RegistryObject<Block> GINKGO_FENCE = REGISTRY.register("ginkgo_fence", () -> new FenceBlock(NONSOLID_PROPERTY.get()));
    public static final RegistryObject<Block> GINKGO_FENCE_GATE = REGISTRY.register("ginkgo_fence_gate", () -> new FenceGateBlock(NONSOLID_PROPERTY.get()));
    public static final RegistryObject<Block> GINKGO_LEAVES = REGISTRY.register("ginkgo_leaves", () -> new GinkgoLeavesBlock(BlockPartyParticles.GINKGO, PLANT_PROPERTY.get()));
    public static final RegistryObject<Block> GINKGO_LOG = REGISTRY.register("ginkgo_log", () -> new RotatedPillarBlock(SOLID_PROPERTY.get()));
    public static final RegistryObject<Block> GINKGO_PLANKS = REGISTRY.register("ginkgo_planks", () -> new Block(SOLID_PROPERTY.get()));
    public static final RegistryObject<Block> GINKGO_PRESSURE_PLATE = REGISTRY.register("ginkgo_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, NONSOLID_PROPERTY.get()));
    public static final RegistryObject<Block> GINKGO_SAPLING = REGISTRY.register("ginkgo_sapling", () -> new SaplingBlock(new GinkgoTree(), PLANT_PROPERTY.get()));
    public static final RegistryObject<Block> GINKGO_SLAB = REGISTRY.register("ginkgo_slab", () -> new SlabBlock(NONSOLID_PROPERTY.get()));
    public static final RegistryObject<Block> GINKGO_STAIRS = REGISTRY.register("ginkgo_stairs", () -> new StairBlock(() -> GINKGO_PLANKS.get().defaultBlockState(), NONSOLID_PROPERTY.get()));
    public static final RegistryObject<Block> GINKGO_WOOD = REGISTRY.register("ginkgo_wood", () -> new RotatedPillarBlock(SOLID_PROPERTY.get()));
    public static final RegistryObject<Block> GREEN_PAPER_LANTERN = REGISTRY.register("green_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_GREEN));
    public static final RegistryObject<Block> LIGHT_BLUE_PAPER_LANTERN = REGISTRY.register("light_blue_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_LIGHT_BLUE));
    public static final RegistryObject<Block> LIGHT_GRAY_PAPER_LANTERN = REGISTRY.register("light_gray_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_LIGHT_GRAY));
    public static final RegistryObject<Block> LIME_PAPER_LANTERN = REGISTRY.register("lime_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_LIGHT_GREEN));
    public static final RegistryObject<Block> LUCKY_CAT = REGISTRY.register("lucky_cat", () -> new LuckyCatBlock(TRANSPARENT_PROPERTY.get()));
    public static final RegistryObject<Block> MAGENTA_PAPER_LANTERN = REGISTRY.register("magenta_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_MAGENTA));
    public static final RegistryObject<Block> ORANGE_PAPER_LANTERN = REGISTRY.register("orange_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_ORANGE));
    public static final RegistryObject<Block> PINK_PAPER_LANTERN = REGISTRY.register("pink_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_PINK));
    public static final RegistryObject<Block> PURPLE_PAPER_LANTERN = REGISTRY.register("purple_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_PURPLE));
    public static final RegistryObject<Block> RED_PAPER_LANTERN = REGISTRY.register("red_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_RED));
    public static final RegistryObject<Block> SAKURA_BLOSSOMS = REGISTRY.register("sakura_blossoms", () -> new SakuraBlossomsBlock(BlockPartyParticles.SAKURA, PLANT_PROPERTY.get()));
    public static final RegistryObject<Block> SAKURA_BUTTON = REGISTRY.register("sakura_button", () -> new WoodButtonBlock(NONSOLID_PROPERTY.get()));
    public static final RegistryObject<Block> SAKURA_FENCE = REGISTRY.register("sakura_fence", () -> new FenceBlock(NONSOLID_PROPERTY.get()));
    public static final RegistryObject<Block> SAKURA_FENCE_GATE = REGISTRY.register("sakura_fence_gate", () -> new FenceGateBlock(NONSOLID_PROPERTY.get()));
    public static final RegistryObject<Block> SAKURA_LOG = REGISTRY.register("sakura_log", () -> new RotatedPillarBlock(SOLID_PROPERTY.get()));
    public static final RegistryObject<Block> SAKURA_PLANKS = REGISTRY.register("sakura_planks", () -> new Block(SOLID_PROPERTY.get()));
    public static final RegistryObject<Block> SAKURA_PRESSURE_PLATE = REGISTRY.register("sakura_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, NONSOLID_PROPERTY.get()));
    public static final RegistryObject<Block> SAKURA_SAPLING = REGISTRY.register("sakura_sapling", () -> new SaplingBlock(new SakuraTree(), PLANT_PROPERTY.get()));
    public static final RegistryObject<Block> SAKURA_SLAB = REGISTRY.register("sakura_slab", () -> new SlabBlock(NONSOLID_PROPERTY.get()));
    public static final RegistryObject<Block> SAKURA_STAIRS = REGISTRY.register("sakura_stairs", () -> new StairBlock(() -> SAKURA_PLANKS.get().defaultBlockState(), NONSOLID_PROPERTY.get()));
    public static final RegistryObject<Block> SAKURA_WOOD = REGISTRY.register("sakura_wood", () -> new RotatedPillarBlock(SOLID_PROPERTY.get()));
    public static final RegistryObject<Block> SHOJI_BLOCK = REGISTRY.register("shoji_block", () -> new Block(SHOJI_PROPERTY.get()));
    public static final RegistryObject<Block> SHIMENAWA = REGISTRY.register("shimenawa", () -> new ShimenawaBlock(TRANSPARENT_PROPERTY.get()));
    public static final RegistryObject<Block> SHOJI_LANTERN = REGISTRY.register("shoji_lantern", () -> new ShojiLanternBlock(SHOJI_PROPERTY.get()));
    public static final RegistryObject<Block> SHOJI_PANEL = REGISTRY.register("shoji_panel", () -> new TrapDoorBlock(SHOJI_PROPERTY.get()));
    public static final RegistryObject<Block> SHOJI_SCREEN = REGISTRY.register("shoji_screen", () -> new ShojiScreenBlock(SHOJI_PROPERTY.get()));
    public static final RegistryObject<Block> STRIPPED_GINKGO_LOG = REGISTRY.register("stripped_ginkgo_log", () -> new RotatedPillarBlock(SOLID_PROPERTY.get()));
    public static final RegistryObject<Block> STRIPPED_GINKGO_WOOD = REGISTRY.register("stripped_ginkgo_wood", () -> new RotatedPillarBlock(SOLID_PROPERTY.get()));
    public static final RegistryObject<Block> STRIPPED_SAKURA_LOG = REGISTRY.register("stripped_sakura_log", () -> new RotatedPillarBlock(SOLID_PROPERTY.get()));
    public static final RegistryObject<Block> STRIPPED_SAKURA_WOOD = REGISTRY.register("stripped_sakura_wood", () -> new RotatedPillarBlock(SOLID_PROPERTY.get()));
    public static final RegistryObject<Block> TATAMI_MAT = REGISTRY.register("tatami_mat", () -> new RotatedPillarBlock(SOLID_PROPERTY.get()));
    public static final RegistryObject<Block> TORII_TABLET = REGISTRY.register("torii_tablet", () -> new ToriiTabletBlock(TRANSPARENT_PROPERTY.get()));
    public static final RegistryObject<Block> WHITE_PAPER_LANTERN = REGISTRY.register("white_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_WHITE));
    public static final RegistryObject<Block> WHITE_SAKURA_BLOSSOMS = REGISTRY.register("white_sakura_blossoms", () -> new SakuraBlossomsBlock(BlockPartyParticles.WHITE_SAKURA, PLANT_PROPERTY.get()));
    public static final RegistryObject<Block> WHITE_SAKURA_SAPLING = REGISTRY.register("white_sakura_sapling", () -> new SaplingBlock(new WhiteSakuraTree(), PLANT_PROPERTY.get()));
    public static final RegistryObject<Block> WIND_CHIMES = REGISTRY.register("wind_chimes", () -> new WindChimesBlock(STONE_PROPERTY.get()));
    public static final RegistryObject<Block> WISTERIA_BINE = REGISTRY.register("wisteria_bine", () -> new RotatedPillarBlock(NONSOLID_PROPERTY.get()));
    public static final RegistryObject<Block> WISTERIA_LEAVES = REGISTRY.register("wisteria_leaves", () -> new WisteriaLeavesBlock(PLANT_PROPERTY.get()));
    public static final RegistryObject<Block> WISTERIA_SAPLING = REGISTRY.register("wisteria_sapling", () -> new SaplingBlock(new WisteriaTree(), PLANT_PROPERTY.get()));
    public static final RegistryObject<Block> WISTERIA_VINE_BODY = REGISTRY.register("wisteria_vine_body", () -> new WisteriaVineBodyBlock(PLANT_PROPERTY.get()));
    public static final RegistryObject<Block> WISTERIA_VINE_TIP = REGISTRY.register("wisteria_vine_tip", () -> new WisteriaVineTipBlock(PLANT_PROPERTY.get()));
    public static final RegistryObject<Block> WRITING_TABLE = REGISTRY.register("writing_table", () -> new WritingTableBlock(SOLID_PROPERTY.get()));
    public static final RegistryObject<Block> YELLOW_PAPER_LANTERN = REGISTRY.register("yellow_paper_lantern", () -> new PaperLanternBlock(SHOJI_PROPERTY.get(), MaterialColor.TERRACOTTA_YELLOW));
    public static final RegistryObject<Block> POTTED_GINKGO_SAPLING = REGISTRY.register("potted_ginkgo_sapling", () -> new FlowerPotBlock(BlockPartyBlocks::getFlowerPot, GINKGO_SAPLING, BlockBehaviour.Properties.copy(BlockPartyBlocks.getFlowerPot())));
    public static final RegistryObject<Block> POTTED_SAKURA_SAPLING = REGISTRY.register("potted_sakura_sapling", () -> new FlowerPotBlock(BlockPartyBlocks::getFlowerPot, SAKURA_SAPLING, BlockBehaviour.Properties.copy(BlockPartyBlocks.getFlowerPot())));
    public static final RegistryObject<Block> POTTED_WHITE_SAKURA_SAPLING = REGISTRY.register("potted_white_sakura_sapling", () -> new FlowerPotBlock(BlockPartyBlocks::getFlowerPot, WHITE_SAKURA_SAPLING, BlockBehaviour.Properties.copy(BlockPartyBlocks.getFlowerPot())));
    public static final RegistryObject<Block> POTTED_WISTERIA_SAPLING = REGISTRY.register("potted_wisteria_sapling", () -> new FlowerPotBlock(BlockPartyBlocks::getFlowerPot, WISTERIA_SAPLING, BlockBehaviour.Properties.copy(BlockPartyBlocks.getFlowerPot())));

    public static void registerPottedPlants() {
        getFlowerPot().addPlant(SAKURA_SAPLING.get().getRegistryName(), POTTED_SAKURA_SAPLING);
        getFlowerPot().addPlant(WHITE_SAKURA_SAPLING.get().getRegistryName(), POTTED_WHITE_SAKURA_SAPLING);
        getFlowerPot().addPlant(WISTERIA_SAPLING.get().getRegistryName(), POTTED_WISTERIA_SAPLING);
    }

    private static FlowerPotBlock getFlowerPot() {
        return (FlowerPotBlock) Blocks.FLOWER_POT;
    }

    public static void registerRenderTypes() {
        ItemBlockRenderTypes.setRenderLayer(BLANK_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BLACK_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(DAWN_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(EVENING_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(MIDNIGHT_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(MORNING_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(NIGHT_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(NOON_HANGING_SCROLL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BLUE_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BROWN_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(CYAN_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(GARDEN_LANTERN.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(GINKGO_LEAVES.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(GINKGO_SAPLING.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(GRAY_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(GREEN_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(LIGHT_BLUE_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(LIGHT_GRAY_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(LIME_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(MAGENTA_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ORANGE_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(PINK_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SAKURA_BLOSSOMS.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(SAKURA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(POTTED_GINKGO_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(POTTED_SAKURA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(POTTED_WHITE_SAKURA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(POTTED_WISTERIA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(PURPLE_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(RED_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SHIMENAWA.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SHOJI_PANEL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SHOJI_SCREEN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(TORII_TABLET.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WHITE_PAPER_LANTERN.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WHITE_SAKURA_BLOSSOMS.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(WHITE_SAKURA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WISTERIA_LEAVES.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(WISTERIA_SAPLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WISTERIA_VINE_BODY.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(WISTERIA_VINE_TIP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(YELLOW_PAPER_LANTERN.get(), RenderType.cutout());
    }
}
