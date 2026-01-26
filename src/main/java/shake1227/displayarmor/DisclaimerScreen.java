package shake1227.displayarmor;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.time.Year;

public class DisclaimerScreen extends Screen {

    private final Screen lastScreen;
    private boolean isJapanese = true;
    private static final int TITLE_COLOR = 0xFFFF55;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int WARNING_COLOR = 0xFF5555;
    private static final int GRAY_COLOR = 0xAAAAAA;

    public DisclaimerScreen(Screen lastScreen) {
        super(Component.literal("Disclaimer"));
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();

        int buttonWidth = 150;
        int buttonHeight = 20;
        int centerX = this.width / 2;
        int bottomY = this.height - 60;
        String agreeText = isJapanese ? "同意する" : "Agree";
        this.addRenderableWidget(Button.builder(Component.literal(agreeText), button -> {
                    ClientEventHandler.hasAgreed = true;
                    this.minecraft.setScreen(this.lastScreen);
                })
                .bounds(centerX - buttonWidth - 5, bottomY, buttonWidth, buttonHeight)
                .build());
        String disagreeText = isJapanese ? "同意しない (終了)" : "Disagree (Exit)";
        this.addRenderableWidget(Button.builder(Component.literal(disagreeText), button -> {
                    this.minecraft.stop();
                })
                .bounds(centerX + 5, bottomY, buttonWidth, buttonHeight)
                .build());
        int langBtnWidth = 80;
        int langBtnHeight = 20;
        String langBtnText = isJapanese ? "English" : "日本語";

        this.addRenderableWidget(Button.builder(Component.literal(langBtnText), button -> {
                    this.isJapanese = !this.isJapanese;
                    this.rebuildWidgets();
                })
                .bounds(this.width - langBtnWidth - 10, this.height - langBtnHeight - 10, langBtnWidth, langBtnHeight)
                .build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        int centerX = this.width / 2;
        int startY = 30;
        int lineHeight = 14;
        guiGraphics.drawCenteredString(this.font, "DisplayArmor", centerX, startY, TITLE_COLOR);
        startY += 20;
        String title = isJapanese ? "【 警告と免責事項 】" : "WARNING & DISCLAIMER";
        guiGraphics.drawCenteredString(this.font, title, centerX, startY, WARNING_COLOR);
        startY += 25;
        if (isJapanese) {
            drawCenteredString(guiGraphics, "・このMODはチート目的で開発されたものではありません。", centerX, startY, TEXT_COLOR);
            startY += lineHeight;
            drawCenteredString(guiGraphics, "・許可されているサーバー以外で使用しないでください。", centerX, startY, TEXT_COLOR);
            startY += lineHeight;
            drawCenteredString(guiGraphics, "(サーバーによってはチートとして処理される可能性があります)", centerX, startY, GRAY_COLOR);
            startY += lineHeight;
            drawCenteredString(guiGraphics, "・開発者はこのMODを使用して起きた一切の責任を持ちません。", centerX, startY, TEXT_COLOR);
        } else {
            drawCenteredString(guiGraphics, "- This mod is NOT developed for cheating purposes.", centerX, startY, TEXT_COLOR);
            startY += lineHeight;
            drawCenteredString(guiGraphics, "- Do not use this on unauthorized servers.", centerX, startY, TEXT_COLOR);
            startY += lineHeight;
            drawCenteredString(guiGraphics, "(It may be detected as a cheat depending on the server)", centerX, startY, GRAY_COLOR);
            startY += lineHeight;
            drawCenteredString(guiGraphics, "- The developer assumes NO responsibility for any consequences.", centerX, startY, TEXT_COLOR);
        }
        int footerY = this.height - 30;
        guiGraphics.drawCenteredString(this.font, "License: CC BY-NC 4.0", centerX, footerY, GRAY_COLOR);
        int currentYear = Year.now().getValue();
        String copyright = "Copyright © 2025-" + currentYear + " Shake.";
        guiGraphics.drawCenteredString(this.font, copyright, centerX, footerY + 12, GRAY_COLOR);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    private void drawCenteredString(GuiGraphics gfx, String text, int x, int y, int color) {
        gfx.drawCenteredString(this.font, text, x, y, color);
    }
}