package fat.vonware.mixin.mixins;

import fat.vonware.util.PlayerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer {

    public Minecraft mc = Minecraft.getMinecraft();

    @Shadow
    public abstract boolean checkHotbarKeys(int keyCode);

    @Shadow
    public Slot hoveredSlot;

    @Shadow
    public Container inventorySlots;

    @Overwrite
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        try {
            if (slotIn != null) {
                slotId = slotIn.slotNumber;
            }
            this.mc.playerController.windowClick(this.inventorySlots.windowId, slotId, mouseButton, type, this.mc.player);
        } catch (Exception ignored) {

        }
    }

    @Overwrite
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        try {
            if (keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(keyCode)) {
                this.mc.player.closeScreen();
            }
            this.checkHotbarKeys(keyCode);
            if (this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
                if (this.mc.gameSettings.keyBindPickBlock.isActiveAndMatches(keyCode)) {
                    this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 0, ClickType.CLONE);
                } else if (this.mc.gameSettings.keyBindDrop.isActiveAndMatches(keyCode)) {
                    this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, PlayerUtil.isCtrlKeyDown() ? 1 : 0, ClickType.THROW);
                }
            }
        } catch (Exception ignored) {

        }
    }
}
