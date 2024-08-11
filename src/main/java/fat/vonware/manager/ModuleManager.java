package fat.vonware.manager;

import fat.vonware.event.events.Render2DEvent;
import fat.vonware.event.events.Render3DEvent;
import fat.vonware.features.Feature;
import fat.vonware.features.gui.VonwareGui;
import fat.vonware.features.modules.combatz.*;
import fat.vonware.features.modules.exploit.*;
import fat.vonware.features.modules.movement.*;
import fat.vonware.features.modules.client.FontMod;
import fat.vonware.features.modules.Module;
import fat.vonware.features.modules.client.*;
import fat.vonware.features.modules.combat.*;
import fat.vonware.features.modules.misc.*;
import fat.vonware.features.modules.player.*;
import fat.vonware.features.modules.render.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager
        extends Feature {
    public ArrayList<Module> modules = new ArrayList();
    public List<Module> sortedModules = new ArrayList<Module>();

    public List<String> sortedModulesABC = new ArrayList<String>();

    public void init() {
        //Chat
        this.modules.add(new ChatModifier());



        //Client
        this.modules.add(new ClickGui());
        this.modules.add(new HUD());
        this.modules.add(new RPC());
        this.modules.add(new FontMod());
        this.modules.add(new ChatSuffix());
        this.modules.add(new NoUnicodeLag());
        this.modules.add(new VisualRange());
        this.modules.add(new FriendNotify());
        this.modules.add(new ConnectionInfo());

        //5b5t
        this.modules.add(new AutoSand());



        //Combat
        this.modules.add(new FutureFacePlace());
        this.modules.add(new Blocker());
        this.modules.add(new AutoCity());
        this.modules.add(new AntiRegear());
        this.modules.add(new AutoPot());
        this.modules.add(new PistonPush());
        this.modules.add(new Quiver());
        this.modules.add(new Offhand());
        this.modules.add(new Offhand2());



        //Exploit
        this.modules.add(new NewCornerclip());
        this.modules.add(new Cornerclip());
        this.modules.add(new UnicodeLag());
        this.modules.add(new AutoFrameDupe());
        this.modules.add(new Speedmine());


        //Misc
        this.modules.add(new Burrow());
        this.modules.add(new NewBurrow());
        this.modules.add(new NameHider());
        this.modules.add(new PopCounter());
        this.modules.add(new Spammer());
        this.modules.add(new MCF());
        this.modules.add(new SmartDisconnect());
        this.modules.add(new AutoReply());


        //Movement
        this.modules.add(new InstantSpeed());
        this.modules.add(new Anchor());
        this.modules.add(new Holesnap());
        this.modules.add(new Step());
        this.modules.add(new Phase());
        this.modules.add(new FastSwim());
        this.modules.add(new FastWeb());



        //Player
        this.modules.add(new FakePlayer());
        this.modules.add(new XCarry());
        this.modules.add(new FreeLook());
        this.modules.add(new Instamine());
        this.modules.add(new HandSwitch());

        //Render
        this.modules.add(new ESP());
        this.modules.add(new Nametags());
        this.modules.add(new ItemESP());
        this.modules.add(new BurrowESP());
        this.modules.add(new SwingSpeed());
        this.modules.add(new PlayerAnimation());
        this.modules.add(new Skeleton());
        this.modules.add(new NoRender());
        this.modules.add(new CrystalSpawns());
        this.modules.add(new HoleESP());
        this.modules.add(new Ambience());
        this.modules.add(new PopChams());
        this.modules.add(new BlockHighlight());
        this.modules.add(new ShulkerViewer());
        this.modules.add(new SkyColor());
        this.modules.add(new SmallShield());
        this.modules.add(new LogoutSpots());
        this.modules.add(new Weather());
        this.modules.add(new CrystalModifier());
        this.modules.add(new KillEffects());
        this.modules.add(new Trails());
        this.modules.add(new GlintModify());
        this.modules.add(new CrystalChams());
        this.modules.add(new PlayerChams());
        this.modules.add(new Trajectories());
        




    }

    public Module getModuleByName(String name) {
        for (Module module : this.modules) {
            if (!module.getName().equalsIgnoreCase(name)) continue;
            return module;
        }
        return null;
    }



    public <T extends Module> T getModuleByClass(Class<T> clazz) {
        for (Module module : this.modules) {
            if (!clazz.isInstance(module)) continue;
            return (T) module;
        }
        return null;
    }


    public void enableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(Class<Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        if (module != null) {
            module.disable();
        }
    }

    public void enableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.enable();
        }
    }

    public void disableModule(String name) {
        Module module = this.getModuleByName(name);
        if (module != null) {
            module.disable();
        }
    }

    public boolean isModuleEnabled(String name) {
        Module module = this.getModuleByName(name);
        return module != null && module.isOn();
    }

    public boolean isModuleEnabled(Class< ? extends Module> clazz) {
        Module module = this.getModuleByClass(clazz);
        return module != null && module.isOn();
    }

    public Module getModuleByDisplayName(String displayName) {
        for (Module module : this.modules) {
            if (!module.getDisplayName().equalsIgnoreCase(displayName)) continue;
            return module;
        }
        return null;
    }

    public ArrayList<Module> getEnabledModules() {
        ArrayList<Module> enabledModules = new ArrayList<Module>();
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            enabledModules.add(module);
        }
        return enabledModules;
    }

    public ArrayList<String> getEnabledModulesName() {
        ArrayList<String> enabledModules = new ArrayList<String>();
        for (Module module : this.modules) {
            if (!module.isEnabled() || !module.isDrawn()) continue;
            enabledModules.add(module.getFullArrayString());
        }
        return enabledModules;
    }

    public ArrayList<Module> getModulesByCategory(Module.Category category) {
        ArrayList<Module> modulesCategory = new ArrayList<Module>();
        this.modules.forEach(module -> {
            if (module.getCategory() == category) {
                modulesCategory.add(module);
            }
        });
        return modulesCategory;
    }

    public List<Module.Category> getCategories() {
        return Arrays.asList(Module.Category.values());
    }

    public void onLoad() {
        this.modules.stream().filter(Module::listening).forEach(((EventBus) MinecraftForge.EVENT_BUS):: register);
        this.modules.forEach(Module::onLoad);
    }

    public void onUpdate() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onUpdate);
    }

    public void onTick() {
        this.modules.stream().filter(Feature::isEnabled).forEach(Module::onTick);
    }

    public void onRender2D(Render2DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender2D(event));
    }

    public void onRender3D(Render3DEvent event) {
        this.modules.stream().filter(Feature::isEnabled).forEach(module -> module.onRender3D(event));
    }

    public void sortModules(boolean reverse) {
        this.sortedModules = this.getEnabledModules().stream().filter(Module::isDrawn).sorted(Comparator.comparing(module -> this.renderer.getStringWidth(module.getFullArrayString()) * (reverse ? -1 : 1))).collect(Collectors.toList());
    }

    public void sortModulesABC() {
        this.sortedModulesABC = new ArrayList<String>(this.getEnabledModulesName());
        this.sortedModulesABC.sort(String.CASE_INSENSITIVE_ORDER);
    }

    public void onLogout() {
        this.modules.forEach(Module::onLogout);
    }

    public void onLogin() {
        this.modules.forEach(Module::onLogin);
    }

    public void onUnload() {
        this.modules.forEach(MinecraftForge.EVENT_BUS::unregister);
        this.modules.forEach(Module::onUnload);
    }

    public void onUnloadPost() {
        for (Module module : this.modules) {
            module.enabled.setValue(false);
        }
    }


    public void onKeyPressed(int eventKey) {
        if (eventKey == 0 || !Keyboard.getEventKeyState() || ModuleManager.mc.currentScreen instanceof VonwareGui) {
            return;
        }
        this.modules.forEach(module -> {
            if (module.getBind().getKey() == eventKey) {
                module.toggle();
            }
        });
    }
}

