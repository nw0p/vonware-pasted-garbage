package fat.vonware.mixin.mixins;

import fat.vonware.features.modules.client.ConnectionInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ GuiConnecting.class })
public class MixinGuiConnecting extends GuiScreen
{
    @Inject(method = { "drawScreen(IIF)V" }, at = { @At("RETURN") })
    private void drawScreen(final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo ci) {
        if (ConnectionInfo.getInstance().isEnabled() && ConnectionInfo.getInstance().ServerIP.getValue()) {
            final String ConnectMessage = "Connecting to... [" + this.mc.getCurrentServerData().serverIP + "]";
            this.mc.fontRenderer.drawStringWithShadow(ConnectMessage, this.width / 2.0f - this.mc.fontRenderer.getStringWidth(ConnectMessage) / 2.0f, this.height / 2.0f - this.mc.fontRenderer.FONT_HEIGHT * 2, -1);

        }
    }
}