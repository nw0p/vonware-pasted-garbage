package fat.vonware.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import fat.vonware.Vonware;
import fat.vonware.event.events.ClientEvent;
import fat.vonware.features.command.Command;
import fat.vonware.features.gui.VonwareGui;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Bind;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.Util;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ClickGui
        extends Module {
    private static ClickGui INSTANCE = new ClickGui();

    public Setting<String> prefix = this.register(new Setting<String>("Prefix", ","));
    public Setting<Boolean> customFov = this.register(new Setting<Boolean>("CustomFov", false));
    public Setting<Float> fov = this.register(new Setting<Float>("Fov", Float.valueOf(150.0f), Float.valueOf(-180.0f), Float.valueOf(180.0f)));

    public Setting<Integer> red = this.register(new Setting<Integer>("Red", 160, 0, 255));
    public Setting<Integer> green = this.register(new Setting<Integer>("Green", 96, 0, 255));
    public Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 195, 0, 255));
    public Setting<Integer> hoverAlpha = this.register(new Setting<Integer>("Alpha", 180, 0, 255));

    public Setting<Integer> bgred = this.register(new Setting<Integer>("BG-Red", 160, 0, 255));
    public Setting<Integer> bggreen = this.register(new Setting<Integer>("BG-Green", 96, 0, 255));
    public Setting<Integer> bgblue = this.register(new Setting<Integer>("BG-Blue", 195, 0, 255));
    public Setting<Integer> bgalpha = this.register(new Setting<Integer>("BG-Alpha", 60, 0, 255));

    public Setting<Integer> alpha = this.register(new Setting<Integer>("HoverAlpha", 255, 0, 255));
    public Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", false));
    public Setting<rainbowMode> rainbowModeHud = this.register(new Setting<Object>("HRainbowMode", rainbowMode.Static, v -> this.rainbow.getValue()));
    public Setting<rainbowModeArray> rainbowModeA = this.register(new Setting<Object>("ARainbowMode", rainbowModeArray.Static, v -> this.rainbow.getValue()));
    public Setting<Integer> rainbowHue = this.register(new Setting<Object>("Delay", Integer.valueOf(240), Integer.valueOf(0), Integer.valueOf(600), v -> this.rainbow.getValue()));
    public Setting<Float> rainbowBrightness = this.register(new Setting<Object>("Brightness ", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> this.rainbow.getValue()));
    public Setting<Float> rainbowSaturation = this.register(new Setting<Object>("Saturation", Float.valueOf(150.0f), Float.valueOf(1.0f), Float.valueOf(255.0f), v -> this.rainbow.getValue()));
    private VonwareGui click;
    public Setting<Mode> particleMode = this.register(new Setting<Object>("Particle", Mode.Normal));
    public Setting<Integer> pSpeed = this.register(new Setting<Object>("Particle Speed", 8, 1, 15, v -> this.particleMode.getValue() == Mode.Normal));
    public Setting<Integer> pRed = this.register(new Setting<Integer>("Particle Red", 255, 0, 255, v -> this.particleMode.getValue() == Mode.Normal));
    public Setting<Integer> pGreen = this.register(new Setting<Integer>("Particle Green", 0, 0, 255, v -> this.particleMode.getValue() == Mode.Normal));
    public Setting<Integer> pBlue = this.register(new Setting<Integer>("Particle Blue", 255, 0, 255, v -> this.particleMode.getValue() == Mode.Normal));
    public Setting<Integer> pAlpha = this.register(new Setting<Integer>("Particle Alpha", 255, 0, 255, v -> this.particleMode.getValue() == Mode.Normal));



    public ClickGui() {
        super("ClickGui", "Opens the ClickGui", Module.Category.CLIENT, true, false, false);
        this.setInstance();
        this.bind.setValue(new Bind(Keyboard.KEY_L));
    }

    public static ClickGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.customFov.getValue().booleanValue()) {
            ClickGui.mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, this.fov.getValue().floatValue());
        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting().getFeature().equals(this)) {
            if (event.getSetting().equals(this.prefix)) {
                Vonware.commandManager.setPrefix(this.prefix.getPlannedValue());
                Command.sendMessage("Prefix set to " + ChatFormatting.DARK_GRAY + Vonware.commandManager.getPrefix());
            }
            Vonware.colorManager.setColor(this.red.getPlannedValue(), this.green.getPlannedValue(), this.blue.getPlannedValue(), this.hoverAlpha.getPlannedValue());
        }
    }
    public Color getGuiColor() {
        return new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue());
    }

    public Color getGuiBackgroundColor() {
        return new Color(this.bgred.getValue(), this.bggreen.getValue(), this.bgblue.getValue(), this.bgalpha.getValue());
    }



    @Override
    public void onEnable() {
        Util.mc.displayGuiScreen(VonwareGui.getClickGui());
    }

    @Override
    public void onLoad() {
        Vonware.colorManager.setColor(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.hoverAlpha.getValue());
        Vonware.commandManager.setPrefix(this.prefix.getValue());
    }

    @Override
    public void onTick() {
        if (!(ClickGui.mc.currentScreen instanceof VonwareGui)) {
            this.disable();
        }
    }
    public final Setting<Integer> height = (new Setting<>("ButtonHeight", 4, 1, 5));
    public int getButtonHeight() {
        return 11 + height.getValue();
    }

    public enum rainbowModeArray {
        Static,
        Up

    }
    public enum Mode {
        Normal,
        Snowing,
        None
    }

    public enum rainbowMode {
        Static,
        Sideway

    }
}

