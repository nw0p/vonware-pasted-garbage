package fat.vonware.features.gui;

import fat.vonware.Vonware;
import fat.vonware.features.Feature;
import fat.vonware.features.gui.components.Component;
import fat.vonware.features.gui.components.items.Item;
import fat.vonware.features.gui.particle.*;
import fat.vonware.features.gui.components.items.buttons.ModuleButton;
import fat.vonware.features.modules.Module;
import fat.vonware.features.modules.client.ClickGui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class VonwareGui
        extends GuiScreen {
    private static VonwareGui vonwareGui;
    private static VonwareGui INSTANCE;
    private final ParticleSystem particleSystem;
    private ArrayList<Snow> _snowList = new ArrayList<Snow>();

    static {
        INSTANCE = new VonwareGui();
    }


    private final ArrayList<Component> components = new ArrayList();

    public VonwareGui() {
        this.particleSystem = new ParticleSystem(100);
        this.setInstance();
        this.load();
    }

    public static VonwareGui getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new VonwareGui();
        }
        return INSTANCE;
    }

    public static VonwareGui getClickGui() {
        return VonwareGui.getInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    private void load() {
        int x = -84;
        Random random = new Random();
        {
            for (int i = 0; i < 100; ++i) {
                for (int y = 0; y < 3; ++y) {
                    Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2) + 1);
                    _snowList.add(snow);
                }
            }
        }
        for (final Module.Category category : Vonware.moduleManager.getCategories()) {
            this.components.add(new Component(category.getName(), x += 100, 4, true) {

                @Override
                public void setupItems() {
                    counter1 = new int[]{1};
                    Vonware.moduleManager.getModulesByCategory(category).forEach(module -> {
                        if (!module.hidden) {
                            this.addButton(new ModuleButton(module));
                        }
                    });
                }
            });
        }
        this.components.forEach(components -> components.getItems().sort(Comparator.comparing(Feature::getName)));
    }

    public void updateModule(Module module) {
        for (Component component : this.components) {
            for (Item item : component.getItems()) {
                if (!(item instanceof ModuleButton)) continue;
                ModuleButton button = (ModuleButton) item;
                Module mod = button.getModule();
                if (module == null || !module.equals(mod)) continue;
                button.initSettings();
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.checkMouseWheel();
        this.drawDefaultBackground();
        this.components.forEach(components -> components.drawScreen(mouseX, mouseY, partialTicks));
        if (ClickGui.getInstance().particleMode.getValue() == ClickGui.Mode.Normal) {
            this.particleSystem.tick(ClickGui.getInstance().pSpeed.getValue());
            this.particleSystem.render();
        }

        final ScaledResolution res = new ScaledResolution(mc);
        if (!_snowList.isEmpty() && ClickGui.getInstance().particleMode.getValue() == ClickGui.Mode.Snowing) {
            _snowList.forEach(snow -> snow.Update(res));
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        this.components.forEach(components -> components.mouseClicked(mouseX, mouseY, clickedButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        this.components.forEach(components -> components.mouseReleased(mouseX, mouseY, releaseButton));
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    public final ArrayList<Component> getComponents() {
        return this.components;
    }

    public void checkMouseWheel() {
        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            this.components.forEach(component -> component.setY(component.getY() - 10));
        } else if (dWheel > 0) {
            this.components.forEach(component -> component.setY(component.getY() + 10));
        }
    }

    public int getTextOffset() {
        return -6;
    }

    public Component getComponentByName(String name) {
        for (Component component : this.components) {
            if (!component.getName().equalsIgnoreCase(name)) continue;
            return component;
        }
        return null;
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.components.forEach(component -> component.onKeyTyped(typedChar, keyCode));
    }
}

