package fat.vonware.features.modules.render;

import fat.vonware.features.modules.Module;
import net.minecraft.client.settings.*;

public class HandSwitch extends Module
{
    public HandSwitch() {
        super("HandSwitch", "im too lazy to go into settings", Category.RENDER, false, false, false);
    }

    public void onEnable() {
        HandSwitch.mc.gameSettings.setOptionValue(GameSettings.Options.MAIN_HAND, 1);
    }

    public void onDisable() {
        HandSwitch.mc.gameSettings.setOptionValue(GameSettings.Options.MAIN_HAND, 0);
    }
}
