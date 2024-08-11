package fat.vonware.mixin.mixins;

import fat.vonware.Vonware;
import fat.vonware.features.modules.render.Ambience;
import fat.vonware.features.modules.render.Nametags;
import fat.vonware.features.modules.render.NoRender;
import fat.vonware.features.modules.render.Weather;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.vecmath.Vector3f;
import java.awt.*;

@Mixin(value = {EntityRenderer.class})
public class MixinEntityRenderer {

    public Minecraft mc = Minecraft.getMinecraft();

    @Shadow
    private int[] lightmapColors;

    private int[] toRGBAArray(int colorBuffer) {
        return new int[]{colorBuffer >> 16 & 0xFF, colorBuffer >> 8 & 0xFF, colorBuffer & 0xFF};
    }

    private Vector3f mix(Vector3f first, Vector3f second, float factor) {
        return new Vector3f(first.x * (1.0f - factor) + second.x * factor, first.y * (1.0f - factor) + second.y * factor, first.z * (1.0f - factor) + first.z * factor);
    }
    @Inject(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;renderRainSnow(F)V"))
    public void weatherHook(int pass, float partialTicks, long finishTimeNano, CallbackInfo ci) {
        if (Vonware.moduleManager.getModuleByClass(Weather.class).isOn()) {
            Vonware.moduleManager.getModuleByClass(Weather.class).render(partialTicks);
        }
    }
    @Inject(method = { "drawNameplate" }, at = { @At("HEAD") }, cancellable = true)
    private static void renderName(final FontRenderer fontRendererIn, final String str, final float x, final float y, final float z, final int verticalShift, final float viewerYaw, final float viewerPitch, final boolean isThirdPersonFrontal, final boolean isSneaking, final CallbackInfo ci) {
        if (Vonware.moduleManager.getModuleByClass(Nametags.class).isEnabled()) {
            ci.cancel();
        }
    }
    @Inject(method={"setupFog"}, at={@At(value="HEAD")}, cancellable=true)
    public void setupFogHook(int startCoords, float partialTicks, CallbackInfo info) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fog.getValue() == NoRender.Fog.NOFOG) {
            info.cancel();
        }
    }

    @Redirect(method={"setupFog"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/ActiveRenderInfo;getBlockStateAtEntityViewpoint(Lnet/minecraft/world/World;Lnet/minecraft/entity/Entity;F)Lnet/minecraft/block/state/IBlockState;"))
    public IBlockState getBlockStateAtEntityViewpointHook(World worldIn, Entity entityIn, float p_186703_2_) {
        if (NoRender.getInstance().isOn() && NoRender.getInstance().fog.getValue() == NoRender.Fog.AIR) {
            return Blocks.AIR.defaultBlockState;
        }
        return ActiveRenderInfo.getBlockStateAtEntityViewpoint((World)worldIn, (Entity)entityIn, (float)p_186703_2_);
    }

    @Inject(method = "updateLightmap", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/DynamicTexture;updateDynamicTexture()V", shift = At.Shift.BEFORE))
    private void updateTextureHook(float partialTicks, CallbackInfo ci) {
        try {
            Ambience ambience = Vonware.moduleManager.getModuleByClass(Ambience.class);
            if (ambience.isEnabled()) {
                for (int i = 0; i < this.lightmapColors.length; ++i) {
                    Color ambientColor = new Color(ambience.red.getValue(), ambience.green.getValue(), ambience.blue.getValue(), ambience.alpha.getValue());
                    int alpha = ambientColor.getAlpha();
                    float modifier = (float) alpha / 255.0f;
                    int color = this.lightmapColors[i];
                    int[] bgr = toRGBAArray(color);
                    Vector3f values = new Vector3f((float) bgr[2] / 255.0f, (float) bgr[1] / 255.0f, (float) bgr[0] / 255.0f);
                    Vector3f newValues = new Vector3f((float) ambientColor.getRed() / 255.0f, (float) ambientColor.getGreen() / 255.0f, (float) ambientColor.getBlue() / 255.0f);
                    Vector3f finalValues = mix(values, newValues, modifier);
                    int red = (int) (finalValues.x * 255.0f);
                    int green = (int) (finalValues.y * 255.0f);
                    int blue = (int) (finalValues.z * 255.0f);
                    this.lightmapColors[i] = 0xFF000000 | red << 16 | green << 8 | blue;
                }
            }
        } catch (Exception ignored) {

        }
    }

}
