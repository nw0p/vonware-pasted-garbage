package fat.vonware.features.modules.player;


import fat.vonware.event.events.TurnEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FreeLook extends Module {
    private float dYaw;
    private float dPitch;
    private Setting<Boolean> autoThirdPerson = this.register(new Setting<>("AutoThirdPerson", true));

    public FreeLook() {
        super("FreeLook", "FreeLook", Category.PLAYER, true, false,false);
        this.dPitch = 0.0f;
    }

    public void onEnable() {
        this.dYaw = 0.0f;
        this.dPitch = 0.0f;
        if (this.autoThirdPerson.getValue()) {
            mc.gameSettings.thirdPersonView = 1;
        }
    }

    public void onDisable() {
        if (this.autoThirdPerson.getValue()) {
            mc.gameSettings.thirdPersonView = 0;
        }
    }

    @SubscribeEvent
    public void onCameraSetup(final EntityViewRenderEvent.CameraSetup event) {
        if (mc.gameSettings.thirdPersonView > 0) {
            event.setYaw(event.getYaw() + this.dYaw);
            event.setPitch(event.getPitch() + this.dPitch);
        }
    }

    @SubscribeEvent
    public void onTurnEvent(final TurnEvent event) {
        if (mc.gameSettings.thirdPersonView > 0) {
            this.dYaw += (float) (event.getYaw() * 0.15);
            this.dPitch -= (float) (event.getPitch() * 0.15);
            this.dPitch = MathHelper.clamp(this.dPitch, -90.0f, 90.0f);
            event.isCancelable();
        }
    }
}