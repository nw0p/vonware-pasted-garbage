package fat.vonware.features.modules.misc;

import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;

public class NameHider extends Module {

    public NameHider() {
        super("NameHider", "hider but name", Category.MISC, true, false, false);
        INSTANCE = this;
    }

    public static NameHider INSTANCE;
    public final Setting<String> newName = this.register(new Setting("[REDACTED]", "[REDACTED]"));
}
