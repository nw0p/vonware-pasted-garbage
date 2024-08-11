package fat.vonware.features.gui.components.items.buttons;

import fat.vonware.Vonware;
import fat.vonware.features.gui.VonwareGui;
import fat.vonware.features.gui.components.Component;
import fat.vonware.features.gui.components.items.Item;
import fat.vonware.features.modules.client.ClickGui;
import fat.vonware.util.RenderUtil;
import fat.vonware.util.Util;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;

public class Button
        extends Item {
    private boolean state;

    public Button(String name) {
        super(name);
        this.height = 15;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean hovering = this.isHovering(mouseX, mouseY);
        RenderUtil.drawRect(this.x, this.y, this.x + (float) this.width, this.y + (float) this.height - 0.5f,
                this.getState() ? (!hovering ?
                        Vonware.colorManager.getColorWithAlpha(Vonware.moduleManager.getModuleByClass(ClickGui.class).hoverAlpha.getValue()) :
                        Vonware.colorManager.getColorWithAlpha(Vonware.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue()))
                        : (!hovering ?  Vonware.colorManager.getColorWithAlphaDark(Vonware.moduleManager.getModuleByClass(ClickGui.class).alpha.getValue()) : -2007673515));
        Vonware.textManager.drawStringWithShadow(this.getName(), this.x + 2.3f, this.y - 2.0f - (float) VonwareGui.getClickGui().getTextOffset(), -1 ); /*, this.getState() ? -1 : -5592406);*/ //color

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.onMouseClick();
        }
    }

    public void onMouseClick() {
        this.state = !this.state;
        this.toggle();
        Util.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    public void toggle() {
    }

    public boolean getState() {
        return this.state;
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        for (Component component : VonwareGui.getClickGui().getComponents()) {
            if (!component.drag) continue;
            return false;
        }
        return (float) mouseX >= this.getX() && (float) mouseX <= this.getX() + (float) this.getWidth() && (float) mouseY >= this.getY() && (float) mouseY <= this.getY() + (float) this.height;
    }
}

