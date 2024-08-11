package fat.vonware.features.modules.render;

import fat.vonware.event.events.Render3DEvent;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.EntityUtil;
import fat.vonware.util.RenderUtil;
import fat.vonware.util.Util;
import fat.vonware.features.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ESP
        extends Module {
    private static ESP INSTANCE = new ESP();
    private final Setting<Boolean> items = this.register(new Setting<Boolean>("Items", true));
    private final Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", false));
    private final Setting<Boolean> xporbs = this.register(new Setting<Boolean>("XpOrbs", false));
    private final Setting<Boolean> xpbottles = this.register(new Setting<Boolean>("XpBottles", false));
    private final Setting<Boolean> pearl = this.register(new Setting<Boolean>("Pearls", false));
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    private final Setting<Integer> boxAlpha = this.register(new Setting<Integer>("BoxAlpha", 120, 0, 255));
    private final Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 255, 0, 255));

    public ESP() {
        super("ESP", "Renders a nice ESP.", Module.Category.RENDER, false, false, false);
        this.setInstance();
    }
    @Override
    public void onUpdate() {
        if (this.rainbow.getValue().booleanValue()) {
            this.doRainbow();
        }
    }
    public void doRainbow() {
        float[] tick_color = new float[]{(float)(System.currentTimeMillis() % 11520L) / 11520.0f};
        int color_rgb_o = Color.HSBtoRGB(tick_color[0], 0.8f, 1.0f);
        this.red.setValue(color_rgb_o >> 16 & 0xFF);
        this.green.setValue(color_rgb_o >> 8 & 0xFF);
        this.blue.setValue(color_rgb_o & 0xFF);
    }

    public static ESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ESP();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        AxisAlignedBB bb;
        Vec3d interp;
        int i;
        if (this.items.getValue().booleanValue()) {
            i = 0;
            for (Entity entity : ESP.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityItem) || !(ESP.mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, Util.mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                GL11.glLineWidth(1.0f);
                RenderGlobal.renderFilledBox(bb, (float) this.red.getValue().intValue() / 255.0f, (float) this.green.getValue().intValue() / 255.0f, (float) this.blue.getValue().intValue() / 255.0f, (float) this.boxAlpha.getValue().intValue() / 255.0f);
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.0f);
                if (++i < 50) continue;
                break;
            }
        }
        if (this.xporbs.getValue().booleanValue()) {
            i = 0;
            for (Entity entity : ESP.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityXPOrb) || !(ESP.mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, Util.mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                GL11.glLineWidth(1.0f);
                RenderGlobal.renderFilledBox(bb, (float) this.red.getValue().intValue() / 255.0f, (float) this.green.getValue().intValue() / 255.0f, (float) this.blue.getValue().intValue() / 255.0f, (float) this.boxAlpha.getValue().intValue() / 255.0f);
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.0f);
                if (++i < 50) continue;
                break;
            }
        }
        if (this.pearl.getValue().booleanValue()) {
            i = 0;
            for (Entity entity : ESP.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityEnderPearl) || !(ESP.mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, Util.mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                GL11.glLineWidth(1.0f);
                RenderGlobal.renderFilledBox(bb, (float) this.red.getValue().intValue() / 255.0f, (float) this.green.getValue().intValue() / 255.0f, (float) this.blue.getValue().intValue() / 255.0f, (float) this.boxAlpha.getValue().intValue() / 255.0f);
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.0f);
                if (++i < 50) continue;
                break;
            }
        }
        if (this.xpbottles.getValue().booleanValue()) {
            i = 0;
            for (Entity entity : ESP.mc.world.loadedEntityList) {
                if (!(entity instanceof EntityExpBottle) || !(ESP.mc.player.getDistanceSq(entity) < 2500.0)) continue;
                interp = EntityUtil.getInterpolatedRenderPos(entity, Util.mc.getRenderPartialTicks());
                bb = new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().minY - 0.0 - entity.posY + interp.y, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ + interp.z, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX + interp.x, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY + interp.y, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ + interp.z);
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableDepth();
                GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
                GlStateManager.disableTexture2D();
                GlStateManager.depthMask(false);
                GL11.glEnable(2848);
                GL11.glHint(3154, 4354);
                GL11.glLineWidth(1.0f);
                RenderGlobal.renderFilledBox(bb, (float) this.red.getValue().intValue() / 255.0f, (float) this.green.getValue().intValue() / 255.0f, (float) this.blue.getValue().intValue() / 255.0f, (float) this.boxAlpha.getValue().intValue() / 255.0f);
                GL11.glDisable(2848);
                GlStateManager.depthMask(true);
                GlStateManager.enableDepth();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                RenderUtil.drawBlockOutline(bb, new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), 1.0f);
                if (++i < 50) continue;
                break;
            }
        }
    }
}
