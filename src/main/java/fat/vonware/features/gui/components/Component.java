package fat.vonware.features.gui.components;

import fat.vonware.Vonware;
import fat.vonware.features.Feature;
import fat.vonware.features.gui.VonwareGui;
import fat.vonware.features.gui.components.items.Item;
import fat.vonware.features.gui.components.items.buttons.Button;
import fat.vonware.features.modules.client.ClickGui;
import fat.vonware.util.ColorUtil;
import fat.vonware.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Component
        extends Feature {
    public static int[] counter1 = new int[]{1};
    private final ArrayList<Item> items = new ArrayList();
    public boolean drag;
    private int x;
    private int y;
    private int x2;
    private int y2;
    private int width;
    private int height;
    private boolean open;
    private boolean hidden = false;
    private int angle;
    private final Minecraft minecraft;

    public Component(String name, int x, int y, boolean open) {
        super(name);
        this.x = x;
        this.y = y;
        this.minecraft = Minecraft.getMinecraft();
        this.width = 98;
        this.height = 18;
        this.open = open;
        this.setupItems();
    }

    public void setupItems() {
    }

    private void drag(int mouseX, int mouseY) {
        if (!this.drag) {
            return;
        }
        this.x = this.x2 + mouseX;
        this.y = this.y2 + mouseY;
    }
    public static float calculateRotation(float var0) {
        if ((var0 %= 360.0f) >= 180.0f) {
            var0 -= 360.0f;
        }
        if (var0 < -180.0f) {
            var0 += 360.0f;
        }
        return var0;
    }
    public static void drawModalRect(final int var0, final int var1, final float var2, final float var3, final int var4, final int var5, final int var6, final int var7, final float var8, final float var9) {
        Gui.drawScaledCustomSizeModalRect(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9);
    }
    public static void glColor(final Color color) {
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, color.getAlpha() / 255.0f);
    }

    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drag(mouseX, mouseY);
        Component.counter1 = new int[] { 1 };
        final float totalItemHeight = this.open ? (this.getTotalItemHeight() - 2.0f) : 0.0f;
        Gui.drawRect(this.x, this.y - 1, this.x + this.width, this.y + this.height - 6, ClickGui.getInstance().getGuiColor().getRGB());
        if (this.open) {
            RenderUtil.drawRect((float)this.x, this.y + 12.5f, (float)(this.x + this.width), this.y + this.height + totalItemHeight, ClickGui.getInstance().getGuiBackgroundColor().getRGB());
        }
        Vonware.textManager.drawStringWithShadow(this.getName(), this.x + 3.0f, this.y - 4.0f - VonwareGui.getClickGui().getTextOffset(), -1);
        //if (!this.open) {
        //    if (this.angle > 0) {
        //        this.angle -= 6;
        //    }
        //}
        //else if (this.angle < 180) {
        //    this.angle += 6;
        //}
        //GlStateManager.pushMatrix();
        //GlStateManager.enableBlend();
        //glColor(new Color(255, 255, 255, 255));
        //this.minecraft.getTextureManager().bindTexture(new ResourceLocation("textures/arrow.png"));
        //GlStateManager.translate((float)(this.getX() + this.getWidth() - 7), this.getY() + 6 - 0.3f, 0.0f);
        //GlStateManager.rotate(calculateRotation((float)this.angle), 0.0f, 0.0f, 1.0f);
        //drawModalRect(-5, -5, 0.0f, 0.0f, 10, 10, 10, 10, 10.0f, 10.0f);
        //GlStateManager.disableBlend();
        //GlStateManager.popMatrix();
        if (this.open) {
            float y = this.getY() + this.getHeight() - 3.0f;
            for (final Item item : this.getItems()) {
                ++Component.counter1[0];
                if (item.isHidden()) {
                    continue;
                }
                item.setLocation(this.x + 2.0f, y);
                item.setWidth(this.getWidth() - 4);
                item.drawScreen(mouseX, mouseY, partialTicks);
                y += item.getHeight() + 1.5f;
            }
        }
        //if (this.open && ClickGui.getInstance().outline.getValue()) {
        //    GlStateManager.disableTexture2D();
        //    GlStateManager.enableBlend();
        //    GlStateManager.disableAlpha();
        //    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        //    GlStateManager.shadeModel(7425);
        //    GL11.glBegin(2);
        //    GL11.glColor4f(ClickGui.getInstance().red.getValue() / 255.0f, ClickGui.getInstance().green.getValue() / 255.0f, ClickGui.getInstance().blue.getValue() / 255.0f, 255.0f);
        //    GL11.glVertex3f((float)this.x, this.y - 0.5f, 0.0f);
        //    GL11.glVertex3f((float)(this.x + this.width), this.y - 0.5f, 0.0f);
        //    GL11.glVertex3f((float)(this.x + this.width), this.y + this.height + totalItemHeight, 0.0f);
        //    GL11.glVertex3f((float)this.x, this.y + this.height + totalItemHeight, 0.0f);
        //    GL11.glEnd();
        //    GlStateManager.shadeModel(7424);
        //    GlStateManager.disableBlend();
        //    GlStateManager.enableAlpha();
        //    GlStateManager.enableTexture2D();
        //}
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && this.isHovering(mouseX, mouseY)) {
            this.x2 = this.x - mouseX;
            this.y2 = this.y - mouseY;
            VonwareGui.getClickGui().getComponents().forEach(component -> {
                if (component.drag) {
                    component.drag = false;
                }
            });
            this.drag = true;
            return;
        }
        if (mouseButton == 1 && this.isHovering(mouseX, mouseY)) {
            this.open = !this.open;
            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0f));
            return;
        }
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        if (releaseButton == 0) {
            this.drag = false;
        }
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (!this.open) {
            return;
        }
        this.getItems().forEach(item -> item.onKeyTyped(typedChar, keyCode));
    }

    public void addButton(Button button) {
        this.items.add(button);
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isOpen() {
        return this.open;
    }

    public final ArrayList<Item> getItems() {
        return this.items;
    }

    private boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.getX() && mouseX <= this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY <= this.getY() + this.getHeight() - (this.open ? 2 : 0);
    }

    private float getTotalItemHeight() {
        float height = 0.0f;
        for (Item item : this.getItems()) {
            height += (float) item.getHeight() + 1.5f;
        }
        return height;
    }
}

