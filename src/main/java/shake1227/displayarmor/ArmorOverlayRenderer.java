package shake1227.displayarmor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class ArmorOverlayRenderer {

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();

        if (player.isSpectator() || player.isInvisible()) {
            return;
        }

        if (player.distanceToSqr(Minecraft.getInstance().cameraEntity) > 4096.0) {
            return;
        }

        renderArmorOverhead(event, player);
    }

    private void renderArmorOverhead(RenderPlayerEvent.Post event, Player player) {
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        double height = player.getBbHeight() + 0.6;
        poseStack.translate(0, height, 0);
        Quaternionf cameraRotation = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
        poseStack.mulPose(cameraRotation);
        float scale = 0.3f;
        poseStack.scale(scale, scale, scale);
        RenderSystem.disableDepthTest();
        int packedLight = LightTexture.FULL_BRIGHT;
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        ItemStack[] armors = { boots, legs, chest, head };

        float spacing = 0.9f;
        float totalWidth = (armors.length - 1) * spacing;
        float startX = -totalWidth / 2.0f;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        for (int i = 0; i < armors.length; i++) {
            ItemStack stack = armors[i];
            if (!stack.isEmpty()) {
                poseStack.pushPose();
                poseStack.translate(startX + (i * spacing), 0, 0);

                BakedModel bakedModel = itemRenderer.getModel(stack, player.level(), player, i);

                itemRenderer.render(
                        stack,
                        ItemDisplayContext.FIXED,
                        false,
                        poseStack,
                        event.getMultiBufferSource(),
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        bakedModel
                );

                poseStack.popPose();
            }
        }
        RenderSystem.enableDepthTest();
        poseStack.popPose();
    }
}