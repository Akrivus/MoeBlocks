package moeblocks.block.tree;

import moeblocks.init.MoeBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.treedecorator.LeaveVineTreeDecorator;
import net.minecraft.world.gen.trunkplacer.ForkyTrunkPlacer;

import java.util.Random;
import java.util.function.Supplier;

public class WisteriaTree extends Tree {
    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random random, boolean hive) {
        return Feature.TREE.withConfiguration(
                new BaseTreeFeatureConfig.Builder(
                        new SimpleBlockStateProvider(MoeBlocks.WISTERIA_BINE.get().getDefaultState()),
                        new SimpleBlockStateProvider(MoeBlocks.WISTERIA_LEAVES.get().getDefaultState()),
                        new BlobFoliagePlacer(FeatureSpread.func_242252_a(1), FeatureSpread.func_242252_a(0), 2),
                        new ForkyTrunkPlacer(4, 0, 2),
                        new TwoLayerFeature(2, 1, 2)
                ).setIgnoreVines().build());
    }
}
