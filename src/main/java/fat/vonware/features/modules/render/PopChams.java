package fat.vonware.features.modules.render;

import fat.vonware.Vonware;
import fat.vonware.event.events.TotemPopEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;

public class PopChams extends Module {

    public PopChams() {
        super("PopChams", "chams but pop", Category.RENDER, true, false, false);
    }

    public final Setting<Integer> fadeTime = register(new Setting("Fade-Time", 1500, 0, 5000));
    public final Setting<Boolean> selfPop = register(new Setting("Self-Pop", false));
    public final Setting<Integer> Red = this.register(new Setting("Red", 255, 0, 255));
    public final Setting<Integer> Green = this.register(new Setting("Green", 255, 0, 255));
    public final Setting<Integer> Blue = this.register(new Setting("Blue", 255, 0, 255));
    public final Setting<Integer> Alpha = this.register(new Setting("Alpha", 70, 0, 255));
    public final Setting<Integer> OLRed = this.register(new Setting("OL-Red", 255, 0, 255));
    public final Setting<Integer> OLGreen = this.register(new Setting("OL-Green", 255, 0, 255));
    public final Setting<Integer> OLBlue = this.register(new Setting("OL-Blue", 255, 0, 255));
    public final Setting<Integer> OLAlpha = this.register(new Setting("OL-Alpha", 190, 0, 255));
    public final Setting<Double> yAnimations = register(new Setting<>("Y-Animation", 0., -7., 7.));
    public final Setting<Boolean> friendPop = register(new Setting("Friend-Pop", false));
    public List<PopData> popDataList = new ArrayList<>();

    @SubscribeEvent
    public void onTotemPop(TotemPopEvent event) {
        popDataList.add(new PopChams.PopData(event.getEntity(), System.currentTimeMillis(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, event.getEntity() instanceof AbstractClientPlayer && ((AbstractClientPlayer) event.getEntity()).getSkinType().equals("slim") ));
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        try {
            for (PopChams.PopData data : popDataList) {
                EntityPlayer player = data.getPlayer();
                ModelPlayer model = data.getModel();
                double x = data.getX() - mc.getRenderManager().viewerPosX;
                double y = data.getY() - mc.getRenderManager().viewerPosY;
                y += yAnimations.getValue() * (System.currentTimeMillis() - data.getTime()) / fadeTime.getValue().doubleValue();
                double z = data.getZ() - mc.getRenderManager().viewerPosZ;

                GlStateManager.pushMatrix();
                glPushAttrib(GL_ALL_ATTRIB_BITS);
                glPushMatrix();
                glDisable(GL_ALPHA_TEST);
                glEnable(GL_BLEND);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                glDisable(GL_TEXTURE_2D);
                glDisable(GL_DEPTH_TEST);
                glDepthMask(false);
                glEnable(GL_CULL_FACE);
                glEnable(GL_LINE_SMOOTH);
                glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                glDisable(GL_LIGHTING);

                GlStateManager.translate(x, y, z);
                GlStateManager.rotate(180 - data.yaw, 0, 1, 0);

                final Color boxColor = getColor();
                final Color outlineColor = getOutlineColor();
                final float maxBoxAlpha = boxColor.getAlpha();
                final float maxOutlineAlpha = outlineColor.getAlpha();
                final float alphaBoxAmount = maxBoxAlpha / fadeTime.getValue();
                final float alphaOutlineAmount = maxOutlineAlpha / fadeTime.getValue();
                final int fadeBoxAlpha = MathHelper.clamp((int) (alphaBoxAmount * (data.getTime() + fadeTime.getValue() - System.currentTimeMillis())), 0, (int) maxBoxAlpha);
                final int fadeOutlineAlpha = MathHelper.clamp((int) (alphaOutlineAmount * (data.getTime() + fadeTime.getValue() - System.currentTimeMillis())), 0, (int) maxOutlineAlpha);

                Color box = new Color(boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue(), fadeBoxAlpha);
                Color out = new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), fadeOutlineAlpha);

                GlStateManager.enableRescaleNormal();
                GlStateManager.scale(-1.0F, -1.0F, 1.0F);
                double widthX = player.getEntityBoundingBox().maxX - player.getRenderBoundingBox().minX + 1;
                double widthZ = player.getEntityBoundingBox().maxZ - player.getEntityBoundingBox().minZ + 1;

                GlStateManager.scale(widthX, player.height, widthZ);

                GlStateManager.translate(0.0F, -1.501F, 0.0F);

                color(box);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                model.render(data.player, data.limbSwing, data.limbSwingAmount, data.ticksExisted, data.yawHead, data.pitch, 0.0625f);

                color(out);
                GL11.glLineWidth(2.0f);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                model.render(data.player, data.limbSwing, data.limbSwingAmount, data.ticksExisted, data.yawHead, data.pitch, 0.0625f);

                glEnable(GL_LIGHTING);
                glDisable(GL_LINE_SMOOTH);
                glEnable(GL_TEXTURE_2D);
                glEnable(GL_DEPTH_TEST);
                glDisable(GL_BLEND);
                glEnable(GL_ALPHA_TEST);
                glDepthMask(true);
                glCullFace(GL_BACK);
                glPopMatrix();
                glPopAttrib();
                GlStateManager.popMatrix();

            }
            popDataList.removeIf(e -> e.getTime() + fadeTime.getValue() < System.currentTimeMillis());
        } catch (Exception ignored) {
            
        }
    }

    public static void color(Color color)
    {
        glColor4f(color.getRed() / 255.0f,
                color.getGreen() / 255.0f,
                color.getBlue() / 255.0f,
                color.getAlpha() / 255.0f);
    }

    public Color getColor() {
        return new Color(Red.getValue(), Green.getValue(), Blue.getValue(), Alpha.getValue());
    }

    public Color getOutlineColor() {
        return new Color(OLRed.getValue(), OLGreen.getValue(), OLBlue.getValue(), OLAlpha.getValue());
    }

    public boolean isValidEntity(EntityPlayer entity) {
        return !(entity == mc.player && !selfPop.getValue()) && !((Vonware.friendManager.isFriend(entity) && entity != mc.player) && !friendPop.getValue());
    }

    public static class PopData {
        public EntityPlayer player;
        public ModelPlayer model;
        public long time;
        public float limbSwing, yaw, limbSwingAmount, yawHead, pitch;
        public int ticksExisted;
        public double x;
        public double y;
        public double z;

        public PopData(EntityPlayer player, long time, double x, double y, double z, boolean slim) {
            this.player = player;
            this.time = time;
            this.x = x;
            this.y = y - (player.isSneaking() ? 0.125 : 0);
            this.z = z;
            this.model = new ModelPlayer(0.0f, ((AbstractClientPlayer) player).getSkinType().equalsIgnoreCase("slim"));
            model.bipedBodyWear.showModel = false;
            model.bipedHeadwear.showModel = true;
            model.bipedHead.showModel = false;
            model.bipedRightArmwear.showModel = false;
            model.bipedRightLegwear.showModel = false;
            model.bipedLeftArmwear.showModel = false;
            model.bipedLeftLegwear.showModel = false;
            limbSwing = player.limbSwing;
            limbSwingAmount = player.limbSwingAmount;
            ticksExisted = player.ticksExisted;
            yawHead = player.rotationYawHead;
            pitch = player.rotationPitch;
            yaw = player.rotationYaw;
        }

        public EntityPlayer getPlayer() {
            return player;
        }

        public long getTime() {
            return time;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }

        public ModelPlayer getModel() {
            return model;
        }
    }
}
