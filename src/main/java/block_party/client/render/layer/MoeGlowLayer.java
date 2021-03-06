package block_party.client.render.layer;

import block_party.client.model.MoeModel;
import block_party.mob.Partyer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class MoeGlowLayer extends RenderLayer<Partyer, MoeModel<Partyer>> {
    private final MoeModel<Partyer> model = new MoeModel<>();

    public MoeGlowLayer(RenderLayerParent<Partyer, MoeModel<Partyer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, Partyer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (this.isWithinDistance(entity.position()) && !entity.isInvisible() && entity.isBlockGlowing()) {
            this.getParentModel().renderToBuffer(stack, buffer.getBuffer(RenderType.eyes(this.getTexture(entity))), 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public boolean isWithinDistance(Vec3 pos) {
        Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
        return renderInfo.getPosition().distanceTo(pos) < 16;
    }

    public ResourceLocation getTexture(Partyer entity) {
        return Partyer.Overrides.getNameOf(entity.getExternalBlockState(), "glow");
    }
}