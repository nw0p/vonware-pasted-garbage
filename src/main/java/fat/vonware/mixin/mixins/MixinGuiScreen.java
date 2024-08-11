package fat.vonware.mixin.mixins;

import fat.vonware.features.modules.render.ShulkerViewer;
import fat.vonware.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreen.class)
public class MixinGuiScreen extends Gui {

    public Minecraft mc = Minecraft.getMinecraft();

    @Inject(method = "renderToolTip", at = @At(value = "HEAD"), cancellable = true)
    public void renderToolTip(ItemStack stack, int x, int y, CallbackInfo ci) {
        if (stack.getItem() instanceof ItemShulkerBox && ShulkerViewer.INSTANCE != null && ShulkerViewer.INSTANCE.isEnabled()) {
            ci.cancel();
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10) && tagCompound.getCompoundTag("BlockEntityTag").hasKey("Items", 9)) {
                NonNullList<ItemStack> itemList = NonNullList.withSize(27, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(tagCompound.getCompoundTag("BlockEntityTag"), itemList);
                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                RenderUtil.drawRect(x - 2.0f, y, x + 156.0f, y + 54.0f, ShulkerViewer.INSTANCE.getColor().hashCode());
                mc.fontRenderer.drawString(stack.getDisplayName(), x + 2.0f, y - mc.fontRenderer.FONT_HEIGHT - 2.0f, -1, true);
                GlStateManager.enableDepth();
                mc.getRenderItem().zLevel = 150.0F;
                RenderHelper.enableGUIStandardItemLighting();
                int iX = x;
                int iY = y + 2;
                int j = 0;
                for (ItemStack itemStack : itemList) {
                    mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, iX, iY);
                    mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, iX, iY, null);
                    iX += 17;
                    j++;
                    if (j >= 9) {
                        iY += 17;
                        iX = x;
                        j = 0;
                    }
                }
                RenderHelper.disableStandardItemLighting();
                mc.getRenderItem().zLevel = 0.0F;
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                GlStateManager.popMatrix();
            }
        }
    }
}
