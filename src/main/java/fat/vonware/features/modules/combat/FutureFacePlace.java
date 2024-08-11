package fat.vonware.features.modules.combat;

import fat.vonware.features.command.Command;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;

public class FutureFacePlace extends Module {

    public FutureFacePlace() {
        super("FutureMinDamage", "stes future min dmg ca to 0.0 or 6.0", Category.COMBAT, true, false, false);
    }
    public final Setting<Float> base = this.register(new Setting("BaseDamage", 6.0f, 0.0f, 20.0f));

    public final Setting<Float> enableDamage = this.register(new Setting("Faceplace", 1.0f, 0.0f, 6.0f));

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        mc.player.sendChatMessage(".Autocrystal mindamage " + ( base.getValue()));
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        mc.player.sendChatMessage(".Autocrystal mindamage " + (enableDamage.getValue()));

    }

}