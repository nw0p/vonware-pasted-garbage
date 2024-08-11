package fat.vonware.manager;

import fat.vonware.features.Feature;
import fat.vonware.mixin.mixins.accessor.IEntityPlayerSP;
import fat.vonware.util.MathUtil;
import fat.vonware.util.RotationUtil;
import fat.vonware.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationManager
        extends Feature {
    private float yaw;
    private float pitch;
    private boolean rotated;
    private int ticksSinceNoRotate;

    public void updateRotations() {
        this.yaw = RotationManager.mc.player.rotationYaw;
        this.pitch = RotationManager.mc.player.rotationPitch;
    }
    public void lookAtVec3dPacket(Vec3d vec, boolean normalize, boolean update) {
        float[] angle = getAngle(vec);
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(angle[0], normalize ? (float) MathHelper.normalizeAngle((int) angle[1], 360) : angle[1], mc.player.onGround));

        if (update) {
            ((IEntityPlayerSP) mc.player).setLastReportedYaw(angle[0]);
            ((IEntityPlayerSP) mc.player).setLastReportedPitch(angle[1]);
        }
    }
    public float[] getAngle(Vec3d vec) {
        Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ);
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{ mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)};
    }

    public static float[] calculateAngle(Vec3d vec3d, Vec3d vec3d2) {
        double d = vec3d2.x - vec3d.x;
        double d2 = (vec3d2.y - vec3d.y) * -1.0;
        double d3 = vec3d2.z - vec3d.z;
        double d4 = MathHelper.sqrt((double)(d * d + d3 * d3));
        float f = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(d3, d)) - 90.0));
        float f2 = (float)MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(d2, d4)));
        if (f2 > 90.0f) {
            f2 = 90.0f;
        } else if (f2 < -90.0f) {
            f2 = -90.0f;
        }
        return new float[]{f, f2};
    }

    public void setRotations(float yaw, float pitch) {
        rotated = true;
        ticksSinceNoRotate = 0;
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.rotationPitch = pitch;
    }

    public void restoreRotations() {
        RotationManager.mc.player.rotationYaw = this.yaw;
        RotationManager.mc.player.rotationYawHead = this.yaw;
        RotationManager.mc.player.rotationPitch = this.pitch;
    }

    public void setPlayerRotations(float yaw, float pitch) {
        RotationManager.mc.player.rotationYaw = yaw;
        RotationManager.mc.player.rotationYawHead = yaw;
        RotationManager.mc.player.rotationPitch = pitch;
    }

    public void setPlayerYaw(float yaw) {
        RotationManager.mc.player.rotationYaw = yaw;
        RotationManager.mc.player.rotationYawHead = yaw;
    }

    public void lookAtPos(BlockPos pos) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getPositionEyes(Util.mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public void lookAtVec3d(Vec3d vec3d) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getPositionEyes(Util.mc.getRenderPartialTicks()), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public void lookAtVec3d(double x, double y, double z) {
        Vec3d vec3d = new Vec3d(x, y, z);
        this.lookAtVec3d(vec3d);
    }

    public void lookAtEntity(Entity entity) {
        float[] angle = MathUtil.calcAngle(RotationManager.mc.player.getPositionEyes(Util.mc.getRenderPartialTicks()), entity.getPositionEyes(Util.mc.getRenderPartialTicks()));
        this.setPlayerRotations(angle[0], angle[1]);
    }

    public void setPlayerPitch(float pitch) {
        RotationManager.mc.player.rotationPitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public int getDirection4D() {
        return RotationUtil.getDirection4D();
    }

    public String getDirection4D(boolean northRed) {
        return RotationUtil.getDirection4D(northRed);
    }
}

