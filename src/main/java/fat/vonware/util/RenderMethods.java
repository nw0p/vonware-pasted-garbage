package fat.vonware.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class RenderMethods implements Util
{
    public static Entity getEntity() {
        return (Entity)((RenderMethods.mc.getRenderViewEntity() == null) ? RenderMethods.mc.player : RenderMethods.mc.getRenderViewEntity());
    }

    public static void startRender() {
        GL11.glPushAttrib(1048575);
        GL11.glPushMatrix();
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glEnable(2884);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4353);
        GL11.glDisable(2896);
    }

    public static void endRender() {
        GL11.glEnable(2896);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3008);
        GL11.glDepthMask(true);
        GL11.glCullFace(1029);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public static void drawBox(final AxisAlignedBB bb) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        fillBox(bb);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawBox(final AxisAlignedBB bb, final Color color) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        color(color);
        fillBox(bb);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawOutline(final AxisAlignedBB bb, final float lineWidth) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        fillOutline(bb);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void drawCross(final AxisAlignedBB bb, final float lineWidth, final Color color) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        color(color);
        fillCross(bb);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void fillCross(final AxisAlignedBB bb) {
        if (bb != null) {
            GL11.glBegin(1);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
            GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            GL11.glEnd();
        }
    }

    public static void drawOutline(final AxisAlignedBB bb, final float lineWidth, final Color color) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(lineWidth);
        color(color);
        fillOutline(bb);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public static void fillOutline(final AxisAlignedBB bb) {
        if (bb != null) {
            GL11.glBegin(1);
            GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.minX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.minY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.minZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            GL11.glVertex3d(bb.maxX, bb.maxY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.maxZ);
            GL11.glVertex3d(bb.minX, bb.maxY, bb.minZ);
            GL11.glEnd();
        }
    }

    public static void fillBox(final AxisAlignedBB boundingBox) {
        if (boundingBox != null) {
            GL11.glBegin(7);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.minY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.minY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.minY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.minY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.minY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.minY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.minY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.minY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.minY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.minY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.minY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.minY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.minY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.minY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.minY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.minY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.maxY, (double)(float)boundingBox.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.minY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.minY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.minY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.minY, (double)(float)boundingBox.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.minY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.minY, (double)(float)boundingBox.minZ);
            GL11.glVertex3d((double)(float)boundingBox.minX, (double)(float)boundingBox.minY, (double)(float)boundingBox.maxZ);
            GL11.glVertex3d((double)(float)boundingBox.maxX, (double)(float)boundingBox.minY, (double)(float)boundingBox.maxZ);
            GL11.glEnd();
        }
    }

    public static void color(final Color color) {
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public static void color(final int color) {
        final float[] color4f = ColorUtil.toArray(color);
        GL11.glColor4f(color4f[0], color4f[1], color4f[2], color4f[3]);
    }

    public static void color(final float r, final float g, final float b, final float a) {
        GL11.glColor4f(r, g, b, a);
    }
}
