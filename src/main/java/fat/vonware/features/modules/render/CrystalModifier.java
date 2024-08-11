package fat.vonware.features.modules.render;

import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;

public class CrystalModifier extends Module {

    public CrystalModifier() {
        super("C-Modifier", "modifier but crystal", Category.RENDER, true, false, false);
        INSTANCE = this;
    }

    public static CrystalModifier INSTANCE;
    public final Setting<Float> spin = this.register(new Setting("Spin", 1.0f, 0.0f, 10.0f));
    public final Setting<Float> scale = this.register(new Setting("Scale", 1.0f, 0.0f, 10.0f));
    public final Setting<Float> bounce = this.register(new Setting("Bounce", 1.0f, 0.0f, 10.0f));

}
