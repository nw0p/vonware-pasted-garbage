package fat.vonware.features.modules.movement;

import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.EntityUtil;
import fat.vonware.util.Timer;
import net.minecraft.entity.Entity;

public class FastSwim
        extends Module {
    public Setting<Boolean> pulse = this.register(new Setting<Boolean>("Pulse", true));
    public Setting<Float> fastWhenPassed = this.register(new Setting<Float>("FastWhenPassed", 250.0f, 0.0f, 2000.0f, v -> this.pulse.getValue()));
    public Setting<Float> fastForTime = this.register(new Setting<Float>("FastForTime", 250.0f, 0.0f, 2000.0f, v -> this.pulse.getValue()));
    public Setting<Boolean> fall = this.register(new Setting<Boolean>("Fall", false));
    public Setting<Float> waterHorizontal = this.register(new Setting<Float>("WaterHorizontal", 1.0f, 1.0f, 20.0f));
    public Setting<Float> waterUp = this.register(new Setting<Float>("WaterUp", 1.0f, 1.0f, 20.0f));
    public Setting<Float> waterDown = this.register(new Setting<Float>("WaterDown", 1.0f, 1.0f, 20.0f));

    public Setting<Float> lavaHorizontal = this.register(new Setting<Float>("LavaHorizontal", 2.89f, 1.0f, 20.0f));
    public Setting<Float> lavaUp = this.register(new Setting<Float>("LavaUp", 3.08f, 1.0f, 20.0f));
    public Setting<Float> lavaDown = this.register(new Setting<Float>("LavaDown", 2.09f, 1.0f, 20.0f));
    private Timer timer = new Timer();

    public FastSwim() {
        super("FastSwim", "Swim fast", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onDisable() {
        timer.reset();
    }

    @Override
    public void onUpdate() {
        if (this.pulse.getValue() && timer.passed((long) this.fastWhenPassed.getValue().floatValue())) {
            if (FastSwim.mc.player.isInLava() && !FastSwim.mc.player.onGround) {
                EntityUtil.moveEntityStrafe(0.05 * this.lavaHorizontal.getValue().floatValue(), (Entity)FastSwim.mc.player);
                if (FastSwim.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    FastSwim.mc.player.motionY = 0.05f * -this.lavaDown.getValue();
                } else if (FastSwim.mc.gameSettings.keyBindJump.isKeyDown()) {
                    FastSwim.mc.player.motionY = 0.05f * this.lavaUp.getValue();
                } else {
                    if (!this.fall.getValue()) {
                        FastSwim.mc.player.motionY = 0.0f;
                    }
                }
            } else if (FastSwim.mc.player.isInWater() && !FastSwim.mc.player.onGround) {
                EntityUtil.moveEntityStrafe(0.05 * this.waterHorizontal.getValue().floatValue(), (Entity)FastSwim.mc.player);
                if (FastSwim.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    FastSwim.mc.player.motionY = 0.05f * -this.waterDown.getValue();
                } else if (FastSwim.mc.gameSettings.keyBindJump.isKeyDown()) {
                    FastSwim.mc.player.motionY = 0.05f * this.waterUp.getValue();
                } else {
                    if (!this.fall.getValue()) {
                        FastSwim.mc.player.motionY = 0.0f;
                    }
                }
            }
            if (timer.passed((long) this.fastForTime.getValue().floatValue())) {
                timer.reset();
            }
        } else {
            if (FastSwim.mc.player.isInLava() && !FastSwim.mc.player.onGround) {
                EntityUtil.moveEntityStrafe(0.05 * this.lavaHorizontal.getValue().floatValue(), (Entity)FastSwim.mc.player);
                if (FastSwim.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    FastSwim.mc.player.motionY = 0.05f * -this.lavaDown.getValue();
                } else if (FastSwim.mc.gameSettings.keyBindJump.isKeyDown()) {
                    FastSwim.mc.player.motionY = 0.05f * this.lavaUp.getValue();
                } else {
                    if (!this.fall.getValue()) {
                        FastSwim.mc.player.motionY = 0.0f;
                    }
                }
            } else if (FastSwim.mc.player.isInWater() && !FastSwim.mc.player.onGround) {
                EntityUtil.moveEntityStrafe(0.05 * this.waterHorizontal.getValue().floatValue(), (Entity)FastSwim.mc.player);
                if (FastSwim.mc.gameSettings.keyBindSneak.isKeyDown()) {
                    FastSwim.mc.player.motionY = 0.05f * -this.waterDown.getValue();
                } else if (FastSwim.mc.gameSettings.keyBindJump.isKeyDown()) {
                    FastSwim.mc.player.motionY = 0.05f * this.waterUp.getValue();
                } else {
                    if (!this.fall.getValue()) {
                        FastSwim.mc.player.motionY = 0.0f;
                    }
                }
            }
        }
    }
}
