package fat.vonware.features.modules.render;

import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;

public class SmallShield extends Module
{
    private static SmallShield INSTANCE;
    public Setting<Boolean> normalOffset;
    public Setting<Float> offset;
    public Setting<Float> offX;
    public Setting<Float> offY;
    public Setting<Float> mainX;
    public Setting<Float> mainY;

    public SmallShield() {
        super("SmallShield", "Makes you offhand lower.", Module.Category.RENDER, false, false, false);
        this.normalOffset = (Setting<Boolean>)this.register(new Setting("OffNormal", false));
        this.offset = (Setting<Float>)this.register(new Setting("Offset", 0.7f, 0.0f, 1.0f, v -> this.normalOffset.getValue()));
        this.offX = (Setting<Float>)this.register(new Setting("OffX", 0.0f, (-1.0f), 1.0f, v -> !this.normalOffset.getValue()));
        this.offY = (Setting<Float>)this.register(new Setting("OffY", 0.0f, (-1.0f), 1.0f, v -> !this.normalOffset.getValue()));
        this.mainX = (Setting<Float>)this.register(new Setting("MainX", 0.0f, (-1.0f), 1.0f));
        this.mainY = (Setting<Float>)this.register(new Setting("MainY", 0.0f, (-1.0f), 1.0f));
        this.setInstance();
    }

    public static SmallShield getINSTANCE() {
        if (SmallShield.INSTANCE == null) {
            SmallShield.INSTANCE = new SmallShield();
        }
        return SmallShield.INSTANCE;
    }

    private void setInstance() {
        SmallShield.INSTANCE = this;
    }

    public void onUpdate() {
        if (this.normalOffset.getValue()) {
            SmallShield.mc.entityRenderer.itemRenderer.equippedProgressOffHand = this.offset.getValue();
        }
    }

    static {
        SmallShield.INSTANCE = new SmallShield();
    }
}
