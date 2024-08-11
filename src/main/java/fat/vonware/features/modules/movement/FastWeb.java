package fat.vonware.features.modules.movement;

import fat.vonware.Vonware;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;

public class FastWeb extends Module {
    private final Setting<Mode> mode = this.register(new Setting<>("Mode",  Mode.FAST));
    private final Setting<Float> fastSpeed = this.register(new Setting<Float>("FastSpeed", 3.0f, 0.0f, 5.0f, v -> mode.getValue() == Mode.FAST));
    public FastWeb() {
        super("FastWeb", "So you don't need to keep timer on keybind", Category.MOVEMENT, true, false,false);
    }

    private enum Mode {
        FAST,
        STRICT
    }

    @Override
    public void onDisable() {
        Vonware.timerManager.reset();
    }

    @Override
    public void onUpdate() {
        if (fullNullCheck()) return;

        if (mc.player.isInWeb) {

            if (mode.getValue() == Mode.FAST && mc.gameSettings.keyBindSneak.isKeyDown()) {
                Vonware.timerManager.reset();
                mc.player.motionY -= fastSpeed.getValue();

            } else if (mode.getValue() == Mode.STRICT && !mc.player.onGround && mc.gameSettings.keyBindSneak.isKeyDown()) {
                Vonware.timerManager.set(8);

            } else {
                Vonware.timerManager.reset();
            }

        } else {
            Vonware.timerManager.reset();
        }
    }
}
