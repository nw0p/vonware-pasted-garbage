package fat.vonware.util;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Interpolation implements Util
{
    public static Vec3d interpolatedEyePos() {
        return mc.player.getPositionEyes(mc.getRenderPartialTicks());
    }

    public static Vec3d interpolatedEyeVec() {
        return mc.player.getLook(mc.getRenderPartialTicks());
    }

    public static Vec3d interpolatedEyeVec(final EntityPlayer player) {
        return player.getLook(mc.getRenderPartialTicks());
    }


    public static Vec3d interpolateEntity(final Entity entity) {
        final double x = interpolateLastTickPos(entity.posX, entity.lastTickPosX) - getRenderPosX();
        final double y = interpolateLastTickPos(entity.posY, entity.lastTickPosY) - getRenderPosY();
        final double z = interpolateLastTickPos(entity.posZ, entity.lastTickPosZ) - getRenderPosZ();
        return new Vec3d(x, y, z);
    }

    public static double interpolateLastTickPos(final double pos, final double lastPos) {
        return lastPos + (pos - lastPos) * mc.timer.renderPartialTicks;
    }

    public static AxisAlignedBB interpolatePos(final BlockPos pos) {
        return interpolatePos(pos, 1.0f);
    }

    public static AxisAlignedBB interpolatePos(final BlockPos pos, final float height) {
        return new AxisAlignedBB(pos.getX() - mc.getRenderManager().viewerPosX, pos.getY() - mc.getRenderManager().viewerPosY, pos.getZ() - mc.getRenderManager().viewerPosZ, pos.getX() - mc.getRenderManager().viewerPosX + 1.0, pos.getY() - mc.getRenderManager().viewerPosY + height, pos.getZ() - mc.getRenderManager().viewerPosZ + 1.0);
    }

    public static AxisAlignedBB interpolateAxis(final AxisAlignedBB bb) {
        return new AxisAlignedBB(bb.minX - mc.getRenderManager().viewerPosX, bb.minY - mc.getRenderManager().viewerPosY, bb.minZ - mc.getRenderManager().viewerPosZ, bb.maxX - mc.getRenderManager().viewerPosX, bb.maxY - mc.getRenderManager().viewerPosY, bb.maxZ - mc.getRenderManager().viewerPosZ);
    }

    public static AxisAlignedBB offsetRenderPos(final AxisAlignedBB bb) {
        return bb.offset(-getRenderPosX(), -getRenderPosY(), -getRenderPosZ());
    }

    public static double getRenderPosX() {
        return mc.getRenderManager().renderPosX;
    }

    public static double getRenderPosY() {
        return mc.getRenderManager().renderPosY;
    }

    public static double getRenderPosZ() {
        return mc.getRenderManager().renderPosZ;
    }

    public static Frustum createFrustum(final Entity entity) {
        final Frustum frustum = new Frustum();
        setFrustum(frustum, entity);
        return frustum;
    }

    public static void setFrustum(final Frustum frustum, final Entity entity) {
        final double x = interpolateLastTickPos(entity.posX, entity.lastTickPosX);
        final double y = interpolateLastTickPos(entity.posY, entity.lastTickPosY);
        final double z = interpolateLastTickPos(entity.posZ, entity.lastTickPosZ);
        frustum.setPosition(x, y, z);
    }
}
