package fat.vonware.mixin.mixins;

import java.awt.Color;

import fat.vonware.event.events.RenderEntityModelEvent;
import fat.vonware.features.modules.client.ClickGui;
import fat.vonware.features.modules.render.CrystalChams;
import fat.vonware.features.modules.render.CrystalModifier;
import fat.vonware.util.ColorUtil;
import fat.vonware.util.EntityUtil;
import fat.vonware.util.RenderUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = {RenderEnderCrystal.class})
public class MixinRenderEnderCrystal {
    @Shadow
    @Final
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES;
    private static ResourceLocation glint;

    @Redirect(method = {"doRender"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    public void renderModelBaseHook(ModelBase model, Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        if (CrystalChams.INSTANCE.isEnabled()) {
            GlStateManager.scale((float) CrystalChams.INSTANCE.scale.getValue().floatValue(), (float) CrystalChams.INSTANCE.scale.getValue().floatValue(), (float) CrystalChams.INSTANCE.scale.getValue().floatValue());
        }
        if (CrystalChams.INSTANCE.isEnabled() && CrystalChams.INSTANCE.wireframe.getValue().booleanValue()) {
            RenderEntityModelEvent event;
            if (CrystalModifier.INSTANCE.isEnabled()) {
                event = new RenderEntityModelEvent(0, model, entity, limbSwing,  limbSwingAmount * CrystalModifier.INSTANCE.spin.getValue(), ageInTicks * CrystalModifier.INSTANCE.bounce.getValue(), netHeadYaw, headPitch, scale);
            } else {
                event = new RenderEntityModelEvent(0, model, entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }

            CrystalChams.INSTANCE.onRenderModel(event);
        }
        if (CrystalChams.INSTANCE.isEnabled() && CrystalChams.INSTANCE.chams.getValue().booleanValue()) {
            GL11.glPushAttrib((int) 1048575);
            GL11.glDisable((int) 3008);
            GL11.glDisable((int) 3553);
            GL11.glDisable((int) 2896);
            GL11.glEnable((int) 3042);
            GL11.glBlendFunc((int) 770, (int) 771);
            GL11.glLineWidth((float) 1.5f);
            GL11.glEnable((int) 2960);
            if (CrystalChams.INSTANCE.rainbow.getValue().booleanValue()) {
                Color rainbowColor1 = CrystalChams.INSTANCE.rainbow.getValue() != false ? ColorUtil.rainbow((int) ClickGui.getInstance().rainbowHue.getValue()) : new Color(RenderUtil.getRainbow(200 * 100, 0, 100.0f, 100.0f));
                Color rainbowColor = EntityUtil.getColor(entity, rainbowColor1.getRed(), rainbowColor1.getGreen(), rainbowColor1.getBlue(), CrystalChams.INSTANCE.alpha.getValue(), true);
                if (CrystalChams.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glDisable((int) 2929);
                    GL11.glDepthMask((boolean) false);
                }
                GL11.glEnable((int) 10754);
                GL11.glColor4f((float) ((float) rainbowColor.getRed() / 255.0f), (float) ((float) rainbowColor.getGreen() / 255.0f), (float) ((float) rainbowColor.getBlue() / 255.0f), (float) ((float) CrystalChams.INSTANCE.alpha.getValue().intValue() / 255.0f));
                if (CrystalModifier.INSTANCE.isEnabled()) {
                    GlStateManager.scale(CrystalModifier.INSTANCE.scale.getValue(), CrystalModifier.INSTANCE.scale.getValue(), CrystalModifier.INSTANCE.scale.getValue());
                    model.render(entity, limbSwing, limbSwingAmount * CrystalModifier.INSTANCE.spin.getValue(), ageInTicks * CrystalModifier.INSTANCE.bounce.getValue(), netHeadYaw, headPitch, scale);
                } else {
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                }

                if (CrystalChams.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glEnable((int) 2929);
                    GL11.glDepthMask((boolean) true);
                }
            } else if (CrystalChams.INSTANCE.xqz.getValue().booleanValue() && CrystalChams.INSTANCE.throughWalls.getValue().booleanValue()) {
                Color visibleColor;
                Color hiddenColor = CrystalChams.INSTANCE.rainbow.getValue() != false ? EntityUtil.getColor(entity, CrystalChams.INSTANCE.hiddenRed.getValue(), CrystalChams.INSTANCE.hiddenGreen.getValue(), CrystalChams.INSTANCE.hiddenBlue.getValue(), CrystalChams.INSTANCE.hiddenAlpha.getValue(), true) : EntityUtil.getColor(entity, CrystalChams.INSTANCE.hiddenRed.getValue(), CrystalChams.INSTANCE.hiddenGreen.getValue(), CrystalChams.INSTANCE.hiddenBlue.getValue(), CrystalChams.INSTANCE.hiddenAlpha.getValue(), true);
                Color color = visibleColor = CrystalChams.INSTANCE.rainbow.getValue() != false ? EntityUtil.getColor(entity, CrystalChams.INSTANCE.red.getValue(), CrystalChams.INSTANCE.green.getValue(), CrystalChams.INSTANCE.blue.getValue(), CrystalChams.INSTANCE.alpha.getValue(), true) : EntityUtil.getColor(entity, CrystalChams.INSTANCE.red.getValue(), CrystalChams.INSTANCE.green.getValue(), CrystalChams.INSTANCE.blue.getValue(), CrystalChams.INSTANCE.alpha.getValue(), true);
                if (CrystalChams.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glDisable((int) 2929);
                    GL11.glDepthMask((boolean) false);
                }
                GL11.glEnable((int) 10754);
                GL11.glColor4f((float) ((float) hiddenColor.getRed() / 255.0f), (float) ((float) hiddenColor.getGreen() / 255.0f), (float) ((float) hiddenColor.getBlue() / 255.0f), (float) ((float) CrystalChams.INSTANCE.alpha.getValue().intValue() / 255.0f));
                if (CrystalModifier.INSTANCE.isEnabled()) {
                    GlStateManager.scale(CrystalModifier.INSTANCE.scale.getValue(), CrystalModifier.INSTANCE.scale.getValue(), CrystalModifier.INSTANCE.scale.getValue());
                    model.render(entity, limbSwing, limbSwingAmount * CrystalModifier.INSTANCE.spin.getValue(), ageInTicks * CrystalModifier.INSTANCE.bounce.getValue(), netHeadYaw, headPitch, scale);
                } else {
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                }

                if (CrystalChams.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glEnable((int) 2929);
                    GL11.glDepthMask((boolean) true);
                }
                GL11.glColor4f((float) ((float) visibleColor.getRed() / 255.0f), (float) ((float) visibleColor.getGreen() / 255.0f), (float) ((float) visibleColor.getBlue() / 255.0f), (float) ((float) CrystalChams.INSTANCE.alpha.getValue().intValue() / 255.0f));
                if (CrystalModifier.INSTANCE.isEnabled()) {
                    GlStateManager.scale(CrystalModifier.INSTANCE.scale.getValue(), CrystalModifier.INSTANCE.scale.getValue(), CrystalModifier.INSTANCE.scale.getValue());
                    model.render(entity, limbSwing, limbSwingAmount * CrystalModifier.INSTANCE.spin.getValue(), ageInTicks * CrystalModifier.INSTANCE.bounce.getValue(), netHeadYaw, headPitch, scale);
                } else {
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                }

            } else {
                Color visibleColor;
                Color color = visibleColor = CrystalChams.INSTANCE.rainbow.getValue() != false ? ColorUtil.rainbow((int) ClickGui.getInstance().rainbowHue.getValue()) : EntityUtil.getColor(entity, CrystalChams.INSTANCE.red.getValue(), CrystalChams.INSTANCE.green.getValue(), CrystalChams.INSTANCE.blue.getValue(), CrystalChams.INSTANCE.alpha.getValue(), true);
                if (CrystalChams.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glDisable((int) 2929);
                    GL11.glDepthMask((boolean) false);
                }
                GL11.glEnable((int) 10754);
                GL11.glColor4f((float) ((float) visibleColor.getRed() / 255.0f), (float) ((float) visibleColor.getGreen() / 255.0f), (float) ((float) visibleColor.getBlue() / 255.0f), (float) ((float) CrystalChams.INSTANCE.alpha.getValue().intValue() / 255.0f));
                if (CrystalModifier.INSTANCE.isEnabled()) {
                    GlStateManager.scale(CrystalModifier.INSTANCE.scale.getValue(), CrystalModifier.INSTANCE.scale.getValue(), CrystalModifier.INSTANCE.scale.getValue());
                    model.render(entity, limbSwing, limbSwingAmount * CrystalModifier.INSTANCE.spin.getValue(), ageInTicks * CrystalModifier.INSTANCE.bounce.getValue(), netHeadYaw, headPitch, scale);
                } else {
                    model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                }

                if (CrystalChams.INSTANCE.throughWalls.getValue().booleanValue()) {
                    GL11.glEnable((int) 2929);
                    GL11.glDepthMask((boolean) true);
                }
            }
            GL11.glEnable((int) 3042);
            GL11.glEnable((int) 2896);
            GL11.glEnable((int) 3553);
            GL11.glEnable((int) 3008);
            GL11.glPopAttrib();
        } else {
            if (CrystalModifier.INSTANCE.isEnabled()) {
                GlStateManager.scale(CrystalModifier.INSTANCE.scale.getValue(), CrystalModifier.INSTANCE.scale.getValue(), CrystalModifier.INSTANCE.scale.getValue());
                model.render(entity, limbSwing, limbSwingAmount * CrystalModifier.INSTANCE.spin.getValue(), ageInTicks * CrystalModifier.INSTANCE.bounce.getValue(), netHeadYaw, headPitch, scale);
            } else {
                model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            }

        }
        if (CrystalChams.INSTANCE.isEnabled()) {
            GlStateManager.scale((float) CrystalChams.INSTANCE.scale.getValue().floatValue(), (float) CrystalChams.INSTANCE.scale.getValue().floatValue(), (float) CrystalChams.INSTANCE.scale.getValue().floatValue());
        }
    }

    static {
        glint = new ResourceLocation("minecraft/textures/enchanted_item_glint.png");
    }
}