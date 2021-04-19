package moeblocks.init;

import moeblocks.MoeMod;
import moeblocks.particle.PinkSakuraParticle;
import moeblocks.particle.WhiteSakuraParticle;
import moeblocks.particle.YellowSakuraParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class MoeParticles {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MoeMod.ID);
    public static final RegistryObject<BasicParticleType> PINK_SAKURA_PETAL = REGISTRY.register("pink_sakura_petal", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> WHITE_SAKURA_PETAL = REGISTRY.register("white_sakura_petal", () -> new BasicParticleType(false));
    public static final RegistryObject<BasicParticleType> YELLOW_SAKURA_PETAL = REGISTRY.register("yellow_sakura_petal", () -> new BasicParticleType(false));

    public static void registerParticleFactories(final ParticleFactoryRegisterEvent e) {
        Minecraft.getInstance().particles.registerFactory(MoeParticles.PINK_SAKURA_PETAL.get(), PinkSakuraParticle.Factory::new);
        Minecraft.getInstance().particles.registerFactory(MoeParticles.WHITE_SAKURA_PETAL.get(), WhiteSakuraParticle.Factory::new);
        Minecraft.getInstance().particles.registerFactory(MoeParticles.YELLOW_SAKURA_PETAL.get(), YellowSakuraParticle.Factory::new);
    }
}
