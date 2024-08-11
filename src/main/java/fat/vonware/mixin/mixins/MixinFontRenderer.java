package fat.vonware.mixin.mixins;

import fat.vonware.features.modules.misc.NameHider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FontRenderer.class)
public abstract class MixinFontRenderer {

    @Shadow
    protected abstract void renderStringAtPos(String var1, boolean var2);

    @Redirect(method = {"renderString(Ljava/lang/String;FFIZ)I"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;renderStringAtPos(Ljava/lang/String;Z)V"))
    public void renderStringAtPosHook(FontRenderer renderer, String text, boolean shadow) {
        this.renderStringAtPos(NameHider.INSTANCE != null && NameHider.INSTANCE.isEnabled() ? text.replace(Minecraft.getMinecraft().getSession().getUsername(), NameHider.INSTANCE.newName.getValue()) : text, shadow);
    }
}
