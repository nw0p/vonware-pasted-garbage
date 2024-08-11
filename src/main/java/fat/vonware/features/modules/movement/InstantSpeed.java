package fat.vonware.features.modules.movement;

import fat.vonware.event.events.MoveEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.util.EntityUtil;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

public class InstantSpeed extends Module {

    public InstantSpeed() {
        super("InstantSpeed", "instant speed", Category.MOVEMENT, true, false, false);
        this.setInstance();
    }
    private static InstantSpeed INSTANCE = new InstantSpeed();
    @SubscribeEvent
    public void onMove(MoveEvent event) {
        double[] speed = EntityUtil.forward(getSpeed(true));
        event.setX(speed[0]);
        event.setZ(speed[1]);
    }

    public static InstantSpeed getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InstantSpeed();
        }
        return INSTANCE;
    }
    private void setInstance() {
        INSTANCE = this;
    }


    public static double getSpeed(boolean slowness) {
        double defaultSpeed = 0.2873;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        if (slowness && mc.player.isPotionActive(MobEffects.SLOWNESS)) {
            int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SLOWNESS)).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (amplifier + 1);
        }
        return defaultSpeed;
    }
}
