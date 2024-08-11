package fat.vonware.features.modules.render;

import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;

public class Ambience extends Module {

    public Ambience() {
        super("Ambience", "yeah", Category.RENDER, true, false, false);
        this.register(red);
        this.register(green);
        this.register(blue);
        this.register(alpha);
    }

    public static Setting<Integer> red = new Setting<>("Red", 255, 0, 255);
    public static Setting<Integer> green = new Setting<>("Green", 255, 0, 255);
    public static Setting<Integer> blue = new Setting<>("Blue", 255, 0, 255);
    public static Setting<Integer> alpha = new Setting<>("Alpha", 255, 250, 255);

}
