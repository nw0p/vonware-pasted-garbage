package fat.vonware.util;


import net.minecraft.init.MobEffects;

import java.util.Objects;

public class MovementUtil implements Util {

    public static boolean anyMovementKeys() {
        return MovementUtil.mc.player.movementInput.forwardKeyDown || MovementUtil.mc.player.movementInput.backKeyDown || MovementUtil.mc.player.movementInput.leftKeyDown || MovementUtil.mc.player.movementInput.rightKeyDown || MovementUtil.mc.player.movementInput.jump || MovementUtil.mc.player.movementInput.sneak;
    }

    public static double getSpeed() {
        return MovementUtil.getSpeed(false);
    }
    public static boolean isMoving() {
        return MovementUtil.mc.player.moveForward != 0.0 || MovementUtil.mc.player.moveStrafing != 0.0;
    }
    public static double[] directionSpeed(final double speed) {
        float forward = MovementUtil.mc.player.movementInput.moveForward;
        float side = MovementUtil.mc.player.movementInput.moveStrafe;
        float yaw = MovementUtil.mc.player.prevRotationYaw + (MovementUtil.mc.player.rotationYaw - MovementUtil.mc.player.prevRotationYaw) * MovementUtil.mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            }
            else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            }
            else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[] { posX, posZ };
    }

    public static double getSpeed(boolean slowness) {
        int amplifier;
        double defaultSpeed = 0.2873;
        if (MovementUtil.mc.player.isPotionActive(MobEffects.SPEED)) {
            amplifier = Objects.requireNonNull(MovementUtil.mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        if (slowness && MovementUtil.mc.player.isPotionActive(MobEffects.SLOWNESS)) {
            amplifier = Objects.requireNonNull(MovementUtil.mc.player.getActivePotionEffect(MobEffects.SLOWNESS)).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (double)(amplifier + 1);
        }
        return defaultSpeed;
    }

}