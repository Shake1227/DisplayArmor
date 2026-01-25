package shake1227.displayarmor;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ArmorOverlayRenderer {

    @SubscribeEvent
    public void onRenderNameTag(RenderNameTagEvent event) {
        // プレイヤー以外には表示しない
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // スペクテイターモードや非表示のプレイヤーは除外
        if (player.isSpectator() || player.isInvisible()) {
            return;
        }

        // カメラから遠すぎる場合は表示しない（64ブロック）
        if (event.getEntity().distanceToSqr(Minecraft.getInstance().cameraEntity) > 4096.0) {
            return;
        }

        renderArmorAboveNameTag(event, player);
    }

    private void renderArmorAboveNameTag(RenderNameTagEvent event, Player player) {
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        // --- 座標調整 ---
        // RenderNameTagEventの時点では、既に以下の変換がかかっています：
        // 1. プレイヤーの頭上に移動 (Translate)
        // 2. カメラの方向へ回転 (Rotate)
        // 3. スケール縮小 (Scale: -0.025, -0.025, 0.025) -> Y軸が反転しており、1単位が約1ピクセル相当

        // テキストの少し上に移動 (Y軸マイナス方向が「上」)
        // テキストの高さ(約9) + マージン分として -27.0 程度移動
        poseStack.translate(0, -27.0f, 0);

        // --- スケールと向きの補正 ---
        // 現在の座標系ではY軸が下向き(反転)になっているため、アイテムを正立させるためにYスケールをマイナスにする
        // また、サイズを大きくして視認できるようにする (16.0f倍 ≒ 16ピクセルサイズ)
        float itemScale = 16.0f;
        poseStack.scale(itemScale, -itemScale, itemScale);

        // --- 壁越し判定 (Depth Test) ---
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515); // GL_LEQUAL

        // --- アイテムの取得 ---
        // 左から 頭 -> 胸 -> 足 -> ブーツ
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        ItemStack[] armors = { head, chest, legs, boots };

        // アイテム間の間隔 (1.0 = 1アイテム分の幅)
        float spacing = 1.05f;
        // 全体の幅を計算して中央揃え
        float totalWidth = (armors.length - 1) * spacing;
        float startX = -totalWidth / 2.0f;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        for (int i = 0; i < armors.length; i++) {
            ItemStack stack = armors[i];
            if (!stack.isEmpty()) {
                poseStack.pushPose();

                // 横並びに配置
                poseStack.translate(startX + (i * spacing), 0, 0);

                // アイテムの描画
                // ItemDisplayContext.FIXED を使用 (GUI等だとライティングが変わるためFIXED推奨)
                BakedModel bakedModel = itemRenderer.getModel(stack, player.level(), player, i);

                itemRenderer.render(
                        stack,
                        ItemDisplayContext.FIXED,
                        false,
                        poseStack,
                        event.getMultiBufferSource(),
                        event.getPackedLight(), // ネームタグと同じ明るさ
                        OverlayTexture.NO_OVERLAY,
                        bakedModel
                );

                poseStack.popPose();
            }
        }

        // 設定を戻す
        RenderSystem.disableDepthTest();
        poseStack.popPose();
    }
}