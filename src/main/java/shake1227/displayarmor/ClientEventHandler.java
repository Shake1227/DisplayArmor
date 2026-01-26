package shake1227.displayarmor;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Displayarmor.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    public static boolean hasAgreed = false;

    @SubscribeEvent
    public static void onGuiInit(ScreenEvent.Init.Post event) {
        if (!hasAgreed && event.getScreen() instanceof TitleScreen) {
            event.getScreen().getMinecraft().setScreen(new DisclaimerScreen(event.getScreen()));
        }
    }
}