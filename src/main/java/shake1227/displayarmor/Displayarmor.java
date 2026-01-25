package shake1227.displayarmor;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("displayarmor")
public class Displayarmor {

    public static final String MODID = "displayarmor";

    public Displayarmor() {
        // クライアントセットアップイベントの登録
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        // レンダラーの登録
        MinecraftForge.EVENT_BUS.register(new ArmorOverlayRenderer());
    }
}