package fat.vonware.features.modules.render;

import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;

import java.awt.*;

public class ShulkerViewer extends Module {

    public ShulkerViewer() {
        super("ShulkerViewer", "skull", Category.RENDER, true, false, false);
        INSTANCE = this;
    }

    public static ShulkerViewer INSTANCE;
    public Setting<Integer> red = this.register(new Setting("Red", 30, 0, 255));
    public Setting<Integer> green = this.register(new Setting("Green", 167, 0, 255));
    public Setting<Integer> blue = this.register(new Setting("Blue", 255, 0, 255));
    public Setting<Integer> alpha = this.register(new Setting("Alpha", 70, 0, 255));

    public Color getColor() {
        return new Color(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
    }
}
