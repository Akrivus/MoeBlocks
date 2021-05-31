package moeblocks.block.tree;

import moeblocks.init.MoeBlocks;
import net.minecraft.block.trees.Tree;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.AcaciaFoliagePlacer;
import net.minecraft.world.gen.trunkplacer.ForkyTrunkPlacer;

import java.util.Random;

public class WisteriaTree extends Tree {
    @Override
    protected ConfiguredFeature<BaseTreeFeatureConfig, ?> getTreeFeature(Random random, boolean hive) {
        return WisteriaTree.build();
    }

    public static ConfiguredFeature build() {
        return Feature.TREE.withConfiguration(new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(MoeBlocks.WISTERIA_BINE.get().getDefaultState()), new SimpleBlockStateProvider(MoeBlocks.WISTERIA_LEAVES.get().getDefaultState()), new AcaciaFoliagePlacer(FeatureSpread.create(2), FeatureSpread.create(1)), new ForkyTrunkPlacer(4, 1, 2), new TwoLayerFeature(1, 0, 2)).setIgnoreVines().build());
    }
}
