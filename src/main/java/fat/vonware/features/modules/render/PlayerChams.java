package fat.vonware.features.modules.render;

import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerChams
        extends Module {
    private static PlayerChams INSTANCE = new PlayerChams();
    public final Setting<Float> alpha = this.register(new Setting<Float>("PAlpha", Float.valueOf(255.0f), Float.valueOf(0.1f), Float.valueOf(255.0f)));
   public final Setting<Float> lineWidth = this.register(new Setting<Float>("PLineWidth", Float.valueOf(1.0f), Float.valueOf(0.1f), Float.valueOf(3.0f)));
   public Setting<RenderMode> mode = this.register(new Setting<RenderMode>("PMode", RenderMode.SOLID));
    public Setting<Boolean> players = this.register(new Setting<Boolean>("Players", Boolean.FALSE));
    public Setting<Boolean> playerModel = this.register(new Setting<Boolean>("PlayerModel", Boolean.FALSE));

    public PlayerChams() {
        super("Wireframe", "Draws a wireframe esp around other players.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static PlayerChams getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerChams();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRenderPlayerEvent(RenderPlayerEvent.Pre event) {
        event.getEntityPlayer().hurtTime = 0;
    }

    public enum RenderMode {
        SOLID,
        WIREFRAME

    }
}

